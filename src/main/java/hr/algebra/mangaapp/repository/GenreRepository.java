package hr.algebra.mangaapp.repository;

import hr.algebra.mangaapp.model.Genre;

import java.util.List;

public interface GenreRepository extends Repository<Genre> {

    List<Genre> search(String query);

    boolean existsByName(String name);

}