package at.ac.tuwien.sepm.assignment.individual.unit.persistence;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IHorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class HorseDaoTest {

    @Autowired
    IHorseDao horseDao;
    @Autowired
    DBConnectionManager dbConnectionManager;

    /**
     * It is important to close the database connection after each test in order to clean the in-memory database
     */
    @After
    public void afterEachTest() throws PersistenceException {
        dbConnectionManager.closeConnection();
    }

    @Test(expected = NotFoundException.class)
    public void givenNothing_whenFindHorseByIdWhichNotExists_thenNotFoundException()
        throws PersistenceException, NotFoundException {
        horseDao.findOneById(1);
    }

    @Test
    public void insertHorse_whenFoundById_thenValueEqual() throws PersistenceException, NotFoundException{
        Horse horse = new Horse( null, "blar", "black lightning", 45.0, 55.0, null, null, false);
        Horse returnedHorse = horseDao.insertHorse(horse);
        Horse horse2 = horseDao.findOneById(returnedHorse.getId());
        assertEquals(horse2.getId(), returnedHorse.getId());
        assertEquals(horse2.getName(), returnedHorse.getName());
        assertEquals(horse2.getBreed(), returnedHorse.getBreed());
        assertEquals(horse2.getMinSpeed(), returnedHorse.getMinSpeed());
        assertEquals(horse2.getMaxSpeed(), returnedHorse.getMaxSpeed());
        assertEquals(horse2.getUpdated(), returnedHorse.getUpdated());
        assertEquals(horse2.getCreated(), returnedHorse.getCreated());
        assertEquals("blar", horse2.getName());
        assertEquals("black lightning", horse2.getBreed());



    }



}

