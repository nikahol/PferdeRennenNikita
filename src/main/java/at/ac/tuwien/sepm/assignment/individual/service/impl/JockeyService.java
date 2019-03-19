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
        LOGGER.info("Get jockey with id " + id);
        try {
            return jockeyDao.findOneById(id);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Jockey insertJockey(Jockey jockey) throws ServiceException, NotFoundException{
        if(jockey.getName() == null){
            throw new ServiceException("Jockeys need to have a name and skill level. If you are reading this it is because one of these attributes was missing during creation.", null);
        }
        LOGGER.info("Inserting Jockey: " + jockey.toString());
        try{
            return jockeyDao.insertJockey(jockey);
        }catch(PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Jockey updateJockey(Jockey jockey) throws ServiceException, NotFoundException{
        LOGGER.info("Updating Jockey in service layer " + jockey.toString());
        try{
            Jockey jockeyCheck = jockeyDao.findOneById(jockey.getId());
            if(jockey.getName() == null || jockey.getName().isEmpty()){
                jockey.setName(jockeyCheck.getName());
            }
            if(jockey.getSkill() == null){
                jockey.setSkill(jockeyCheck.getSkill());
            }
            return jockeyDao.updateJockey(jockey);
        }catch (PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteJockey(Integer id)throws ServiceException, NotFoundException{
        LOGGER.info("Deleting Jockey with id " + id + " in service layer");
        try{
            jockeyDao.deleteJockey(id);
        }catch (PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public LinkedList<Jockey> getAllJockeys() throws ServiceException{
        LOGGER.info("Getting all jockeys in service layer");
        try{
            return jockeyDao.getAllJockeys();
        }catch(PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public LinkedList<Jockey> getAllJockeysFiltered(Jockey jockey) throws ServiceException{
        LOGGER.info("Getting all jockeys in service layer");
        try{
            return jockeyDao.getAllJockeysFiltered(jockey);
        }catch(PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

}
