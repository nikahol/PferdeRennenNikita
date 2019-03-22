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


import java.text.Normalizer;
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
        LOGGER.info("Getting all simulations");
        try{
            return simulationDao.getAllSimulations();
        }catch(PersistenceException e){
            throw new ServiceException("Error getting all horses " + e.getMessage(), e);
        }
    }

    @Override
    public LinkedList<Simulation> getAllSimulationsFiltered(String name) throws ServiceException {
        LOGGER.info("Getting all simulations filtered by " + name);
        try{
            return simulationDao.getAllSimulationsFiltered(name);
        }catch(PersistenceException e){
            throw new ServiceException("Error getting all horses " + e.getMessage(), e);
        }
    }

    @Override
    public Simulation getSimulationByID(Integer id) throws ServiceException, NotFoundException{
        LOGGER.info("Gettting one simulation by id in service layer");
        Simulation ret = null;
        try{
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
        LOGGER.info("New Simulation in the Service Layer" + simulation);
        validateSimulation(simulation);

        try{
            LinkedList<JockeyHorse> jockeyHorses = new LinkedList<>();
            for(Participant participant : simulation.getParticipants()){
                jockeyHorses.add(new JockeyHorse(horseDao.findOneById(participant.getHorseId()), jockeyDao.findOneById(participant.getJockeyId()), participant.getLuck()));
            }
            ArrayList<Participant> results = raceSim(jockeyHorses);
            Simulation inserted = simulationDao.insertSimulation(simulation);
            for(Participant x: results){
                horseDao.newVersionHorse(x.getHorseId(), x.getHorseUpdate());
                jockeyDao.newVersionJockey(x.getJockeyId(), x.getJockeyUpdate());
                x.setId(simulationDao.insertParticipant(inserted.getId(), x).getId());
            }
            inserted.setParticipants(results);
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
            Participant bob = new Participant(null, horseRider.getHorse().getId(), horseRider.getHorse().getName(), horseRider.getJockey().getName(), horseRider.getJockey().getId(),null,d, p, g, k, horseRider.getHorse().getUpdated(), horseRider.getJockey().getUpdated());
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
        if(simulation.getName() == null || simulation.getName().isEmpty()){
            throw new BadRequestException("Simulation Name cannot be empty " + simulation);
        }
        if(simulation.getParticipants() == null || simulation.getParticipants().isEmpty()){
            throw new BadRequestException("Simulation needs participants " + simulation);
        }
        LinkedList<Integer> jockeyParticipants = new LinkedList<>();
        LinkedList<Integer> horseParticipants = new LinkedList<>();
        for(Participant p : simulation.getParticipants()){
            if(p.getHorseId() == null || p.getJockeyId() == null || p.getLuck() == null){
                throw new BadRequestException("Participant (" + p.getHorseId() + "," + p.getJockeyId() +","+ p.getLuck() + ") does not have one of the required parameters filled out");
            }
            if(p.getLuck() > 1.05 || p.getLuck() < 0.95){
                throw new BadRequestException("Luck values need to be between 0.95 and 1.05");
            }
            if(jockeyParticipants.contains(p.getJockeyId())){
                throw new BadRequestException("The same Jockey cannot participate in the Race multiple times");
            }else{
                jockeyParticipants.add(p.getJockeyId());
            }
            if(horseParticipants.contains(p.getHorseId())){
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
