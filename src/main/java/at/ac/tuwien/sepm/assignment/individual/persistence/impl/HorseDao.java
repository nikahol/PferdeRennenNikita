package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IHorseDao;
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
public class HorseDao implements IHorseDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(HorseDao.class);
    private final DBConnectionManager dbConnectionManager;

    @Autowired
    public HorseDao(DBConnectionManager dbConnectionManager) {
        this.dbConnectionManager = dbConnectionManager;
    }

    private static Horse dbResultToHorse(ResultSet result) throws SQLException {
        return new Horse(
            result.getInt(1),
            result.getString(2),
            result.getString(3),
            result.getDouble(4),
            result.getDouble(5),
            result.getTimestamp(6).toLocalDateTime(),
            result.getTimestamp(7).toLocalDateTime(),
            result.getBoolean(8));
    }


    @Override
    public Horse findOneById(Integer id) throws PersistenceException, NotFoundException {
        String sql = "SELECT * FROM Horse WHERE id=? AND deleted = 0";
        Horse horse = null;
        try {
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1, id);
            LOGGER.debug("About to execute sql statement " + statement);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                horse = dbResultToHorse(result);
            }
        } catch (SQLException e) {
            LOGGER.error("SQLEXCEPTION FIND ONE BY ID HORSE DAO: Problem while executing SQL select statement for reading horse with id " + id, e);
            throw new PersistenceException("Could not read horses with id " + id, e);
        }

        if (horse != null) {
            LOGGER.debug("Horse with id " + id + " found in the database.");
            return horse;
        } else {
            LOGGER.error("NOT FOUND FIND ONE BY ID HORSE DAO: Horse with id " + id + " was not found in the database.");
            throw new NotFoundException("Could not find horse with id " + id);
        }
    }

    @Override
    public Horse insertHorse(Horse horse) throws PersistenceException{
        String sql = "INSERT INTO horse (name, breed, min_speed, max_speed, created, updated, deleted) VALUES (?,?,?,?, DEFAULT, DEFAULT, DEFAULT)";
        Horse ret = null;
        int id = 0;
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, horse.getName());
            statement.setString(2, horse.getBreed());
            statement.setDouble(3, horse.getMinSpeed());
            statement.setDouble(4, horse.getMaxSpeed());
            LOGGER.debug("About to execute sql statement " + statement);
            statement.execute();

            ResultSet rs = statement.getGeneratedKeys();

            if(rs.next()){
                id = rs.getInt("id");

            }
            ret = findOneById(id);

        }catch(SQLException e){
            LOGGER.error("Problem inserting following horse into the database: " + horse.toString() + " " + e);
            throw new PersistenceException("Could not insert horse " + horse.toString(),e);
        }catch(NotFoundException e){
            LOGGER.error("NOT FOUND INSERT HORSE DAO: findOneByID threw an unexpected not found exception for newly generated horse: " + horse.toString());
            throw new PersistenceException("Could not find newly generated horse with id: " + ret.getId(), e);
        }
        LOGGER.info("Horse with id " + ret.getId() + " inserted into the database");
        return ret;

    }

    @Override
    public Horse updateHorse(Horse horse) throws PersistenceException, NotFoundException{
        String sql = "UPDATE horse SET name=?, breed = ?, min_speed =?, max_speed =?, updated=DEFAULT WHERE id = ? AND deleted = 0";
        Horse ret = null;
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, horse.getName());
            statement.setString(2, horse.getBreed());
            statement.setDouble(3, horse.getMinSpeed());
            statement.setDouble(4, horse.getMaxSpeed());
            statement.setInt(5, horse.getId());
            LOGGER.debug("About to execute sql statement " + statement);
            statement.execute();
            ret = findOneById(horse.getId());
        }catch(SQLException e){
            LOGGER.error("SQLEXCEPTION UPDATE HORSE DAO: Problem updating horse in the database" + horse.toString() + " " + e);
            throw new PersistenceException("Could not update horse" + horse.toString(),e);
        }
        LOGGER.info("Updated horse with id " + horse.getId());
        return ret;
    }
    @Override
    public void deleteHorse(Integer id) throws PersistenceException, NotFoundException{
        String sql = "UPDATE horse SET deleted = 1 WHERE id = ?";
        Horse horse = null;
        try{
            horse = findOneById(id);
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1,id);
            LOGGER.debug("About to execute sql statement " + statement);
            statement.executeUpdate();
            horse.setDeleted(true);
        }catch(SQLException e){
            LOGGER.error("SQLEXCEPTION DELETE HORSE DAO: Problem deleting horse in the database with id " + id + " " + e);
            throw new PersistenceException("Could not delete horse with id " + id,e);
        }
        LOGGER.info("Deleted horse with id " + horse.getId());

    }

    @Override
    public LinkedList<Horse> getAllHorses() throws PersistenceException{
        LOGGER.info("Getting all Horses from database");
        String sql = "SELECT * FROM horse Where deleted=0";
        LinkedList<Horse> horseList = new LinkedList<>();
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            LOGGER.debug("About to execute sql statement " + statement);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                horseList.add(dbResultToHorse(rs));
            }
        }catch(SQLException e){
            LOGGER.error("SQLEXCEPTION GET ALL HORSE DAO: Problem getting all horses from database " + e);
            throw new PersistenceException("Could not get all horses from database" ,e);
        }

        LOGGER.info("Successfully got all horses");
        return horseList;

    }

    @Override
    public LinkedList<Horse> getAllHorsesFiltered(Horse horse) throws PersistenceException{
        LOGGER.info("Getting all horses Filtered from database");
        String sql = "SELECT * From horse WHERE name LIKE ? AND breed LIKE ? AND min_speed >= ? AND max_speed <= ? AND deleted = 0";

        LinkedList<Horse> horseList = new LinkedList<>();
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setString(1, ("%" + horse.getName() + "%"));
            statement.setString(2,("%" + horse.getBreed() + "%"));
            statement.setDouble(3,horse.getMinSpeed());
            statement.setDouble(4,horse.getMaxSpeed());
            LOGGER.debug("About to execute sql statement " + statement);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                horseList.add(dbResultToHorse(rs));
            }
            LOGGER.info("Succesuflly got all horses filtered");
            return horseList;
        }catch(SQLException e){
            LOGGER.error("SQLEXCEPTION GET ALL FILTERED HORSE DAO: Problem getting all horses filtered from database " + e);
            throw new PersistenceException("Could not get all horses filtered from database" ,e);
        }

    }

    public void newVersionHorse(Integer id, LocalDateTime horseUpdate) throws NotFoundException, PersistenceException{
        LOGGER.debug("Moving horse with ID " + id + " to version table");
        String sql = "INSERT INTO horseVersions (id, name, breed, min_speed, max_speed, created, updated) VALUES (?,?,?,?,?,?,?)";
        try{
            if(checkVersionExists(id, horseUpdate) == false) {
                Horse toTransfer = findOneById(id);
                PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
                statement.setInt(1, toTransfer.getId());
                statement.setString(2, toTransfer.getName());
                statement.setString(3, toTransfer.getBreed());
                statement.setDouble(4, toTransfer.getMinSpeed());
                statement.setDouble(5, toTransfer.getMaxSpeed());
                statement.setTimestamp(6, Timestamp.valueOf(toTransfer.getCreated()));
                statement.setTimestamp(7, Timestamp.valueOf(toTransfer.getUpdated()));
                LOGGER.debug("About to execute sql statement " + statement);
                statement.execute();
            }else{
                LOGGER.debug("Horse with id " + id + " and update time " + horseUpdate + " is already in version table.");
            }
        }catch(SQLException e){
            LOGGER.error("SQLEXCEPTION NEW VERSION HORSE DAO: Cant move horse with id " + id + " to version table");
            throw new PersistenceException("Could not move horse to the version table", e);
        }
    }

    private boolean checkVersionExists(Integer id, LocalDateTime horseUpdate) throws PersistenceException{
        String sql = "SELECT * FROM horseVersions WHERE id = ? AND updated = ?";
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1, id);
            statement.setTimestamp(2, Timestamp.valueOf(horseUpdate));
            ResultSet rs = statement.executeQuery();
            return rs.next();
        }catch(SQLException e){
            LOGGER.error("SQLEXCEPTION NEW VERSION CHECK HORSE DAO: Cant check if  horse with id " + id + " and update time " + horseUpdate +"exists in version table");
            throw new PersistenceException("Could not check version existence " + id + " " + horseUpdate, e);
        }
    }
}
