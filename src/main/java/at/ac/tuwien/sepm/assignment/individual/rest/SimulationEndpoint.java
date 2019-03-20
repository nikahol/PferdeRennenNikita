package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.exceptions.BadRequestException;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.SimulationDto;
import at.ac.tuwien.sepm.assignment.individual.service.ISimulationService;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.util.mapper.SimulationMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
    public SimulationDto newSimulation(@RequestBody SimulationDto simulationDto){
        LOGGER.info("POST " + BASE_URL + simulationDto);
        try {
            return simulationMapper.simToSimDto(simulationService.newSimulation(simulationMapper.simDtoSim(simulationDto)));
        }catch(ServiceException e){

        }catch(NotFoundException e){

        }catch(BadRequestException e){

        }
        return simulationDto;
    }
}

