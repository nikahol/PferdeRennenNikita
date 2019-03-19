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
        LOGGER.info("Get horse with id " + id);
        try {
            return horseDao.findOneById(id);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Horse insertHorse(Horse horse) throws ServiceException, NotFoundException, BadRequestException{
        LOGGER.info("Inserting horse in service layer " + horse.toString());
        if(horse.getMinSpeed() == null || horse.getMaxSpeed() == null|| horse.getName() == null || horse.getName().isEmpty()){
            throw new BadRequestException("Horses must have a name, min speed and max speed. If you are seeing this message, one of these attributes was not defined during creation");
        }
        if(horse.getMaxSpeed() > 60 || horse.getMinSpeed() < 40 || horse.getMaxSpeed() < horse.getMinSpeed()){
            throw new BadRequestException("Horses cannot be faster than 60 km/h, slower than 40 km/h or have their minimal speed lower than their max speed.");
        }
        try{
            return horseDao.insertHorse(horse);
        }catch (PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Horse updateHorse(Horse horse) throws ServiceException, NotFoundException, BadRequestException {
        LOGGER.info("Updating horse in service layer " + horse.toString());
        if((horse.getMaxSpeed() != null && horse.getMaxSpeed() > 60) || (horse.getMinSpeed() != null && horse.getMinSpeed() < 40) || (horse.getMinSpeed() != null && horse.getMaxSpeed() != null && horse.getMinSpeed() > horse.getMaxSpeed())){
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
            return horseDao.updateHorse(horse);
        }catch (PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteHorse(Integer id) throws ServiceException, NotFoundException{
        LOGGER.info("Deleting horse with id " + id + " in service layer");
        try{
            horseDao.deleteHorse(id);
        }catch(PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public LinkedList<Horse> getAllHorses()throws ServiceException{
        LOGGER.info("Getting all horses in service layer");
        try{
            return horseDao.getAllHorses();
        }catch(PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public LinkedList<Horse> getAllHorsesFiltered(Horse horse) throws ServiceException{
        LOGGER.info("Getting all horses filtered in service layer");
        try{
            return horseDao.getAllHorsesFiltered(horse);
        }catch(PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
