package at.ac.tuwien.sepm.assignment.individual.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ParticipantDto {

    @JsonProperty("horseId")
    private Integer horseId;
    @JsonProperty("jockeyId")
    private Integer jockeyId;
    @JsonProperty("luckFactor")
    private Double luckFactor;

    public ParticipantDto(){

    }
    public ParticipantDto(Integer horseId, Integer jockeyId, Double luckFactor) {
        this.horseId = horseId;
        this.jockeyId = jockeyId;
        this.luckFactor = luckFactor;
    }

    public Integer getHorseId() {
        return horseId;
    }

    public void setHorseId(Integer horseId) {
        this.horseId = horseId;
    }

    public Integer getJockeyId() {
        return jockeyId;
    }

    public void setJockeyId(Integer jockeyId) {
        this.jockeyId = jockeyId;
    }

    public Double getluckFactor() {
        return luckFactor;
    }

    public void setluckFactor(Double luckFactor) {
        this.luckFactor = luckFactor;
    }

    @Override
    public String toString() {
        return "ParticipantDto{" +
            "horseId=" + horseId +
            ", jockeyId=" + jockeyId +
            ", luckFactor=" + luckFactor +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantDto that = (ParticipantDto) o;
        return Objects.equals(horseId, that.horseId) &&
            Objects.equals(jockeyId, that.jockeyId) &&
            Objects.equals(luckFactor, that.luckFactor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(horseId, jockeyId, luckFactor);
    }
}
