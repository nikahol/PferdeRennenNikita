package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.exceptions.BadRequestException;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;

public interface ISimulationService {


    /**
     * @param simulation container for information regarding the simulation
     * @return all of the calculated values for the simulation
     * @throws ServiceException will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if a jockey or horse could not be found in the system
     * @throws BadRequestException will be thrown if some inputs are not accepted
     */
     Simulation newSimulation(Simulation simulation) throws ServiceException, NotFoundException, BadRequestException;

}
