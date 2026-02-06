package domain;
import java.util.Objects;

public class Event {
    private final int id;
    private final String title;
    private final String location;
    private final int organizerId;

    private Event(Builder b){
        id=b.id; title=b.title; location=b.location; organizerId=b.organizerId;
    }

    public int getId(){ return id; }
    public String getTitle(){ return title; }
    public String getLocation(){ return location; }
    public int getOrganizerId(){ return organizerId; }

    @Override public String toString(){ return id+" | "+title+" | "+location; }
    @Override public boolean equals(Object o){ return (o instanceof Event e) && e.id==id; }
    @Override public int hashCode(){ return Objects.hash(id); }

    public static class Builder {
        private int id; private String title; private String location; private int organizerId;
        public Builder id(int v){id=v; return this;}
        public Builder title(String v){title=v; return this;}
        public Builder location(String v){location=v; return this;}
        public Builder organizerId(int v){organizerId=v; return this;}
        public Event build(){ return new Event(this); }
    }
}
