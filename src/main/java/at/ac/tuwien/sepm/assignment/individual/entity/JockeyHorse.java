package at.ac.tuwien.sepm.assignment.individual.entity;

import java.util.Objects;

public class JockeyHorse {
    private Horse horse;
    private Jockey jockey;

    public JockeyHorse(Horse horse, Jockey jockey) {
        this.horse = horse;
        this.jockey = jockey;
    }

    public Horse getHorse() {
        return horse;
    }

    public void setHorse(Horse horse) {
        this.horse = horse;
    }

    public Jockey getJockey() {
        return jockey;
    }

    public void setJockey(Jockey jockey) {
        this.jockey = jockey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JockeyHorse that = (JockeyHorse) o;
        return Objects.equals(horse, that.horse) &&
            Objects.equals(jockey, that.jockey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(horse, jockey);
    }

    @Override
    public String toString() {
        return "JockeyHorse{" +
            "horse=" + horse +
            ", jockey=" + jockey +
            '}';
    }
}
