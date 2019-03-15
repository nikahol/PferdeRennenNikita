package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IHorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.service.IHorseService;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Horse insertHorse(Horse horse) throws ServiceException, NotFoundException{
        LOGGER.info("Inserting horse in service layer " + horse.toString());
        if(horse.getMaxSpeed() > 60 || horse.getMinSpeed() < 40 || horse.getMaxSpeed() < horse.getMinSpeed()){
            throw new ServiceException("Horses cannot be faster than 60 km/h, slower than 40 km/h or have their minimal speed lower than their max speed.", null);
        }
        try{

            return horseDao.insertHorse(horse);

        }catch (PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Horse updateHorse(Horse horse) throws ServiceException, NotFoundException{
        LOGGER.info("Updating horse in service layer " + horse.toString());
        if(horse.getMaxSpeed() > 60 || horse.getMinSpeed() < 40 || horse.getMaxSpeed() < horse.getMinSpeed()){
            throw new ServiceException("Horses cannot be faster than 60 km/h, slower than 40 km/h or have their minimal speed lower than their max speed.", null);
        }
        try{

            return horseDao.updateHorse(horse);

        }catch (PersistenceException e){
            throw new ServiceException(e.getMessage(), e);
        }
    }
}