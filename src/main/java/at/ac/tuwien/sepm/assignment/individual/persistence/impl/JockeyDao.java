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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        String sql = "SELECT * FROM jockey WHERE id=?";
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
    public Jockey insertJockey(Jockey jockey) throws PersistenceException, NotFoundException{
        LOGGER.info("insert Jockey " + jockey.toString());
        String sql = "INSERT INTO jockey (name, skill, created, updated) VALUES (?, ?, DEFAULT, DEFAULT)";
        Jockey ret = null;
        int id = 0;
        try{
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, jockey.getName());
            statement.setDouble(2, jockey.getSkill());

            statement.execute();
            ResultSet rs = statement.getGeneratedKeys();
            if(rs.next()){
                id = rs.getInt("id");

            }
            ret = findOneById(id);


        }catch(SQLException e){
            LOGGER.error("Problem inserting following jockey into the database: " + jockey.toString() + " " + e);
            throw new PersistenceException("Could not insert jockey " + jockey.toString(),e);
        }
        if(ret == null){
            throw new NotFoundException("Could not find newly generated jockey with id: " + ret.getId());
        }else{
            LOGGER.info("Successfully inserted jockey with id: " + ret.getId());
            return ret;
        }

    }
}
