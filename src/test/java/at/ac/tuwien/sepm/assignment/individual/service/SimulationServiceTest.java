package at.ac.tuwien.sepm.assignment.individual.service;


import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.entity.Participant;
import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.exceptions.BadRequestException;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IHorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.IJockeyDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

import javax.management.BadAttributeValueExpException;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.LinkedList;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class SimulationServiceTest {

    @Autowired
    ISimulationService simulationService;
    @Autowired
    IHorseDao horseDao;
    @Autowired
    IJockeyDao jockeyDao;
    @Autowired
    DBConnectionManager dbConnectionManager;

    Jockey jockey = new Jockey(null, "Jack", 55.0,  null, null);
    Horse horse = new Horse( null, "blar", "black lightning", 45.0, 55.0,  null, null,  false);
    Jockey jockey2 = new Jockey(null, "Rock", 728.8,  null, null);
    Horse horse2 = new Horse( null, "bor", "red", 42.0, 57.0,  null, null,  false);
    Jockey jockey3 = new Jockey(null, "borat", 421.21, null, null);
    Horse horse3 = new Horse(null, "ewe", "blue streak", 46.0, 51.0, null, null, false);


    @After
    public void afterEachTest() throws PersistenceException {
        dbConnectionManager.closeConnection();
    }

    @Test
    public void checkSimMath()throws PersistenceException, ServiceException, BadRequestException, NotFoundException{
        Horse returnedHorse = horseDao.insertHorse(horse);
        Jockey returnedJockey =jockeyDao.insertJockey(jockey);


        Simulation sim = new Simulation(null, "testSim,", null, null);
        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(new Participant(null, returnedHorse.getId(), null, null, returnedJockey.getId(), null, null, null, 1.03, null, null, null));
        sim.setParticipants(participants);
        Simulation returnedSim = simulationService.newSimulation(sim);
        double pair1P = (1.03 - 0.95) * (horse.getMaxSpeed() - horse.getMinSpeed())/(1.05 -0.95) + horse.getMinSpeed();
        pair1P = roundTo4(pair1P);
        double pair1K = 1 + (0.15 * 1/Math.PI * Math.atan(0.2 * jockey.getSkill()));
        pair1K = roundTo4(pair1K);
        double pair1d = pair1P * pair1K * 1.03;
        pair1d = roundTo4(pair1d);
        double resultD = returnedSim.getParticipants().get(0).getAvgSpeed();
        double resultP = returnedSim.getParticipants().get(0).getHorseSpeed();
        double reusltK = returnedSim.getParticipants().get(0).getSkill();
        assertEquals(resultD, pair1d, 0.00);
        assertEquals(reusltK, pair1K, 0.00);
        assertEquals(resultP, pair1P, 0.00);


    }

    @Test
    public void testSimRankings()throws PersistenceException, ServiceException, NotFoundException, BadRequestException{
        Horse returnedHorse = horseDao.insertHorse(horse);
        Jockey returnedJockey =jockeyDao.insertJockey(jockey);
        Horse returnedHorse2 = horseDao.insertHorse(horse2);
        Jockey returnedJockey2 =jockeyDao.insertJockey(jockey2);
        Horse returnedHorse3 = horseDao.insertHorse(horse3);
        Jockey returnedJockey3 =jockeyDao.insertJockey(jockey3);

        Simulation sim = new Simulation(null, "testSim,", null, null);
        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(new Participant(null, returnedHorse.getId(), null, null, returnedJockey.getId(), null, null, null, 1.03, null, null, null));
        participants.add(new Participant(null, returnedHorse2.getId(), null, null, returnedJockey2.getId(), null, null, null, 0.97, null, null, null));
        participants.add(new Participant(null, returnedHorse3.getId(), null, null, returnedJockey3.getId(), null, null, null, 1.0, null, null, null));

        sim.setParticipants(participants);
        Simulation returnedSim = simulationService.newSimulation(sim);
        assertEquals(returnedSim.getParticipants().get(0).getHorseId(), returnedHorse.getId());
        assertEquals(returnedSim.getParticipants().get(1).getHorseId(), returnedHorse3.getId());
        assertEquals(returnedSim.getParticipants().get(2).getHorseId(), returnedHorse2.getId());

    }

    @Test(expected = BadRequestException.class)
    public void testLuckValidation()throws PersistenceException, ServiceException, NotFoundException, BadRequestException{
        Horse returnedHorse = horseDao.insertHorse(horse);
        Jockey returnedJockey =jockeyDao.insertJockey(jockey);
        Simulation sim = new Simulation(null, "testSim,", null, null);
        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(new Participant(null, returnedHorse.getId(), null, null, returnedJockey.getId(), null, null, null, 1.06, null, null, null));
        sim.setParticipants(participants);
        Simulation returnedSim = simulationService.newSimulation(sim);
    }

    @Test
    public void testGetValues()throws PersistenceException, ServiceException, NotFoundException, BadRequestException{
        Horse returnedHorse = horseDao.insertHorse(horse);
        Jockey returnedJockey =jockeyDao.insertJockey(jockey);


        Simulation sim = new Simulation(null, "testSim,", null, null);
        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(new Participant(null, returnedHorse.getId(), null, null, returnedJockey.getId(), null, null, null, 1.03, null, null, null));

        sim.setParticipants(participants);
        Simulation returnedSim = simulationService.newSimulation(sim);
        Simulation foundSim = simulationService.getSimulationByID(returnedSim.getId());
        assertEquals(foundSim.getParticipants().get(0).getAvgSpeed(), calcD(horse, jockey, 1.03), 0.0);

    }
    private static double calcD(Horse horse, Jockey jockey, double luck){
        double pair1P = (luck - 0.95) * (horse.getMaxSpeed() - horse.getMinSpeed())/(1.05 -0.95) + horse.getMinSpeed();
        pair1P = roundTo4(pair1P);
        double pair1K = 1 + (0.15 * 1/Math.PI * Math.atan(0.2 * jockey.getSkill()));
        pair1K = roundTo4(pair1K);
        double pair1d = pair1P * pair1K * luck;
        pair1d = roundTo4(pair1d);
        return pair1d;
    }
    private static double roundTo4(double r){
        double tmp = r * 10000;
        tmp = Math.round(tmp);
        return (tmp/10000);
    }

    /*@Test
    public void putSimulation_checkResultsMatchExpected()throws ServiceException, BadRequestException, NotFoundException {
        Simulation sim = new Simulation(null, "testSim,", null, null);
        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(new Participant(null, 2, null, null, 4, null, null, null, 1.0, null, null, null));
        participants.add(new Participant(null, 5, null, null, 5, null, null, null, 0.95, null, null, null));
        participants.add(new Participant(null, 6, null, null, 7, null, null, null, 1.05, null, null, null));

        sim.setParticipants(participants);

        Simulation returnedSim = simulationService.newSimulation(sim);

        Simulation expected = new Simulation(null, "testSim", null, null);
        ArrayList<Participant> expectedParticipants = new ArrayList<>();
        expectedParticipants.add(new Participant(null, 2,"Lisa", "jockey1", 4, 2, 48.751, 45.6, 1.0, 1.0691,null, null));
        expectedParticipants.add(new Participant(null, 6,"horse3", "Bob Marley", 6, 1, 67.3911, 60.0, 1.05, 1.0697,null, null));
        expectedParticipants.add(new Participant(null, 5,"horse2", "jockey2", 5, 3, 41.1336, 40.5, 0.95, 1.0691,null, null));
        expected.setParticipants(expectedParticipants);

        for(Participant x : returnedSim.getParticipants()){
            x.setJockeyUpdate(null);
            x.setHorseUpdate(null);
            x.setId(null);
        }
        returnedSim.setId(null);
        returnedSim.setCreated(null);

        assertEquals(returnedSim, expected);
    }*/

}
