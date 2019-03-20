package at.ac.tuwien.sepm.assignment.individual.util.mapper;

import at.ac.tuwien.sepm.assignment.individual.entity.Participant;
import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.ParticipantDto;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.SimulationDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;

@Component
public class SimulationMapper {
    public Simulation simDtoSim(SimulationDto simulationDto){
        ArrayList<Participant> participants = new LinkedList<>();
        for(ParticipantDto x : simulationDto.getSimulationParticipants()){
            Participant bob = new Participant(x.getHorseId(), x.getJockeyId(), null, null, null, x.getluckFactor(), null, null, null);
            participants.add(bob);
        }
        Simulation sim = new Simulation(null, simulationDto.getName(), null, participants);
        return sim;
    }

    public SimulationDto simToSimDto(Simulation simulation){
        return new SimulationDto();
    }


}
