package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Participant;
import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.persistence.ISimulationDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.time.LocalDateTime;

@Repository
public class SimulationDao implements ISimulationDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationDao.class);
    private final DBConnectionManager dbConnectionManager;

    @Autowired
    public SimulationDao(DBConnectionManager dbConnectionManager) {
        this.dbConnectionManager = dbConnectionManager;
    }

    @Override
    public Simulation insertSimulation(Simulation simulation) throws PersistenceException {
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
                id = rs.getInt("id");
                created = rs.getTimestamp("created").toLocalDateTime();
            }
            simulation.setCreated(created);
            simulation.setId(id);
            return simulation;
        }catch (SQLException e){
            throw new PersistenceException("Error inserting simulation into database + " + simulation);
        }
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
            statement.execute();
            ResultSet rs = statement.getGeneratedKeys();
            while(rs.next()){
                id = rs.getInt("id");
            }
            participant.setId(id);
            return participant;
        }catch(SQLException e){
            throw new PersistenceException("Error inserting participant into database " + participant);
        }
    }

}
