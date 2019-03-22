package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.entity.Participant;
import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;

import javax.servlet.http.Part;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;

public interface ISimulationDao {
    /**
     * @param  simulation is the simulation we want to insert into the database(simulation table)
     * @return the simulation we inserted into the simulation table
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     * @throws NotFoundException will be thrown if the entry was not entered properly
     */
    Simulation insertSimulation(Simulation simulation) throws PersistenceException, NotFoundException;

    /**
     *
     * @param simID the id of the previously inserted simulation
     * @param participant the Participant to insert
     * @return the participant we just inserted
     * @throws PersistenceException will be thrown if something goes wrong during the database access
     */
    Participant insertParticipant(Integer simID, Participant participant) throws PersistenceException;

    /**
     * @param id of the sim we want to fetch
     * @return A simulation without a participant list with the id in the parameters
     * @throws PersistenceException will be thrown if something goes wrong during the database access
     * @throws NotFoundException will be thrown if the simulation with the requested ID could not be find
     */
    Simulation getOneSimById(Integer id) throws PersistenceException, NotFoundException;

    /**
     * @param id is the simulation id of the participants
     * @param simCreated is the date the simulation was created
     * @return A list of participants who were in the simulation with simID
     * @throws PersistenceException will be thrown if something goes wrong during the database access
     * @throws NotFoundException will be thrown if the simulation with the requested ID could not be found
     */
    ArrayList<Participant> getParticipantListByID(Integer id, LocalDateTime simCreated) throws PersistenceException, NotFoundException;

    /**
     * @return a list of all simulations and their participants
     * @throws PersistenceException will be thrown if something goes wrong during the database access
     */
    LinkedList<Simulation> getAllSimulations() throws PersistenceException;

    /**
     * @param name is a piece of a simulation name we search for
     * @return a list of all simulations and their participants
     * @throws PersistenceException will be thrown if something goes wrong during the database access
     */
    LinkedList<Simulation> getAllSimulationsFiltered(String name) throws PersistenceException;

}
