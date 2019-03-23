package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exceptions.BadRequestException;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IHorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.service.IHorseService;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class HorseService implements IHorseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HorseService.class);
    private final IHorseDao horseDao;

    @Autowired
    public HorseService(IHorseDao horseDao) {
        this.horseDao = horseDao;
    }

    @Override
    public Horse findOneById(Integer id) throws ServiceException, NotFoundException {
        try {
            LOGGER.debug("Attempting to find horse with id " + id + "in the database. Currently in service.");
            return horseDao.findOneById(id);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Horse insertHorse(Horse horse) throws ServiceException, NotFoundException, BadRequestException{
        if(horse.getMinSpeed() == null || horse.getMaxSpeed() == null|| horse.getName() == null || horse.getName().isEmpty()){
            LOGGER.error("BAD REQUEST INSERT HORSE SERVICE: One of the following is either null or empty: min_speed: " + horse.getMinSpeed() + " max_speed: " + horse.getMaxSpeed() + " name: " + horse.getName());
            throw new BadRequestException("Horses must have a name, min speed and max speed. If you are seeing this message, one of these attributes was not defined during creation");
        }
        if(horse.getMaxSpeed() > 60 || horse.getMinSpeed() < 40 || horse.getMaxSpeed() < horse.getMinSpeed()){
            LOGGER.error("BAD REQUEST INSERT HORSE SERVICE: One of the following has occurred: max_speed greater than 60, min_speed is less than 40, min_speed is greater than max_speed. max_speed " + horse.getMaxSpeed() + " min_speed: " + horse.getName());
            throw new BadRequestException("Horses cannot be faster than 60 km/h, slower than 40 km/h or have their minimal speed lower than their max speed.");
        }
        try{
            LOGGER.debug("Attempting to insert horse " + horse.toString() + " into the database. Currently in service.");
            return horseDao.insertHorse(horse);
        }catch (PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Horse updateHorse(Horse horse) throws ServiceException, NotFoundException, BadRequestException {
        if((horse.getMaxSpeed() != null && horse.getMaxSpeed() > 60) || (horse.getMinSpeed() != null && horse.getMinSpeed() < 40) || (horse.getMinSpeed() != null && horse.getMaxSpeed() != null && horse.getMinSpeed() > horse.getMaxSpeed())){
            LOGGER.error("BAD REQUEST UPDATE HORSE SERVICE: One of the following has occurred: max_speed is not null and greater than 60, min_speed is not null and less than 40, min_speed is greater than max_speed. max_speed " + horse.getMaxSpeed() + " min_speed: " + horse.getName());
            throw new BadRequestException("Horses cannot be faster than 60 km/h, slower than 40 km/h or have their minimal speed lower than their max speed.");
        }
        try{
            Horse horseCheck = horseDao.findOneById(horse.getId());
            if(horse.getName() == null || horse.getName().isEmpty()){
                horse.setName(horseCheck.getName());
            }if(horse.getBreed() == null){
                horse.setName(horseCheck.getBreed());
            }if(horse.getMaxSpeed() == null){
                horse.setMaxSpeed(horseCheck.getMaxSpeed());
            }if(horse.getMinSpeed() == null){
                horse.setMinSpeed(horseCheck.getMinSpeed());
            }
            if(horse.getMaxSpeed() < horse.getMinSpeed()){
                throw new BadRequestException("Horses cannot be faster than 60 km/h, slower than 40 km/h, have their minimal speed lower than their max speed.");
            }
            LOGGER.debug("Attempting to update horse with id " + horse.getId() + " with values " + horse.toString() + ". Currently in service.");
            return horseDao.updateHorse(horse);
        }catch (PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteHorse(Integer id) throws ServiceException, NotFoundException{
        try{
            LOGGER.debug("Attempting to delete horse with id " + id + " from the database. Currently in service");
            horseDao.deleteHorse(id);
        }catch(PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public LinkedList<Horse> getAllHorses()throws ServiceException{
        try{
            LOGGER.debug("Attempting to get all horses from database. Currently in service");
            return horseDao.getAllHorses();
        }catch(PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public LinkedList<Horse> getAllHorsesFiltered(Horse horse) throws ServiceException{
        try{
            LOGGER.debug("Attempting to get all horses from database, filtered by: " + horse.toString() + ". Currently in service");
            return horseDao.getAllHorsesFiltered(horse);
        }catch(PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
