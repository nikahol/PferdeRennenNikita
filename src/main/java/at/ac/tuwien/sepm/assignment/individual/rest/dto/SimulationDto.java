package at.ac.tuwien.sepm.assignment.individual.rest.dto;

import at.ac.tuwien.sepm.assignment.individual.entity.Participant;
import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class SimulationDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("simulationParticipants")
    private LinkedList<ParticipantDto> simulationParticipants;


    public SimulationDto (){

    }
    public SimulationDto(String name, LinkedList<ParticipantDto> participants) {
        this.name = name;
        this.simulationParticipants = participants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<ParticipantDto> getSimulationParticipants() {
        return simulationParticipants;
    }

    public void setSimulationParticipants(LinkedList<ParticipantDto> simulationParticipants) {
        this.simulationParticipants = simulationParticipants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimulationDto that = (SimulationDto) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(simulationParticipants, that.simulationParticipants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, simulationParticipants);
    }

    @Override
    public String toString() {
        return "SimulationDto{" +
            "name='" + name + '\'' +
            ", simulationParticipants=" + simulationParticipants +
            '}';
    }
}



