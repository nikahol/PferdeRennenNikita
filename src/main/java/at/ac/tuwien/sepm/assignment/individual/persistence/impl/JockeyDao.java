package at.ac.tuwien.sepm.assignment.individual.persistence.impl;


import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IJockeyDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.LinkedList;

@Repository
public class JockeyDao implements IJockeyDao {
    private static Logger LOGGER = LoggerFactory.getLogger(JockeyDao.class);
    private final DBConnectionManager dbConnectionManager;

    @Autowired
    public JockeyDao(DBConnectionManager dbConnectionManager){this.dbConnectionManager = dbConnectionManager;}

    private static Jockey dbResultToJockey(ResultSet result) throws SQLException {
        return new Jockey(
            result.getInt(1),
            result.getString(2),
            result.getDouble(3),
            result.getTimestamp(4).toLocalDateTime(),
            result.getTimestamp(5).toLocalDateTime());
    }

    @Override
    public Jockey findOneById(Integer id) throws PersistenceException, NotFoundException {
        LOGGER.info("Get jockey with id " + id);
        String sql = "SELECT * FROM jockey WHERE id=? AND deleted = 0";
        Jockey jockey = null;
        try {
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                jockey = dbResultToJockey(result);
            }

        } catch (SQLException e) {
            LOGGER.error("Problem while executing SQL select statement for reading jockey with id " + id, e);
            throw new PersistenceException("Could not read jockey with id " + id, e);
        }
        if (jockey != null) {
            return jockey;
        } else {
            throw new NotFoundException("Could not find jockey with id " + id);
        }
    }

    @Override
    public Jockey insertJockey(Jockey jockey) throws PersistenceException{
        String sql = "INSERT INTO jockey (name, skill, created, updated) VALUES (?, ?, DEFAULT, DEFAULT)";
        Jockey ret = null;
        int id = 0;
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, jockey.getName());
            statement.setDouble(2, jockey.getSkill());
            LOGGER.debug("About to execute statement " + statement);
            statement.execute();
            ResultSet rs = statement.getGeneratedKeys();
            if(rs.next()){
                id = rs.getInt("id");

            }
            ret = findOneById(id);


        }catch(SQLException e){
            LOGGER.error("Problem inserting following jockey into the database: " + jockey.toString() + " " + e);
            throw new PersistenceException("Could not insert jockey " + jockey.toString(),e);
        }catch(NotFoundException e){
            LOGGER.error("NOT FOUND INSERT JOCKEY DAO: Unexpected error thrown by findOneByID " + e.getMessage(), e);
            throw new PersistenceException("Could not insert jockey " + jockey.toString(),e);
        }
        LOGGER.info("Inserted jockey with id: " + ret.getId());
        return ret;

    }

    @Override
    public Jockey updateJockey(Jockey jockey)throws PersistenceException, NotFoundException{
        String sql = "UPDATE jockey SET name = ?, skill = ?, updated=DEFAULT WHERE id = ? AND deleted = 0";
        Jockey ret = null;
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setString(1, jockey.getName());
            statement.setDouble(2,jockey.getSkill());
            statement.setInt(3, jockey.getId());
            LOGGER.debug("About to execute statement " + statement);
            statement.execute();
            ret = findOneById(jockey.getId());
        }catch(SQLException e){
            LOGGER.error("SQLEXCEPTION UPDATE JOCKEY DAO: Problem updating jockey in the database" + jockey.toString() + " " + e);
            throw new PersistenceException("Could not update jockey" + jockey.toString(),e);
        }
        LOGGER.info("Successfully updated jockey with id: " + jockey.getId());
        return ret;

    }

    @Override
    public void deleteJockey(Integer id) throws PersistenceException, NotFoundException{
        String sql = "UPDATE jockey SET deleted = 1 WHERE id = ?";
        Jockey jockey = null;
        try{
            jockey = findOneById(id);
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1,id);
            LOGGER.debug("About to execute statement " + statement);
            statement.executeUpdate();

        }catch(SQLException e){
            LOGGER.error("SQLEXCEPTION DELETE JOCKEY DAO: Problem deleting jockey in the database with id " + id + " " + e);
            throw new PersistenceException("Could not delete jockey with id " + id,e);
        }
        LOGGER.info("Deleted jockey with id: " + jockey.getId());
    }

    @Override
    public LinkedList<Jockey> getAllJockeys() throws PersistenceException {
        String sql = "SELECT * FROM jockey WHERE deleted = 0";
        LinkedList<Jockey> jockeyList = new LinkedList<>();
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            LOGGER.debug("About to execute statement " + statement);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                jockeyList.add(dbResultToJockey(rs));
            }
        }catch(SQLException e){
            LOGGER.error("SQLEXCEPTION GET ALL JOCKEY DAO: Problem getting all jockeys from database " + e);
            throw new PersistenceException("Could not get all jockeys from database" ,e);
        }

        LOGGER.info("Successfully got all jockeys");
        return jockeyList;

    }

    public LinkedList<Jockey> getAllJockeysFiltered(Jockey jockey) throws PersistenceException{
        String sql = "SELECT * From jockey WHERE name LIKE ? AND skill >= ? AND deleted = 0";
        LinkedList<Jockey> jockeyList = new LinkedList<>();
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setString(1, ("%" + jockey.getName() + "%"));
            statement.setDouble(2,jockey.getSkill());
            LOGGER.debug("About to execute statement " + statement);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                jockeyList.add(dbResultToJockey(rs));
            }
            LOGGER.info("Got all jockeys, filtered by " + jockey.toString());
            return jockeyList;
        }catch(SQLException e){
            LOGGER.error("SQLEXCEPTION GET ALL FILTERED JOCKEY DAO: Problem getting all jockeys filtered from database " + e);
            throw new PersistenceException("Could not get all jockeys filtered from database" ,e);
        }

    }

    public void newVersionJockey(Integer id, LocalDateTime jockeyUpdate) throws NotFoundException, PersistenceException{
        LOGGER.debug("Moving jockey with ID " + id + " to version table");
        String sql = "INSERT INTO jockeyVersions (id, name, skill, created, updated) VALUES (?,?,?,?,?)";
        try{
            if(!checkVersionExists(id, jockeyUpdate)) {
                Jockey toTransfer = findOneById(id);
                PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
                statement.setInt(1, toTransfer.getId());
                statement.setString(2, toTransfer.getName());
                statement.setDouble(3, toTransfer.getSkill());
                statement.setTimestamp(4, Timestamp.valueOf(toTransfer.getUpdated()));
                statement.setTimestamp(5, Timestamp.valueOf(toTransfer.getCreated()));
                LOGGER.debug("About to execute statement " + statement);
                statement.execute();
            }else{
                LOGGER.debug("Jockey with id " + id + " and updated " + jockeyUpdate + " already exists in the version table");
            }
        }catch(SQLException e){
            LOGGER.error("SQLEXCEPTION NEW VERSION JOCKEY DAO: Cant move jockey with id " + id + "to version table");
            throw new PersistenceException("Could not move jockey to the version table", e);
        }
    }

    private boolean checkVersionExists(Integer id, LocalDateTime jockeyUpdate) throws PersistenceException{
        String sql = "SELECT * FROM jockeyVersions WHERE id = ? AND updated = ?";
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1, id);
            statement.setTimestamp(2, Timestamp.valueOf(jockeyUpdate));
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                return true;
            }
        }catch(SQLException e){
            LOGGER.error("SQLEXCEPTION NEW VERSION CHECK EXISTS JOCKEY DAO: Cant check if jockey with  " + id + " exists in version table");
            throw new PersistenceException("Could not check version existence " + id + " " + jockeyUpdate, e);
        }
        return false;
    }
}
