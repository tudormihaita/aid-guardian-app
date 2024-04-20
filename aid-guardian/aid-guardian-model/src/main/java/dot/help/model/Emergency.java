package dot.help.model;

import java.time.LocalDateTime;

public class Emergency extends Entity<Long>
{
    private User reporter;
    private LocalDateTime dateTime;
    private String description;
    private Status status;
    private FirstResponder firstResponder;
    private String location;

    public Emergency(User reporter, LocalDateTime dateTime, String description, Status status, FirstResponder firstResponder, String location) {
        this.reporter = reporter;
        this.dateTime = dateTime;
        this.description = description;
        this.status = status;
        this.firstResponder = firstResponder;
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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public FirstResponder getFirstResponder() {
        return firstResponder;
    }

    public void setFirstResponder(FirstResponder firstResponder) {
        this.firstResponder = firstResponder;
    }
}
