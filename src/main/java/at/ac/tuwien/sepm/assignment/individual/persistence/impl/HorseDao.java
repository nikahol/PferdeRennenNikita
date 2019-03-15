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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


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
            result.getInt("id"),
            result.getString("name"),
            result.getString("breed"),
            result.getDouble("min_speed"),
            result.getDouble("max_speed"),
            result.getTimestamp("created").toLocalDateTime(),
            result.getTimestamp("updated").toLocalDateTime());
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
        if (horse != null) {
            return horse;
        } else {
            throw new NotFoundException("Could not find horse with id " + id);
        }
    }

    @Override
    public Horse insertHorse(Horse horse) throws PersistenceException, NotFoundException{
        LOGGER.info("insert Horse " + horse.toString());
        String sql = "INSERT INTO horse (name, breed, min_speed, max_speed, created, updated) VALUES (?,?,?,?, DEFAULT, DEFAULT)";
        Horse ret = null;
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, horse.getName());
            statement.setString(2, horse.getBreed());
            statement.setDouble(3, horse.getMinSpeed());
            statement.setDouble(4, horse.getMaxSpeed());

            statement.execute();

            ResultSet rs = statement.getGeneratedKeys();

            if(rs.next()){
                ret = dbResultToHorse(rs);
            }


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

    public Horse updateHorse(Horse horse) throws PersistenceException, NotFoundException{
        LOGGER.info("updating Horse " + horse.toString());
        String sql = "UPDATE horse SET name=?, breed = ?, min_speed =?, max_speed =?, updated=DEFAULT";
        Horse ret = null;
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, horse.getName());
            statement.setString(2, horse.getBreed());
            statement.setDouble(3, horse.getMinSpeed());
            statement.setDouble(4, horse.getMaxSpeed());

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
}
