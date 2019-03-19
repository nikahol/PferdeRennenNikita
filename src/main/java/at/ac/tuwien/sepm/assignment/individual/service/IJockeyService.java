package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.exceptions.BadRequestException;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;

public interface IJockeyService {
    /**
        * @param id of the jockey to find.
        * @return the jockey with the specified id.
        * @throws ServiceException  will be thrown if something goes wrong during data processing.
        * @throws NotFoundException will be thrown if the jockey could not be found in the system.
     */
    Jockey findOneById(Integer id) throws ServiceException, NotFoundException;
    /**
     * @param jockey is the jockey we want to insert into the system
     * @return the jockey we inserted
     * @throws ServiceException will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if the jockey could not be found in the system
     */
    Jockey insertJockey(Jockey jockey) throws ServiceException, NotFoundException;

    /**
     * @param jockey is the jockey we want to update in the system
     * @return the jockey we updated
     * @throws ServiceException will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if the jockey could not be found in the system
     * @throws BadRequestException will be thrown if some inputs are not accepted
     */
    Jockey updateJockey(Jockey jockey) throws ServiceException, NotFoundException;


}
