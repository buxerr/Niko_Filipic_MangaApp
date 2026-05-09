package hr.algebra.mangaapp.service;

import hr.algebra.mangaapp.model.Author;
import hr.algebra.mangaapp.model.Genre;
import hr.algebra.mangaapp.model.Manga;
import hr.algebra.mangaapp.model.Publisher;
import hr.algebra.mangaapp.model.StoryCharacter;
import hr.algebra.mangaapp.model.enums.MangaStatus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MangaFormService {

    public MangaFormResult buildManga(
            Long id,
            String title,
            String description,
            String releaseYearText,
            String volumesText,
            String imagePath,
            MangaStatus status,
            Publisher publisher,
            Collection<StoryCharacter> selectedCharacters,
            Collection<Genre> selectedGenres,
            Collection<Author> selectedAuthors
    ) {
        if (title == null || title.isBlank()) {
            return MangaFormResult.failure("Title is required.");
        }

        Integer releaseYear = parseInteger(releaseYearText);
        if (releaseYear == null) {
            return MangaFormResult.failure("Release year is required and must be a number.");
        }

        Integer volumes = parseInteger(volumesText);
        if (volumes == null) {
            return MangaFormResult.failure("Volumes is required and must be a number.");
        }

        if (status == null) {
            return MangaFormResult.failure("Status is required.");
        }

        if (publisher == null) {
            return MangaFormResult.failure("Publisher is required.");
        }

        String normalizedDescription = description == null || description.isBlank()
                ? null
                : description.trim();

        String normalizedImagePath = imagePath == null || imagePath.isBlank()
                ? null
                : imagePath.trim();

        Set<StoryCharacter> characters = new HashSet<>(selectedCharacters);
        Set<Genre> genres = new HashSet<>(selectedGenres);
        Set<Author> authors = new HashSet<>(selectedAuthors);

        Manga manga = id == null
                ? new Manga(
                        title.trim(),
                        normalizedDescription,
                        releaseYear,
                        volumes,
                        publisher,
                        normalizedImagePath,
                        status,
                        characters,
                        genres,
                        authors
                )
                : new Manga(
                        id,
                        title.trim(),
                        normalizedDescription,
                        releaseYear,
                        volumes,
                        publisher,
                        normalizedImagePath,
                        status,
                        characters,
                        genres,
                        authors
                );

        return MangaFormResult.success(manga);
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public record MangaFormResult(Manga manga, String errorMessage) {

        private static MangaFormResult success(Manga manga) {
            return new MangaFormResult(manga, null);
        }

        private static MangaFormResult failure(String errorMessage) {
            return new MangaFormResult(null, errorMessage);
        }

        public boolean isSuccessful() {
            return manga != null;
        }
    }
}
