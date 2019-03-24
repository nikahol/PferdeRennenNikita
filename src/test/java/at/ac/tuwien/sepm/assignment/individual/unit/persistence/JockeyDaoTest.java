package at.ac.tuwien.sepm.assignment.individual.unit.persistence;

import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IHorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.IJockeyDao;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class JockeyDaoTest {

    @Autowired
    IJockeyDao jockeyDao;
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
    public void givenNothing_whenFindJockeyByIdWhichNotExists_thenNotFoundException()
        throws PersistenceException, NotFoundException {
        jockeyDao.findOneById(1);
    }


    @Test
    public void insertJockey_whenFoundByID_thenSameValues() throws PersistenceException, NotFoundException{
            Jockey jockey = new Jockey(null, "Jack", 55.0, null, null);
            Jockey returnedJockey = jockeyDao.insertJockey(jockey);
            Jockey jockey2 = jockeyDao.findOneById(returnedJockey.getId());
            assertEquals(jockey2.getName(), returnedJockey.getName());
            assertEquals(jockey2.getId(), returnedJockey.getId());
            assertEquals(jockey2.getSkill(), returnedJockey.getSkill());
            assertEquals(jockey2.getCreated(), returnedJockey.getCreated());
            assertEquals(jockey2.getUpdated(), returnedJockey.getUpdated());
    }



}