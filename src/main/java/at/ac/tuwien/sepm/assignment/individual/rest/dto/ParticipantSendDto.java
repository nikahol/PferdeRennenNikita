package at.ac.tuwien.sepm.assignment.individual.rest.dto;

import java.util.Objects;

public class ParticipantSendDto {
    private Integer id;
    private Integer rank;
    private String horseName;
    private String jockeyName;
    private Double avgSpeed;
    private Double horseSpeed;
    private Double skill;
    private Double luck;

    public ParticipantSendDto(Integer id, Integer rank, String horseName, String jockeyName, Double avgSpeed, Double horseSpeed, Double skill, Double luck) {
        this.id = id;
        this.rank = rank;
        this.horseName = horseName;
        this.jockeyName = jockeyName;
        this.avgSpeed = avgSpeed;
        this.horseSpeed = horseSpeed;
        this.skill = skill;
        this.luck = luck;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getHorseName() {
        return horseName;
    }

    public void setHorseName(String horseName) {
        this.horseName = horseName;
    }

    public String getJockeyName() {
        return jockeyName;
    }

    public void setJockeyName(String jockeyName) {
        this.jockeyName = jockeyName;
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Double getHorseSpeed() {
        return horseSpeed;
    }

    public void setHorseSpeed(Double horseSpeed) {
        this.horseSpeed = horseSpeed;
    }

    public Double getSkill() {
        return skill;
    }

    public void setSkill(Double skill) {
        this.skill = skill;
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
        ParticipantSendDto that = (ParticipantSendDto) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(rank, that.rank) &&
            Objects.equals(horseName, that.horseName) &&
            Objects.equals(jockeyName, that.jockeyName) &&
            Objects.equals(avgSpeed, that.avgSpeed) &&
            Objects.equals(horseSpeed, that.horseSpeed) &&
            Objects.equals(skill, that.skill) &&
            Objects.equals(luck, that.luck);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rank, horseName, jockeyName, avgSpeed, horseSpeed, skill, luck);
    }

    @Override
    public String toString() {
        return "ParticipantSendDto{" +
            "id=" + id +
            ", rank=" + rank +
            ", horseName='" + horseName + '\'' +
            ", jockeyName='" + jockeyName + '\'' +
            ", avgSpeed=" + avgSpeed +
            ", horseSpeed=" + horseSpeed +
            ", skill=" + skill +
            ", luck=" + luck +
            '}';
    }
}
