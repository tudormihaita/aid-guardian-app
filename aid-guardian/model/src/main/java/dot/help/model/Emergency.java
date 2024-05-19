package dot.help.model;

import java.time.LocalDateTime;

public class Emergency extends Identifiable<Long> {
    private User reporter;
    private LocalDateTime reportedAt;
    private String description;
    private EmergencyStatus status;
    private FirstResponder responder;
    private String location;

    public Emergency()
    {

    }
    public Emergency(User reporter, LocalDateTime reportedAt, String description, EmergencyStatus status, FirstResponder responder, String location) {
        this.reporter = reporter;
        this.reportedAt = reportedAt;
        this.description = description;
        this.status = status;
        this.responder = responder;
        this.location = location;
    }

    public Emergency(User reporter, LocalDateTime reportedAt, String description, EmergencyStatus status, String location) {
        this.reporter = reporter;
        this.reportedAt = reportedAt;
        this.description = description;
        this.status = status;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
}
