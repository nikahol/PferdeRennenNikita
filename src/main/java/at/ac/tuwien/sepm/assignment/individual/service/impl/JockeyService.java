package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IJockeyDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.service.IJockeyService;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class JockeyService implements IJockeyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JockeyService.class);
    private final IJockeyDao jockeyDao;

    @Autowired
    public JockeyService(IJockeyDao jockeyDao){ this.jockeyDao = jockeyDao; }

    @Override
    public Jockey findOneById(Integer id) throws ServiceException, NotFoundException{
        try {
            LOGGER.debug("Attempting to find a jockey by id " + id + ". Currently in service");
            return jockeyDao.findOneById(id);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Jockey insertJockey(Jockey jockey) throws ServiceException{
        if(jockey.getName() == null){
            LOGGER.error("BAD REQUEST INSERT JOCKEY: name cannot be null");
            throw new ServiceException("Jockeys need to have a name and skill level. If you are reading this it is because one of these attributes was missing during creation.", null);
        }
        try{
            LOGGER.debug("Attempting to insert jockey " + jockey.toString() + " into the database. Currently in service");
            return jockeyDao.insertJockey(jockey);
        }catch(PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Jockey updateJockey(Jockey jockey) throws ServiceException, NotFoundException{
        try{
            Jockey jockeyCheck = jockeyDao.findOneById(jockey.getId());
            if(jockey.getName() == null || jockey.getName().isEmpty()){
                jockey.setName(jockeyCheck.getName());
            }
            if(jockey.getSkill() == null){
                jockey.setSkill(jockeyCheck.getSkill());
            }
            LOGGER.info("Attempting to update jockey with id " + jockey.getId() +" with values "+ jockey.toString() + ". Currently in service");
            return jockeyDao.updateJockey(jockey);
        }catch (PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteJockey(Integer id)throws ServiceException, NotFoundException{
        try{
            LOGGER.debug("Attempting to delete jockey with id " + id + ". Currently in service");
            jockeyDao.deleteJockey(id);
        }catch (PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public LinkedList<Jockey> getAllJockeys() throws ServiceException{
        try{
            LOGGER.debug("Attempting to get all jockeys from the database. Currently in service");
            return jockeyDao.getAllJockeys();
        }catch(PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public LinkedList<Jockey> getAllJockeysFiltered(Jockey jockey) throws ServiceException{
        try{
            LOGGER.debug("Attempting to get all jockeys from the database, filtered by "+ jockey.toString() + ". Currently in service");
            return jockeyDao.getAllJockeysFiltered(jockey);
        }catch(PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

}
