package dot.help.model;

import java.time.LocalDate;

public class Profile extends Entity<Long> {
    private User user;
    private String firstName;
    private String lastName;
    private GenderType gender;
    private LocalDate birthDate;
    private BloodGroupType bloodGroup;
    private Double height;
    private Double weight;
    private String medicalHistory;
    private Double score;

    public Profile(User user, String firstName, String lastName, GenderType gender, LocalDate birthDate, BloodGroupType bloodGroup, Double height, Double weight, String medicalHistory, Double score) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.bloodGroup = bloodGroup;
        this.height = height;
        this.weight = weight;
        this.medicalHistory = medicalHistory;
        this.score = score;
    }

    public User getUser() {
        return user;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public GenderType getGender() {
        return gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public BloodGroupType getBloodGroup() {
        return bloodGroup;
    }

    public Double getHeight() {
        return height;
    }

    public Double getWeight() {
        return weight;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public Double getScore() {
        return score;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(GenderType gender) {
        this.gender = gender;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setBloodGroup(BloodGroupType bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
