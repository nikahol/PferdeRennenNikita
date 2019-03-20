package at.ac.tuwien.sepm.assignment.individual.entity;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class Simulation {
    private Integer id;
    private String name;
    private LocalDateTime created;
    private ArrayList<Participant> participants;

    public Simulation(Integer id, String name,LocalDateTime created, ArrayList<Participant> participants) {
        this.id = id;
        this.name = name;
        this.created = created;
        this.participants = participants;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Simulation that = (Simulation) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(created, that.created) &&
            Objects.equals(participants, that.participants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, created, participants);
    }

    @Override
    public String toString() {
        return "Simulation{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", created=" + created +
            ", participants=" + participants +
            '}';
    }
}
