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
        LOGGER.info("Get horse with id " + id);
        String sql = "SELECT * FROM Horse WHERE id=?";
        Horse horse = null;
        try {
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                horse = dbResultToHorse(result);
            }
        } catch (SQLException e) {
            LOGGER.error("Problem while executing SQL select statement for reading horse with id " + id, e);
            throw new PersistenceException("Could not read horses with id " + id, e);
        }
        if(horse.isDeleted()){
            throw new NotFoundException("Could not find horse with id " + id);
        }
        if (horse != null) {
            return horse;
        } else {
            throw new NotFoundException("Could not find horse with id " + id);
        }
    }

    @Override
    public Horse insertHorse(Horse horse) throws PersistenceException, NotFoundException{
        LOGGER.info("insert Horse " + horse.toString());
        String sql = "INSERT INTO horse (name, breed, min_speed, max_speed, created, updated, deleted) VALUES (?,?,?,?, DEFAULT, DEFAULT, DEFAULT)";
        Horse ret = null;
        int id = 0;
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, horse.getName());
            statement.setString(2, horse.getBreed());
            statement.setDouble(3, horse.getMinSpeed());
            statement.setDouble(4, horse.getMaxSpeed());

            statement.execute();

            ResultSet rs = statement.getGeneratedKeys();

            if(rs.next()){
                id = rs.getInt("id");

            }
            ret = findOneById(id);

        }catch(SQLException e){
            LOGGER.error("Problem inserting following horse into the database: " + horse.toString() + " " + e);
            throw new PersistenceException("Could not insert horse " + horse.toString(),e);
        }
        if(ret == null){
            throw new NotFoundException("Could not find newly generated horse with id: " + ret.getId());
        }else{
            LOGGER.info("Successfully inserted horse with id: " + ret.getId());
            return ret;
        }

    }
    @Override
    public Horse updateHorse(Horse horse) throws PersistenceException, NotFoundException{
        LOGGER.info("updating Horse " + horse.toString());
        String sql = "UPDATE horse SET name=?, breed = ?, min_speed =?, max_speed =?, updated=DEFAULT WHERE id = ? AND deleted = 0";
        Horse ret = null;
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, horse.getName());
            statement.setString(2, horse.getBreed());
            statement.setDouble(3, horse.getMinSpeed());
            statement.setDouble(4, horse.getMaxSpeed());
            statement.setInt(5, horse.getId());
            statement.execute();
            ret = findOneById(horse.getId());
        }catch(SQLException e){
            LOGGER.error("Problem updating horse in the database" + horse.toString() + " " + e);
            throw new PersistenceException("Could not update horse" + horse.toString(),e);
        }
        if(ret == null){
            throw new NotFoundException("Could not find newly updated horse with id: " + horse.getId());
        }else{
            LOGGER.info("Successfully updated horse with id: " + horse.getId());
            return ret;
        }

    }
    @Override
    public void deleteHorse(Integer id) throws PersistenceException, NotFoundException{
        LOGGER.info("deleting horse with id " + id);
        String sql = "UPDATE horse SET deleted = 1 WHERE id = ?";
        Horse horse = null;
        try{
            horse = findOneById(id);
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1,id);
            statement.executeUpdate();
            horse.setDeleted(true);
        }catch(SQLException e){
            LOGGER.error("Problem deleting horse in the database with id " + id + " " + e);
            throw new PersistenceException("Could not delete horse with id " + id,e);
        }
        if(horse == null){
            throw new NotFoundException("Could not find horse with id: " + horse.getId());
        }
        LOGGER.info("Successfully deleted horse with id: " + horse.getId());

    }

    @Override
    public LinkedList<Horse> getAllHorses() throws PersistenceException{
        LOGGER.info("Getting all Horses from database");
        String sql = "SELECT * FROM horse Where deleted=0";
        LinkedList<Horse> horseList = new LinkedList<>();
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);

            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                horseList.add(dbResultToHorse(rs));
            }
        }catch(SQLException e){
            LOGGER.error("Problem getting all horses from database " + e);
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
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                horseList.add(dbResultToHorse(rs));
            }
            return horseList;
        }catch(SQLException e){
            LOGGER.error("Problem getting all horses filtered from database " + e);
            throw new PersistenceException("Could not get all horses filtered from database" ,e);
        }

    }

    public void newVersionHorse(Integer id, LocalDateTime horseUpdate) throws NotFoundException, PersistenceException{
        LOGGER.info("Moving horse with ID " + id + " to version table");
        String sql = "INSERT INTO horseVersions (id, name, breed, min_speed, max_speed, created, updated) VALUES (?,?,?,?,?,?,?)";
        try{
            if(!checkVersionExists(id, horseUpdate)) {
                Horse toTransfer = findOneById(id);
                PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
                statement.setInt(1, toTransfer.getId());
                statement.setString(2, toTransfer.getName());
                statement.setString(3, toTransfer.getBreed());
                statement.setDouble(4, toTransfer.getMinSpeed());
                statement.setDouble(5, toTransfer.getMaxSpeed());
                statement.setTimestamp(6, Timestamp.valueOf(toTransfer.getUpdated()));
                statement.setTimestamp(7, Timestamp.valueOf(toTransfer.getCreated()));
                statement.execute();
            }
        }catch(SQLException e){
            LOGGER.error("Cant move horse to version table");
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
            if(rs.next()){
                return true;
            }
        }catch(SQLException e){
            throw new PersistenceException("Could not check version existence " + id + " " + horseUpdate, e);
        }
        return false;
    }
}
