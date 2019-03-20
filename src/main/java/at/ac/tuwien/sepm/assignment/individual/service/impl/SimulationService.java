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

import java.util.HashMap;
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
    public Simulation newSimulation(Simulation simulation) throws ServiceException, NotFoundException, BadRequestException{
        LOGGER.info("New Simulation in the Service Layer" + simulation);
        validateSimulation(simulation);

        try{
            LinkedList<JockeyHorse> jockeyHorses = new LinkedList<>();
            for(Participant participant : simulation.getParticipants()){
                jockeyHorses.add(new JockeyHorse(horseDao.findOneById(participant.getHorseId()), jockeyDao.findOneById(participant.getJockeyId())));
            }


        }catch (PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
        return simulation;
    }

    private void validateSimulation(Simulation simulation) throws  BadRequestException{
        if(simulation.getName() == null || simulation.getName().isEmpty()){
            throw new BadRequestException("Simulation Name cannot be empty " + simulation);
        }
        if(simulation.getParticipants() == null || simulation.getParticipants().isEmpty()){
            throw new BadRequestException("Simulation needs participants " + simulation);
        }
        for(Participant p : simulation.getParticipants()){
            if(p.getHorseId() == null || p.getJockeyId() == null || p.getLuck() == null){
                throw new BadRequestException("Participant (" + p.getHorseId() + "," + p.getJockeyId() +","+ p.getLuck() + ") does not have one of the required parameters filled out");
            }
        }
        LinkedList<Integer> jockeyParticipants = new LinkedList<>();
        LinkedList<Integer> horseParticipants = new LinkedList<>();
        for(Participant participant : simulation.getParticipants()){
            if(jockeyParticipants.contains(participant.getJockeyId())){
                throw new BadRequestException("The same Jockey cannot participate in the Race multiple times");
            }else{
                jockeyParticipants.add(participant.getJockeyId());
            }
            if(horseParticipants.contains(participant.getHorseId())){
                throw new BadRequestException("The same Horse cannot participate in the Race multiple times");
            }else{
                horseParticipants.add(participant.getHorseId());
            }
        }
    }
}
