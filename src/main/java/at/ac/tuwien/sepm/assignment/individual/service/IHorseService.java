package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exceptions.BadRequestException;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;

import java.util.LinkedList;

public interface IHorseService {

    /**
     * @param id of the horse to find.
     * @return the horse with the specified id.
     * @throws ServiceException  will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if the horse could not be found in the system.
     */
    Horse findOneById(Integer id) throws ServiceException, NotFoundException;
    /**
     * @param horse is the horse we want to insert into the system
     * @return the horse we inserted
     * @throws ServiceException will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if the horse could not be found in the system
     * @throws BadRequestException will be thrown if the input is not as expected
     */
    Horse insertHorse(Horse horse) throws ServiceException, NotFoundException, BadRequestException;
    /**
     * @param horse is the horse we want to update in the system
     * @return the horse we updated
     * @throws ServiceException will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if the horse could not be found in the system
     * @throws BadRequestException will be thrown if the input is not as expected
     */
    Horse updateHorse(Horse horse) throws ServiceException, NotFoundException, BadRequestException;
    /**
     * @param id is the id of th horse we want to delete in the system
     * @return the horse we deleted
     * @throws ServiceException will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if the horse could not be found in the system
     */
    Horse deleteHorse(Integer id) throws ServiceException, NotFoundException;

    /**
     * @return a list of all horses in the database
     * @throws ServiceException will be thrown if something goes wrong during data processing.
     */
    LinkedList<Horse> getAllHorses() throws ServiceException;

    /**
     * @param horse is a container for the search options
     * @return a list of all horses in the database
     * @throws ServiceException will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if there are no search results
     */
    LinkedList<Horse> getAllHorsesFiltered(Horse horse) throws ServiceException, NotFoundException;

}
