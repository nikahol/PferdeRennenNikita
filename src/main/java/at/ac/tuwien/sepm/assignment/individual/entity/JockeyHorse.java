package at.ac.tuwien.sepm.assignment.individual.entity;

import java.util.Objects;

public class JockeyHorse {
    private Horse horse;
    private Jockey jockey;
    private Double luck;

    public JockeyHorse(Horse horse, Jockey jockey, Double luck) {
        this.horse = horse;
        this.jockey = jockey;
        this.luck = luck;
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

    public Double getLuck() {
        return luck;
    }

    public void setLuck(Double luck) {
        this.luck = luck;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JockeyHorse that = (JockeyHorse) o;
        return Objects.equals(horse, that.horse) &&
            Objects.equals(jockey, that.jockey) &&
            Objects.equals(luck, that.luck);
    }

    @Override
    public int hashCode() {
        return Objects.hash(horse, jockey, luck);
    }

    @Override
    public String toString() {
        return "JockeyHorse{" +
            "horse=" + horse +
            ", jockey=" + jockey +
            ", luck=" + luck +
            '}';
    }
}
