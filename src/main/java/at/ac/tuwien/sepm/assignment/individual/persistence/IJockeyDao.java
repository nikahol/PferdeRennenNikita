package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;

import java.time.LocalDateTime;
import java.util.LinkedList;

public interface IJockeyDao {
    /**
     * @param id of the jockey to find.
     * @return the jockey with the specified id.
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     * @throws NotFoundException    will be thrown if the jockey could not be found in the database.
     */
    Jockey findOneById(Integer id) throws PersistenceException, NotFoundException;
    /**
     * @param jockey is the jockey we will insert into the database.
     * @return the inserted jockey.
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     * @throws NotFoundException    will be thrown if the jockey could not be found in the database.
     */
    Jockey insertJockey(Jockey jockey) throws PersistenceException;
    /**
     * @param jockey is the jockey we will update in the database.
     * @return the updated jockey.
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     * @throws NotFoundException    will be thrown if the jockey could not be found in the database.
     */
    Jockey updateJockey(Jockey jockey)throws PersistenceException, NotFoundException;
    /**
     * @param id of the jockey to delete.
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     * @throws NotFoundException    will be thrown if the jockey could not be found in the database.
     */
    void deleteJockey(Integer id) throws PersistenceException, NotFoundException;

    /**
     * @return list of all jockeys in the database
     * @throws PersistenceException will be thrown if something goes wrong during data processing.
     */
    LinkedList<Jockey> getAllJockeys() throws PersistenceException;

    /**
     * @param jockey is a container for values to filter by
     * @return list of all jockeys in the database
     * @throws PersistenceException will be thrown if something goes wrong during data processing.
     */
    LinkedList<Jockey> getAllJockeysFiltered(Jockey jockey) throws PersistenceException;

    /**
     * @param id of the jockey that needs new version
     * @param lastUpdated last time jockey was updated
     * @throws NotFoundException if the jockey does not exist in the database
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     */
    void newVersionJockey(Integer id, LocalDateTime lastUpdated) throws NotFoundException, PersistenceException;
}
