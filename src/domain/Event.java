package domain;

import java.time.LocalDateTime;

public class Event {
    private int id;
    private String title;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int organizerId;

    public Event(int id, String title, String location, LocalDateTime startTime, LocalDateTime endTime, int organizerId) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.organizerId = organizerId;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public int getOrganizerId() { return organizerId; }

    @Override
    public String toString() {
        return "Event{id=" + id + ", title='" + title + "', location='" + location + "', start=" + startTime +
                ", end=" + endTime + ", organizerId=" + organizerId + "}";
    }
}
