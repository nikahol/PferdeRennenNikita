package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.HorseDto;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.service.IHorseService;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.util.mapper.HorseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedList;

@RestController
@RequestMapping("/api/v1/horses")
public class HorseEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(HorseEndpoint.class);
    private static final String BASE_URL = "/api/v1/horses";
    private final IHorseService horseService;
    private final HorseMapper horseMapper;

    @Autowired
    public HorseEndpoint(IHorseService horseService, HorseMapper horseMapper) {
        this.horseService = horseService;
        this.horseMapper = horseMapper;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public HorseDto getOneById(@PathVariable("id") Integer id) {
        LOGGER.info("GET " + BASE_URL + "/" + id);
        try {
            return horseMapper.entityToDto(horseService.findOneById(id));
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during read horse with id " + id, e);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during reading horse: " + e.getMessage(), e);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public HorseDto insertHorse(@RequestBody HorseDto horseDto) {
        LOGGER.info("POST " + BASE_URL + " " + horseDto.toString());
        try {
            return horseMapper.entityToDto(horseService.insertHorse(horseMapper.dtoToEntity(horseDto)));
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error attempting to insert horse: " + horseDto.toString(), e);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error attempting to insert horse " + e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public HorseDto updateHorse(@RequestBody HorseDto horseDto, @PathVariable("id") Integer id) {
        LOGGER.info("POST " + BASE_URL + " " + id);
        horseDto.setId(id);
        try {
            return horseMapper.entityToDto(horseService.updateHorse(horseMapper.dtoToEntity(horseDto)));
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error attempting to update horse: " + horseDto.toString(), e);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error attempting to update horse " + e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public HorseDto deleteHorse(@PathVariable("id") Integer id){
        LOGGER.info("DELETE " + BASE_URL + " " + id);
        try{
            return horseMapper.entityToDto(horseService.deleteHorse(id));
        }catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error attempting to delete horse with id " + id, e);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error attempting to delete horse " + e.getMessage(), e);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public LinkedList<HorseDto> getAllHorses(){
        LOGGER.info("GET " + BASE_URL);
        LinkedList<HorseDto> res = new LinkedList<>();
        try{
            LinkedList<Horse> horseList = horseService.getAllHorses();
            for (Horse x: horseList
                 ) {
                res.add(horseMapper.entityToDto(x));
            }
            return res;
        }catch(ServiceException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error attempting to get all horses" + e, e);
        }

    }

    @RequestMapping(params =  {"name", "breed", "minSpeed", "maxSpeed"},method = RequestMethod.GET)
    public LinkedList<HorseDto> getAllHorsesFiltered(@RequestParam("name") String name, @RequestParam("breed") String breed, @RequestParam("minSpeed") Double minSpeed, @RequestParam("maxSpeed") Double maxSpeed){
        LOGGER.info("GET " + BASE_URL);
        LinkedList<HorseDto> res = new LinkedList<>();
        HorseDto horse = new HorseDto(null, name,breed,minSpeed,maxSpeed,null,null,false);
        try{
            LinkedList<Horse> horseList = horseService.getAllHorsesFiltered(horseMapper.dtoToEntity(horse));
            for (Horse x: horseList
            ) {
                res.add(horseMapper.entityToDto(x));
            }
            return res;
        }catch(ServiceException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error attempting to get all horses " + e, e);
        }catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error attempting to get filtered horses " + e.getMessage(), e);
        }
    }


}
