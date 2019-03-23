package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.exceptions.BadRequestException;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.SimulationDto;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.SimulationSendDto;
import at.ac.tuwien.sepm.assignment.individual.service.ISimulationService;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.util.mapper.SimulationMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedList;


@RestController
@RequestMapping("/api/v1/simulations")
public class SimulationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationEndpoint.class);
    private static final String BASE_URL = "/api/v1/simulations";
    private final ISimulationService simulationService;
    private final SimulationMapper simulationMapper;

    @Autowired
    public SimulationEndpoint(ISimulationService simulationService, SimulationMapper simulationMapper){
        this.simulationMapper = simulationMapper;
        this.simulationService = simulationService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public SimulationSendDto newSimulation(@RequestBody SimulationDto simulationDto){
        LOGGER.info("POST " + BASE_URL + simulationDto);
        try {
            return simulationMapper.simToSimSendDto(simulationService.newSimulation(simulationMapper.simDtoSim(simulationDto)));
        }catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error attempting to insert simulation: " + simulationDto.toString(), e);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error attempting to insert simulation " + e.getMessage(), e);
        }catch(BadRequestException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Input not accepted in POST simulations method " + e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public SimulationSendDto getSimulationByID(@PathVariable("id") Integer id){
        LOGGER.info("GET "  + BASE_URL + "/" + id);
        try{
            return simulationMapper.simToSimSendDto(simulationService.getSimulationByID(id));
        }catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during GET sim with id " + id, e);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during GET sim with id " + id + e.getMessage(), e);
        }

    }

    @RequestMapping(method = RequestMethod.GET)
    public LinkedList<SimulationSendDto> getAllSimulations(){
        LOGGER.info("GET " + BASE_URL);
        try{
            return simulationMapper.simListTosimSendDtoList(simulationService.getAllSimulations());
        }catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during GET all simulations", e);
        }
    }

    @RequestMapping(params = {"name"}, method = RequestMethod.GET)
    public LinkedList<SimulationSendDto> getAllSimulationsFiltered(@RequestParam String name){
        LOGGER.info("GET " + BASE_URL +" name like " + name);
        if(name == null){
            name = "";
        }
        try{
            return simulationMapper.simListTosimSendDtoList(simulationService.getAllSimulationsFiltered(name));
        }catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during GET all simulations", e);
        }
    }

}

