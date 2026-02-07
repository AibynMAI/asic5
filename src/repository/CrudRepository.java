package repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    T save(T entity);
    Optional<T> findById(int id);
    List<T> findAll();
    void deleteById(int id);

    default boolean existsById(int id) {
        return findById(id).isPresent();
    }
}
