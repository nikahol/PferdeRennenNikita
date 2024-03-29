package at.ac.tuwien.sepm.assignment.individual.entity;



import java.time.LocalDateTime;
import java.util.Objects;

public class Participant {
    private Integer id;
    private Integer horseId;
    private String horseName;
    private String jockeyName;
    private Integer jockeyId;
    private Integer rank;
    private Double avgSpeed;
    private Double horseSpeed;
    private Double luck;
    private Double skill;
    private LocalDateTime horseUpdate;
    private LocalDateTime jockeyUpdate;

    public Participant(Integer id, Integer horseId, String horseName, String jockeyName, Integer jockeyId, Integer rank, Double avgSpeed, Double horseSpeed, Double luck, Double skill, LocalDateTime horseUpdate, LocalDateTime jockeyUpdate) {
        this.id = id;
        this.horseId = horseId;
        this.horseName = horseName;
        this.jockeyName = jockeyName;
        this.jockeyId = jockeyId;
        this.rank = rank;
        this.avgSpeed = avgSpeed;
        this.horseSpeed = horseSpeed;
        this.luck = luck;
        this.skill = skill;
        this.horseUpdate = horseUpdate;
        this.jockeyUpdate = jockeyUpdate;
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

    public Double getLuck() {
        return luck;
    }

    public void setLuck(Double luck) {
        this.luck = luck;
    }

    public Double getSkill() {
        return skill;
    }

    public void setSkill(Double skill) {
        this.skill = skill;
    }

    public LocalDateTime getHorseUpdate() {
        return horseUpdate;
    }

    public void setHorseUpdate(LocalDateTime horseUpdate) {
        this.horseUpdate = horseUpdate;
    }

    public LocalDateTime getJockeyUpdate() {
        return jockeyUpdate;
    }

    public void setJockeyUpdate(LocalDateTime jockeyUpdate) {
        this.jockeyUpdate = jockeyUpdate;
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

    @Override
    public String toString() {
        return "Participant{" +
            "id=" + id +
            ", horseId=" + horseId +
            ", horseName='" + horseName + '\'' +
            ", jockeyName='" + jockeyName + '\'' +
            ", jockeyId=" + jockeyId +
            ", rank=" + rank +
            ", avgSpeed=" + avgSpeed +
            ", horseSpeed=" + horseSpeed +
            ", luck=" + luck +
            ", skill=" + skill +
            ", horseUpdate=" + horseUpdate +
            ", jockeyUpdate=" + jockeyUpdate +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(horseId, that.horseId) &&
            Objects.equals(horseName, that.horseName) &&
            Objects.equals(jockeyName, that.jockeyName) &&
            Objects.equals(jockeyId, that.jockeyId) &&
            Objects.equals(rank, that.rank) &&
            Objects.equals(avgSpeed, that.avgSpeed) &&
            Objects.equals(horseSpeed, that.horseSpeed) &&
            Objects.equals(luck, that.luck) &&
            Objects.equals(skill, that.skill) &&
            Objects.equals(horseUpdate, that.horseUpdate) &&
            Objects.equals(jockeyUpdate, that.jockeyUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, horseId, horseName, jockeyName, jockeyId, rank, avgSpeed, horseSpeed, luck, skill, horseUpdate, jockeyUpdate);
    }
}
