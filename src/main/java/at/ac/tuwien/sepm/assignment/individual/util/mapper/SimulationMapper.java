package at.ac.tuwien.sepm.assignment.individual.util.mapper;

import at.ac.tuwien.sepm.assignment.individual.entity.Participant;
import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.ParticipantDto;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.ParticipantSendDto;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.SimulationDto;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.SimulationSendDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;

@Component
public class SimulationMapper {
    public Simulation simDtoSim(SimulationDto simulationDto){
        ArrayList<Participant> participants = new ArrayList<>();
        for(ParticipantDto x : simulationDto.getSimulationParticipants()){
            Participant bob = new Participant(null, x.getHorseId(),  null,null, x.getJockeyId(),null, null, null, x.getluckFactor(), null, null, null);
            participants.add(bob);
        }
        Simulation sim = new Simulation(null, simulationDto.getName(), null, participants);
        return sim;
    }

    public SimulationSendDto simToSimSendDto(Simulation simulation){
       ArrayList<ParticipantSendDto> horseJockeyCombinations = new ArrayList<>();
       for(Participant p: simulation.getParticipants()){
           ParticipantSendDto bob = new ParticipantSendDto(p.getId(),p.getRank(),p.getHorseName(),p.getJockeyName(),p.getAvgSpeed(),p.getHorseSpeed(),p.getSkill(),p.getLuck());
           horseJockeyCombinations.add(bob);
       }
       SimulationSendDto sim = new SimulationSendDto(simulation.getId(), simulation.getName(),simulation.getCreated(), horseJockeyCombinations);
       return sim;
    }

    public LinkedList<SimulationSendDto> simListTosimSendDtoList(LinkedList<Simulation> simulations){
        LinkedList<SimulationSendDto> sendSims = new LinkedList<>();
        for(Simulation s: simulations){
            sendSims.add(simToSimSendDto(s));
        }
        return sendSims;
    }


}
