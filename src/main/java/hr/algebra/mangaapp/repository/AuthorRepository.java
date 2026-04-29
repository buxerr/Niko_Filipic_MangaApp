package hr.algebra.mangaapp.repository;

import hr.algebra.mangaapp.model.Author;

import java.util.List;

public interface AuthorRepository extends Repository<Author> {

    List<Author> search(String query);
}