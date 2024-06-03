package dot.help.model;

import java.time.LocalDateTime;

public class Emergency extends Identifiable<Long> {
    private User reporter;
    private LocalDateTime reportedAt;
    private String description;
    private EmergencyStatus status;
    private FirstResponder responder;
    private double latitude;
    private double longitude;

    public Emergency()
    {
    }

    public Emergency(User reporter, LocalDateTime reportedAt, String description, EmergencyStatus status, FirstResponder responder, double latitude, double longitude) {
        this.reporter = reporter;
        this.reportedAt = reportedAt;
        this.description = description;
        this.status = status;
        this.responder = responder;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Emergency(User reporter, LocalDateTime reportedAt, String description, EmergencyStatus status, double latitude, double longitude) {
        this.reporter = reporter;
        this.reportedAt = reportedAt;
        this.description = description;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(LocalDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EmergencyStatus getStatus() {
        return status;
    }

    public void setStatus(EmergencyStatus status) {
        this.status = status;
    }

    public FirstResponder getResponder() {
        return responder;
    }

    public void setResponder(FirstResponder responder) {
        this.responder = responder;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
