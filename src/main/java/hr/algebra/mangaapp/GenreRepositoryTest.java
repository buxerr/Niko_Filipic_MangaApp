package hr.algebra.mangaapp;

import hr.algebra.mangaapp.model.Genre;
import hr.algebra.mangaapp.repository.GenreRepository;
import hr.algebra.mangaapp.repository.sql.SqlGenreRepository;

public class GenreRepositoryTest {

    public static void main(String[] args) {
        GenreRepository genreRepository = new SqlGenreRepository();

        Long id = genreRepository.create(
                new Genre("Repository Test", "Created from Java")
        );

        System.out.println("Created genre id: " + id);

        System.out.println("All genres:");
        genreRepository.findAll().forEach(System.out::println);

        Genre genre = genreRepository.findById(id).orElseThrow();
        genre.setDescription("Updated from Java");
        genreRepository.update(genre);

        System.out.println("After update:");
        genreRepository.findAll().forEach(System.out::println);

        genreRepository.delete(id);

        System.out.println("After delete:");
        genreRepository.findAll().forEach(System.out::println);
    }
}