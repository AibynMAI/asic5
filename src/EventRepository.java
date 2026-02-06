package repository;
import domain.Event;
import java.util.*;

public interface EventRepository {
    Event save(Event e);
    Optional<Event> findById(int id);
    List<Event> findAll();
    void delete(int id);

    default void requirePositive(int id){
        if(id <= 0) throw new IllegalArgumentException("id must be positive");
    }
}
