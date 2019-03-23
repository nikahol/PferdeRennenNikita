package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.exceptions.BadRequestException;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.JockeyDto;
import at.ac.tuwien.sepm.assignment.individual.service.IJockeyService;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.util.mapper.JockeyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedList;

@RestController
@RequestMapping("/api/v1/jockeys")
public class JockeyEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(JockeyEndpoint.class);
    private static final String BASE_URL = "api/v1/jockeys";
    private final IJockeyService jockeyService;
    private final JockeyMapper jockeyMapper;

    @Autowired
    public JockeyEndpoint(IJockeyService jockeyService, JockeyMapper jockeyMapper) {
        this.jockeyService = jockeyService;
        this.jockeyMapper = jockeyMapper;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JockeyDto getOneById(@PathVariable("id") Integer id) {
        LOGGER.info("GET " + BASE_URL + "/" + id);
        try {
            LOGGER.debug("Attempting to get jockey with id " + id + ". Currently in endpoint");
            return jockeyMapper.jockeyToJockeyDto(jockeyService.findOneById(id));
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during read jockey with id " + id, e);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during reading jockey: " + e.getMessage(), e);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public JockeyDto insertJockey(@RequestBody JockeyDto jockeyDto) {
        LOGGER.info("POST " + BASE_URL + " ");
        try {
            LOGGER.debug("Attempting to insert jockey " + jockeyDto.toString() + "into the database. Currently in endpoint");
            return jockeyMapper.jockeyToJockeyDto(jockeyService.insertJockey(jockeyMapper.jockeyDtoToJockey(jockeyDto)));
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error attempting to insert jockey: " + jockeyDto.toString(), e);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public JockeyDto updateJockey(@RequestBody JockeyDto jockeyDto, @PathVariable("id") Integer id) {
        LOGGER.info("PUT" + BASE_URL + " " + id);
        jockeyDto.setId(id);
        try {
            LOGGER.debug("Attempting to update jockey with id " + id + " with values " + jockeyDto.toString() + ". Currently in endpoint");
            return jockeyMapper.jockeyToJockeyDto((jockeyService.updateJockey(jockeyMapper.jockeyDtoToJockey(jockeyDto))));
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error attempting to update jockey: " + jockeyDto.toString(), e);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error attempting to update jockey " + e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteJockey(@PathVariable("id") Integer id){
        LOGGER.info("DELETE " + BASE_URL + id);
        try{
            LOGGER.debug("Attempting to delete jockey with id " + id + ". Currently in endpoint");
            jockeyService.deleteJockey(id);
        }catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error attempting to delete jockey with id " + id, e);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error attempting to delete jockey " + e.getMessage(), e);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public LinkedList<JockeyDto> getAllJockeys(){
        LOGGER.info("GET " + BASE_URL);
        try{
            LOGGER.debug("Attempting to get all jockeys from the database. Currently in endpoint");
            return jockeyMapper.jockeyListToJockeyDtoList(jockeyService.getAllJockeys());
        }catch(ServiceException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error attempting to get all jockeys" + e, e);
        }
    }
    @RequestMapping(params =  {"name", "skill"},method = RequestMethod.GET)
    public LinkedList<JockeyDto> getAllJockeysFiltered(@RequestParam("name") String name, @RequestParam("skill") Double skill){
        LOGGER.info("GET " + BASE_URL);
        if(name == null){
            name = "";
        }
        if(skill == null){
            skill = Double.MIN_VALUE;
        }
        JockeyDto jockey = new JockeyDto(null, name, skill, null, null);
        try{
            LOGGER.debug("Attempting to get all jockeys form the database, filtered by " + jockey.toString() + ". Currently in endpoint");
            return jockeyMapper.jockeyListToJockeyDtoList(jockeyService.getAllJockeysFiltered(jockeyMapper.jockeyDtoToJockey(jockey)));
        }catch(ServiceException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error attempting to get all jockeys " + e, e);
        }
    }
}
