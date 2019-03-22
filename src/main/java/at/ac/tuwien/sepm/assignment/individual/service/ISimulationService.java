package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.exceptions.BadRequestException;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;

import java.util.LinkedList;

public interface ISimulationService {


    /**
     * @param simulation container for information regarding the simulation
     * @return all of the calculated values for the simulation
     * @throws ServiceException will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if a jockey or horse could not be found in the system
     * @throws BadRequestException will be thrown if some inputs are not accepted
     */
     Simulation newSimulation(Simulation simulation) throws ServiceException, NotFoundException, BadRequestException;

    /**
     * @param id of the simulation we want to get
     * @return the simulation requested
     * @throws ServiceException will be thrown if something goes wrong during data processing
     * @throws NotFoundException will be thrown if the simulation could not be found
     */
     Simulation getSimulationByID(Integer id) throws ServiceException, NotFoundException;

    /**
     * @return a list of all simulations in the database
     * @throws ServiceException will be thrown if something goes wrong during data processing
     * @throws NotFoundException will be thrown if the simulation could not be found
     */
     LinkedList<Simulation> getAllSimulations() throws ServiceException;

    /**
     * @param name a string to search sim names for
     * @return a list of all simulations in the database
     * @throws ServiceException will be thrown if something goes wrong during data processing
     * @throws NotFoundException will be thrown if the simulation could not be found
     */
     LinkedList<Simulation> getAllSimulationsFiltered(String name) throws ServiceException;

}
