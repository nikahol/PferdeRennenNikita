package at.ac.tuwien.sepm.assignment.individual.unit.persistence;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.entity.Participant;
import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IHorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.IJockeyDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.ISimulationDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class SimulationDaoTest {

    @Autowired
    ISimulationDao simulationDao;
    @Autowired
    DBConnectionManager dbConnectionManager;

    @Autowired
    IHorseDao horseDao;
    @Autowired
    IJockeyDao jockeyDao;

    /**
     * It is important to close the database connection after each test in order to clean the in-memory database
     */
    @After
    public void afterEachTest() throws PersistenceException {
        dbConnectionManager.closeConnection();
    }

    @Test(expected = NotFoundException.class)
    public void givenNothing_whenFindSimulationByIdWhichNotExists_thenNotFoundException()
        throws PersistenceException, NotFoundException {
        simulationDao.getOneSimById(1);
    }


    @Test
    public void insertSimulation_whenFoundByID_thenSameValues() throws PersistenceException, NotFoundException{
        Jockey jockey = new Jockey(null, "Jack", 55.0,  null, null);
        Horse horse = new Horse( null, "blar", "black lightning", 45.0, 55.0,  null, null,  false);
        Horse returnedHorse = horseDao.insertHorse(horse);
        Jockey returnedJockey = jockeyDao.insertJockey(jockey);
        horseDao.newVersionHorse(returnedHorse.getId(), returnedHorse.getUpdated());
        jockeyDao.newVersionJockey(returnedJockey.getId(), returnedJockey.getUpdated());
        Participant participant = new Participant(null, returnedHorse.getId(), returnedHorse.getName(), returnedJockey.getName(), returnedJockey.getId(), 1, 55.0, 50.0 , 1.0, 1.0442, returnedHorse.getUpdated(), returnedJockey.getUpdated());
        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(participant);
        at.ac.tuwien.sepm.assignment.individual.entity.Simulation simulation = new Simulation(null, "testSim", null, participants);
        Simulation returnedSimulation = simulationDao.insertSimulation(simulation);
        Participant returnedParticipant = simulationDao.insertParticipant(returnedSimulation.getId(), participant);

        ArrayList<Participant> participants1 = new ArrayList<>();
        participants1.add(returnedParticipant);
        returnedSimulation.setParticipants(participants1);


        Simulation found = simulationDao.getOneSimById(returnedSimulation.getId());
        found.setParticipants(simulationDao.getParticipantListByID(found.getId(), found.getCreated()));


        returnedSimulation.getParticipants().get(0).setHorseId(null);
        returnedSimulation.getParticipants().get(0).setJockeyId(null);
        returnedSimulation.getParticipants().get(0).setHorseUpdate(null);
        returnedSimulation.getParticipants().get(0).setJockeyUpdate(null);
        found.getParticipants().get(0).setSkill(1.0442);

        //System.out.println("Found Pid " + found.getParticipants().get(0).getHorseId() + " returnedID " + returnedSimulation.getParticipants().get(0).getHorseId() );
        assertEquals(found, returnedSimulation);


    }



}