package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Participant;
import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.ISimulationDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;

@Repository
public class SimulationDao implements ISimulationDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationDao.class);
    private final DBConnectionManager dbConnectionManager;

    @Autowired
    public SimulationDao(DBConnectionManager dbConnectionManager) {
        this.dbConnectionManager = dbConnectionManager;
    }

    private static Simulation dbResultToSim(ResultSet result) throws SQLException {
        return new Simulation(
            result.getInt(1),
            result.getString(2),
            result.getTimestamp(3).toLocalDateTime(),
            null);

    }

    private static Participant dbResultToParticipant(ResultSet result) throws SQLException{
       return new Participant(
           result.getInt(1),
           null,
           result.getString(3),
           result.getString(4),
           null,
           result.getInt(2),
           result.getDouble(5),
           roundTo4(((result.getDouble(9) - 0.95) * (result.getDouble(7) - result.getDouble(6))/0.1 + result.getDouble(6))),
           result.getDouble(9),
           roundTo4((1 + ( 0.15 * 1/Math.PI * Math.atan(0.2*result.getDouble(8))))),
           null,
           null
       );
    }

    @Override
    public  ArrayList<Participant> getParticipantListByID(Integer id, LocalDateTime created) throws PersistenceException, NotFoundException{
        String sql = "SELECT s.id, s.rank, h.name, j.name, s.avgSpeed, h.min_speed, h.max_speed, j.skill, s.luck FROM simulationRelation s INNER JOIN jockeyversions j ON  j.id = s.jockeyID INNER JOIN horseversions h  ON h.id = s.horseid WHERE simID = ? AND h.created <= ? AND j.created <= ?";
        ArrayList<Participant> participants = new ArrayList<>();
        try {
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1, id);
            statement.setTimestamp(2,Timestamp.valueOf(created));
            statement.setTimestamp(3,Timestamp.valueOf(created));
            LOGGER.debug("About to execute statement " + statement);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Participant bob = dbResultToParticipant(rs);
                if (participants.isEmpty()) {
                    participants.add(bob);
                } else {
                    boolean added = false;
                    for (int i = 0; i < participants.size(); i++) {
                        if (participants.get(i).getAvgSpeed() < bob.getAvgSpeed()) {
                            participants.add(i, bob);
                            i = participants.size();
                            added = true;
                        }
                    }
                    if(!added){
                        participants.add(bob);
                    }
                }
            }
        }catch(SQLException e){
            LOGGER.error("SQLEXCEPTION GET PARTICIPANTS SIMULATION DAO: Problem getting list of participants" + e.getMessage());
            throw new PersistenceException("Error getting Participant list", e);
        }
        LOGGER.debug("Participant list for simulation " + id + " has been obtained");
        return participants;

    }
    @Override
    public Simulation getOneSimById(Integer id) throws NotFoundException, PersistenceException {
        String sql = "SELECT * FROM simulation WHERE simID = ?";
        Simulation ret = null;
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1,id);
            LOGGER.debug("About to execute statement " + statement);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                ret = dbResultToSim(rs);
            }
        }catch (SQLException e){
            LOGGER.error("SQLEXCEPTION GET ONE BY ID SIMULATION DAO: Problem getting simulation with id " + id);
            throw new PersistenceException("Error finding a sim by id " + id, e);
        }
        if(ret == null){
            LOGGER.error("NOT FOUND GET ONE BY ID SIMULATION DAO: Could not find simulation with id " + id);
            throw new NotFoundException("Could not find sim with id " + id);
        }
        LOGGER.debug("Obtained simulation with id " + id);
        return ret;
    }
    @Override
    public Simulation insertSimulation(Simulation simulation) throws PersistenceException {

        String sql = "INSERT INTO simulation (name, created) VALUES (?, DEFAULT)";
        int id = 0;
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, simulation.getName());
            LOGGER.debug("About to execute statement " + statement);
            statement.execute();
            ResultSet rs = statement.getGeneratedKeys();
            while(rs.next()){
                id = rs.getInt("simID");
            }
            Simulation tmp = getOneSimById(id);
            simulation.setCreated(tmp.getCreated());
            simulation.setId(id);
            LOGGER.info("Inserted simulation into the database " + simulation);
            return simulation;
        }catch (SQLException e){
            LOGGER.error("SQLEXCEPTION INSERT SIMULATION DAO: Problem inserting simulation " + simulation.toString());
            throw new PersistenceException("Error inserting simulation into database + " + simulation, e);
        }catch(NotFoundException e){
            LOGGER.error("NOT FOUND INSERT SIMULATION DAO: Unexpected not found error while creating new simulation " + simulation);
            throw new PersistenceException("Error inserting simulation into database + " + simulation, e);
        }
    }

    @Override
    public LinkedList<Simulation> getAllSimulations() throws PersistenceException{
        String sql = "SELECT * FROM simulation";
        LinkedList<Simulation> simulations = new LinkedList<>();
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            LOGGER.debug("About to execute statement " + statement);

            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                Simulation sim = dbResultToSim(rs);
                sim.setParticipants(getParticipantListByID(sim.getId(),sim.getCreated()));
                simulations.add(sim);
            }
        }catch(SQLException e){
            LOGGER.error("SQLEXCEPTION GET ALL SIMULATION DAO: Problem getting all simulations");
            throw new PersistenceException("Error getting all simulations " + e.getMessage(), e);
        }catch(NotFoundException e){
            LOGGER.error("NOT FOUND GET ALL SIMULATION DAO: Problem getting all simulations");
            throw new PersistenceException("Error finding participants" + e.getMessage(),e );
        }
        LOGGER.info("Got all simulations");
        return simulations;
    }

    @Override
    public LinkedList<Simulation> getAllSimulationsFiltered(String name) throws PersistenceException{
        String sql = "SELECT * FROM simulation WHERE name LIKE ?";
        LinkedList<Simulation> simulations = new LinkedList<>();
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setString(1,"%" + name + "%");
            LOGGER.debug("About to execute statement " + statement);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                Simulation sim = dbResultToSim(rs);
                sim.setParticipants(getParticipantListByID(sim.getId(),sim.getCreated()));
                simulations.add(sim);
            }
        }catch(SQLException e){
            LOGGER.error("SQLEXCEPTION GET ALL FILTERED SIMULATION DAO: Problem getting all simulations, filtered by " + name);
            throw new PersistenceException("Error getting all simulations " + e.getMessage(), e);
        }catch(NotFoundException e){
            LOGGER.error("NOT FOUND GET ALL FILTERED SIMULATION DAO: Problem getting all simulations, filtered by " + name);
            throw new PersistenceException("Error finding participants" + e.getMessage(), e);
        }
        LOGGER.info("Got all simulation where name like " + name);
        return simulations;
    }
    @Override
    public Participant insertParticipant(Integer simID, Participant participant) throws PersistenceException{
        String sql = "INSERT INTO simulationRelation (simID, horseID, horseUp, jockeyID, jockeyUp, rank, avgSpeed, luck) VALUES (?,?,?,?,?,?,?,?)";
        int id = 0;
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, simID);
            statement.setInt(2,participant.getHorseId());
            statement.setTimestamp(3,Timestamp.valueOf(participant.getHorseUpdate()));
            statement.setInt(4,participant.getJockeyId());
            statement.setTimestamp(5,Timestamp.valueOf(participant.getJockeyUpdate()));
            statement.setInt(6, participant.getRank());
            statement.setDouble(7,participant.getAvgSpeed());
            statement.setDouble(8,participant.getLuck());
            LOGGER.debug("About to execute statement " + statement);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            while(rs.next()){
                id = rs.getInt("id");
            }
            participant.setId(id);
            LOGGER.debug("Inserted Participant " + participant.getId() );
            return participant;
        } catch(SQLException e){
            LOGGER.error("SQLEXCEPTION INSERT PARTICIPANT SIMULATION DAO: Problem inserting participant belonging to simulation " + id + " with attributes " + participant.toString());
            throw new PersistenceException("Error inserting participant into database " + participant);
        }
    }

    private static double roundTo4(double r){
        double tmp = r * 10000;
        tmp = Math.round(tmp);
        return (tmp/10000);
    }

}
