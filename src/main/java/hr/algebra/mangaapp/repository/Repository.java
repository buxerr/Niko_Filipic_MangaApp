package hr.algebra.mangaapp.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {

    List<T> findAll();

    Optional<T> findById(Long id);

    Long create(T entity);

    void update(T entity);

    void delete(Long id);

    default boolean existsById(Long id) {
        return findById(id).isPresent();
    }

}
