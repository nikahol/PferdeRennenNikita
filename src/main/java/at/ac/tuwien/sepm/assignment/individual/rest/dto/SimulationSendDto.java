package at.ac.tuwien.sepm.assignment.individual.rest.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class SimulationSendDto {
    private Integer id;
    private String name;
    private LocalDateTime created;
    private ArrayList<ParticipantSendDto> horseJockeyCombinations;

    public SimulationSendDto(Integer id, String name, LocalDateTime created, ArrayList<ParticipantSendDto> horseJockeyCombinations) {
        this.id = id;
        this.name = name;
        this.created = created;
        this.horseJockeyCombinations = horseJockeyCombinations;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public ArrayList<ParticipantSendDto> getHorseJockeyCombinations() {
        return horseJockeyCombinations;
    }

    public void setHorseJockeyCombinations(ArrayList<ParticipantSendDto> horseJockeyCombinations) {
        this.horseJockeyCombinations = horseJockeyCombinations;
    }

    @Override
    public String toString() {
        return "SimulationSendDto{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", created=" + created +
            ", horseJockeyCombinations=" + horseJockeyCombinations +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimulationSendDto that = (SimulationSendDto) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(created, that.created) &&
            Objects.equals(horseJockeyCombinations, that.horseJockeyCombinations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, created, horseJockeyCombinations);
    }
}
