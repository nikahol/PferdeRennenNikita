package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;

import java.time.LocalDateTime;
import java.util.LinkedList;

public interface IHorseDao {

    /**
     * @param id of the horse to find.
     * @return the horse with the specified id.
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     * @throws NotFoundException    will be thrown if the horse could not be found in the database.
     */
    Horse findOneById(Integer id) throws PersistenceException, NotFoundException;
    /**
     * @param horse is the horse we want to insert into the database
     * @return the horse we inserted
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     * @throws NotFoundException    will be thrown if the horse could not be found in the database.
     */
    Horse insertHorse(Horse horse) throws PersistenceException, NotFoundException;
    /**
     * @param horse is the horse we want to update in the database
     * @return the horse after updates
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     * @throws NotFoundException    will be thrown if the horse could not be found in the database.
     */
    Horse updateHorse(Horse horse) throws PersistenceException, NotFoundException;
    /**
     * @param id is the id of the horse we want to delete in the database
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     * @throws NotFoundException    will be thrown if the horse could not be found in the database.
     */
    void deleteHorse(Integer id) throws PersistenceException, NotFoundException;

    /**
     * @return a list of all horses in the database
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     */
    LinkedList<Horse> getAllHorses() throws PersistenceException;
    /**
     * @param horse is a container for search parameters
     * @return a list of horses matching search criteria
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     */
    LinkedList<Horse> getAllHorsesFiltered(Horse horse) throws PersistenceException;

    /**
     * @param id of the horse that needs new version
     * @param horseUpdate last time horse was updated
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     * @throws NotFoundException if the horse does not exist in the database
     */
    void newVersionHorse(Integer id, LocalDateTime horseUpdate) throws PersistenceException, NotFoundException;
}
