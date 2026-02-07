package repository;

import domain.Event;

public interface EventRepository extends CrudRepository<Event> {
    void updateLocation(int eventId, String location);
}
