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

import javax.xml.transform.Result;
import java.nio.file.LinkOption;
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
           ((result.getDouble(9) - 0.95) * (result.getDouble(7) - result.getDouble(6))/0.1 + result.getDouble(6)),
           result.getDouble(9),
           (1 + ( 0.15 * 1/Math.PI * Math.atan(0.2*result.getDouble(8)))),
           null,
           null
       );
    }

    @Override
    public  ArrayList<Participant> getParticipantListByID(Integer id, LocalDateTime created) throws PersistenceException, NotFoundException{
        LOGGER.info("Getting Participant List with sim id: " + id);
        String sql = "SELECT s.id, s.rank, h.name, j.name, s.avgSpeed, h.min_speed, h.max_speed, j.skill, s.luck FROM simulationRelation s INNER JOIN jockeyversions j ON  j.id = s.jockeyID INNER JOIN horseversions h  ON h.id = s.horseid WHERE simID = ? AND h.created <= ? AND j.created <= ?";
        ArrayList<Participant> participants = new ArrayList<>();
        try {
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1, id);
            statement.setTimestamp(2,Timestamp.valueOf(created));
            statement.setTimestamp(3,Timestamp.valueOf(created));
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
            throw new PersistenceException("Error getting Participant list");
        }
        if(participants.isEmpty()){
            throw new NotFoundException("Could not find any participants in simulation with id " + id);
        }
        return participants;

    }
    @Override
    public Simulation getOneSimById(Integer id) throws NotFoundException, PersistenceException {
        LOGGER.info("Getting one sim no race info by ID " + id);
        String sql = "SELECT * FROM simulation WHERE simID = ?";
        Simulation ret = null;
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                ret = dbResultToSim(rs);
            }
        }catch (SQLException e){
            throw new PersistenceException("Error finding a sim by id " + id, e);
        }
        if(ret == null){
            throw new NotFoundException("Could not find sim with id " + id);
        }
        return ret;
    }
    @Override
    public Simulation insertSimulation(Simulation simulation) throws PersistenceException, NotFoundException {
        LOGGER.info("Inserting new Sim into the database " + simulation);
        String sql = "INSERT INTO simulation (name, created) VALUES (?, DEFAULT)";
        int id = 0;
        LocalDateTime created = null;
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, simulation.getName());
            statement.execute();
            ResultSet rs = statement.getGeneratedKeys();
            while(rs.next()){
                id = rs.getInt("simID");
            }
            Simulation tmp = getOneSimById(id);
            simulation.setCreated(tmp.getCreated());
            simulation.setId(id);

            return simulation;
        }catch (SQLException e){
            throw new PersistenceException("Error inserting simulation into database + " + simulation);
        }
    }

    @Override
    public LinkedList<Simulation> getAllSimulations() throws PersistenceException{
        LOGGER.info("Getting all Simulations in DAO");
        String sql = "SELECT * FROM simulation";
        LinkedList<Simulation> simulations = new LinkedList<>();
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                Simulation sim = dbResultToSim(rs);
                sim.setParticipants(getParticipantListByID(sim.getId(),sim.getCreated()));
                simulations.add(sim);
            }
        }catch(SQLException e){
            throw new PersistenceException("Error getting all simulations " + e.getMessage());
        }catch(NotFoundException e){
            throw new PersistenceException("Error finding participants" + e.getMessage());
        }
        return simulations;
    }

    @Override
    public LinkedList<Simulation> getAllSimulationsFiltered(String name) throws PersistenceException{
        LOGGER.info("Getting all simulations filtered by " + name);
        String sql = "SELECT * FROM simulation WHERE name LIKE ?";
        LinkedList<Simulation> simulations = new LinkedList<>();
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setString(1,"%" + name + "%");
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                Simulation sim = dbResultToSim(rs);
                sim.setParticipants(getParticipantListByID(sim.getId(),sim.getCreated()));
                simulations.add(sim);
            }
        }catch(SQLException e){
            throw new PersistenceException("Error getting all simulations " + e.getMessage());
        }catch(NotFoundException e){
            throw new PersistenceException("Error finding participants" + e.getMessage());
        }
        return simulations;
    }
    @Override
    public Participant insertParticipant(Integer simID, Participant participant) throws PersistenceException{
        LOGGER.info("Inserting the participant" + participant.toString());
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
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            while(rs.next()){
                id = rs.getInt("id");
            }
            participant.setId(id);
            return participant;
        } catch(SQLException e){
            throw new PersistenceException("Error inserting participant into database " + participant);
        }catch (Exception e){
            throw new PersistenceException("Error inserting participant into database G" + participant);
        }
    }

}
