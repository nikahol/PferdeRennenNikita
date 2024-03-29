package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.exceptions.BadRequestException;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;

import java.util.LinkedList;

public interface IJockeyService {
    /**
        * @param id of the jockey to find.
        * @return the jockey with the specified id.
        * @throws ServiceException  will be thrown if something goes wrong during data processing.
     */
    Jockey findOneById(Integer id) throws ServiceException, NotFoundException;
    /**
     * @param jockey is the jockey we want to insert into the system
     * @return the jockey we inserted
     * @throws ServiceException will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if the jockey could not be found in the system
     */
    Jockey insertJockey(Jockey jockey) throws ServiceException;

    /**
     * @param jockey is the jockey we want to update in the system
     * @return the jockey we updated
     * @throws ServiceException will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if the jockey could not be found in the system
     * @throws BadRequestException will be thrown if some inputs are not accepted
     */
    Jockey updateJockey(Jockey jockey) throws ServiceException, NotFoundException;
    /**
     * @param id is the id of the jockey we want to delete from the system
     * @throws ServiceException will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if the jockey could not be found in the system
     */
    void deleteJockey(Integer id) throws ServiceException, NotFoundException;

    /**
     * @return list of all jockeys in the database
     * @throws ServiceException will be thrown if something goes wrong during data processing.
     */
    LinkedList<Jockey> getAllJockeys() throws ServiceException;

    /**
     * @param jockey is a container for the information by which the search should be filtered
     * @return a list of all jockeys matching search criteria
     * @throws ServiceException will be thrown if something goes wrong during data processing
     */
    LinkedList<Jockey> getAllJockeysFiltered(Jockey jockey) throws ServiceException;


}
