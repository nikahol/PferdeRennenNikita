package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.JockeyHorse;
import at.ac.tuwien.sepm.assignment.individual.entity.Participant;
import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.exceptions.BadRequestException;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IHorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.IJockeyDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.ISimulationDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.service.ISimulationService;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.LinkedList;

@Service
public class SimulationService implements ISimulationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationService.class);
    private final IHorseDao horseDao;
    private final IJockeyDao jockeyDao;
    private final ISimulationDao simulationDao;

    @Autowired
    public SimulationService(IHorseDao horseDao, IJockeyDao jockeyDao, ISimulationDao simulationDao){
        this.horseDao = horseDao;
        this.jockeyDao = jockeyDao;
        this.simulationDao = simulationDao;
    }

    @Override
    public LinkedList<Simulation> getAllSimulations() throws ServiceException {
        try{
            LOGGER.debug("Attempting to get all simulations. Currently in service");
            return simulationDao.getAllSimulations();
        }catch(PersistenceException e){
            throw new ServiceException("Error getting all horses " + e.getMessage(), e);
        }
    }

    @Override
    public LinkedList<Simulation> getAllSimulationsFiltered(String name) throws ServiceException {
        try{
            LOGGER.debug("Attempting to get all simulations where name is like " + name + ". Currently in service");
            return simulationDao.getAllSimulationsFiltered(name);
        }catch(PersistenceException e){
            throw new ServiceException("Error getting all horses " + e.getMessage(), e);
        }
    }

    @Override
    public Simulation getSimulationByID(Integer id) throws ServiceException, NotFoundException{

        Simulation ret = null;
        try{
            LOGGER.debug("Attempting to get simulation with id " + id +". Currently in service");
            ret = simulationDao.getOneSimById(id);
            LOGGER.debug("Simulation fetched: " + ret.toString());
            ArrayList<Participant> participants = simulationDao.getParticipantListByID(id, ret.getCreated());
            ret.setParticipants(participants);
            return ret;
        }catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Simulation newSimulation(Simulation simulation) throws ServiceException, NotFoundException, BadRequestException{
        LOGGER.debug("Attempting to create a new simulation based on information " + simulation.toString() + ". Currently in service");
        validateSimulation(simulation);
        try{
            LinkedList<JockeyHorse> jockeyHorses = new LinkedList<>();
            for(Participant participant : simulation.getParticipants()){
                jockeyHorses.add(new JockeyHorse(horseDao.findOneById(participant.getHorseId()), jockeyDao.findOneById(participant.getJockeyId()), participant.getLuck()));
            }
            LOGGER.debug("Able to fetch all horse/jockey pairs for new simulation");
            ArrayList<Participant> results = raceSim(jockeyHorses);
            Simulation inserted = simulationDao.insertSimulation(simulation);
            for(Participant x: results){
                horseDao.newVersionHorse(x.getHorseId(), x.getHorseUpdate());
                jockeyDao.newVersionJockey(x.getJockeyId(), x.getJockeyUpdate());
                x.setId(simulationDao.insertParticipant(inserted.getId(), x).getId());
            }
            inserted.setParticipants(results);
            LOGGER.info("Inserted new simulation into the database");
            return inserted;
        }catch (PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }

    }

    private ArrayList<Participant> raceSim(LinkedList<JockeyHorse> participants){
        ArrayList<Participant> calculated = new ArrayList<>();
        LOGGER.debug(" # of Participants entering the race: " + participants.size() + " ");
        for(JockeyHorse horseRider: participants){
            LOGGER.debug("Processing Participant " + horseRider.getJockey().getId() + " with horse " + horseRider.getHorse().getId());
            double pmin = horseRider.getHorse().getMinSpeed();
            double pmax = horseRider.getHorse().getMaxSpeed();
            double k = horseRider.getJockey().getSkill();
            double g = horseRider.getLuck();
            double p = roundTo4((g - 0.95) * (pmax-pmin)/(1.05-0.95) + pmin);
            double ka = roundTo4(1 + (0.15 * 1/Math.PI * Math.atan(0.2 * k)));
            double d = roundTo4(p * ka * g);
            Participant bob = new Participant(
                null,
                horseRider.getHorse().getId(),
                horseRider.getHorse().getName(),
                horseRider.getJockey().getName(),
                horseRider.getJockey().getId(),
                null,
                d,
                p,
                g,
                ka,
                horseRider.getHorse().getUpdated()
                ,
                horseRider.getJockey().getUpdated()
            );
            LOGGER.debug("A participant in the race has been processed. " + bob.toString());
            if(calculated.isEmpty()){
                calculated.add(bob);
            }else{
                boolean added = false;
                for(int i = 0; i < calculated.size(); i++){
                    if(calculated.get(i).getAvgSpeed() < d){
                        calculated.add((i), bob);
                        i=calculated.size();
                        added = true;
                    }
                }
                if(!added){
                    calculated.add(bob);
                }
            }

            for(int i = 0; i < calculated.size(); i++){
                calculated.get(i).setRank(i+1);
            }
        }
        LOGGER.debug("Size of calculated " + calculated.size() + " isEqual: " + (calculated.size() == participants.size()));
        return calculated;
    }

    private void validateSimulation(Simulation simulation) throws  BadRequestException{
        LOGGER.debug("Attempting to validate new simulation input");
        if(simulation.getName() == null || simulation.getName().isEmpty()){
            LOGGER.error("BAD REQUEST NEW SIMULATION SERVICE: Name can neither be neither null nor empty. name " + simulation.getName());
            throw new BadRequestException("Simulation Name cannot be empty " + simulation);
        }
        if(simulation.getParticipants() == null){
            simulation.setParticipants(new ArrayList<Participant>());
        }
        LinkedList<Integer> jockeyParticipants = new LinkedList<>();
        LinkedList<Integer> horseParticipants = new LinkedList<>();
        for(Participant p : simulation.getParticipants()){
            if(p.getHorseId() == null || p.getJockeyId() == null || p.getLuck() == null){
                LOGGER.error("BAD REQUEST NEW SIMULATION SERVICE: a participant in the simulation was missing a value: horse id " + p.getHorseId() + ", jockeyId " + p.getJockeyId() + ", luck factor " + p.getLuck() );
                throw new BadRequestException("Participant (" + p.getHorseId() + "," + p.getJockeyId() +","+ p.getLuck() + ") does not have one of the required parameters filled out");
            }
            if(p.getLuck() > 1.05 || p.getLuck() < 0.95){
                LOGGER.error("BAD REQUEST NEW SIMULATION SERVICE: Luck factor must be between 0.95 and 1.05. luck = " + p.getLuck());
                throw new BadRequestException("Luck values need to be between 0.95 and 1.05");
            }
            if(jockeyParticipants.contains(p.getJockeyId())){
                LOGGER.error("BAD REQUEST NEW SIMULATION SERVICE: a jockey cannot participate multiple times in the same simulation. repeating jockey id: " + p.getJockeyId());
                throw new BadRequestException("The same Jockey cannot participate in the Race multiple times");
            }else{
                jockeyParticipants.add(p.getJockeyId());
            }
            if(horseParticipants.contains(p.getHorseId())){
                LOGGER.error("BAD REQUEST NEW SIMULATION SERVICE: a horse cannot participate multiple times in the same simulation. repeating horse id: " + p.getHorseId());
                throw new BadRequestException("The same Horse cannot participate in the Race multiple times");
            }else{
                horseParticipants.add(p.getHorseId());
            }
        }
    }

    private double roundTo4(double r){
        double tmp = r * 10000;
        tmp = Math.round(tmp);
        return (tmp/10000);
    }
}
