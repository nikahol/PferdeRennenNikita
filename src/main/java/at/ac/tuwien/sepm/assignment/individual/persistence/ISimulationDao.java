package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.entity.Participant;
import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;

import javax.servlet.http.Part;

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
     * @throws PersistenceException will be if something goes wrong during the database access
     */
    Participant insertParticipant(Integer simID, Participant participant) throws PersistenceException;
}
