package hr.algebra.mangaapp.service;

import hr.algebra.mangaapp.model.Author;
import hr.algebra.mangaapp.model.Genre;
import hr.algebra.mangaapp.model.StoryCharacter;
import hr.algebra.mangaapp.model.enums.MangaStatus;

import java.util.Set;
import java.util.stream.Collectors;

public class MangaDisplayService {

    public String formatAuthors(Set<Author> authors) {
        if (authors == null || authors.isEmpty()) {
            return "";
        }

        return authors.stream()
                .map(Author::getFullName)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    public String formatGenres(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return "";
        }

        return genres.stream()
                .map(Genre::getName)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    public String formatCharacters(Set<StoryCharacter> characters) {
        if (characters == null || characters.isEmpty()) {
            return "";
        }

        return characters.stream()
                .map(StoryCharacter::getFullName)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    public String formatStatus(MangaStatus status) {
        if (status == null) {
            return "";
        }

        return switch (status) {
            case ONGOING -> "Ongoing";
            case COMPLETED -> "Completed";
            case HIATUS -> "Hiatus";
            case CANCELLED -> "Cancelled";
        };
    }
}
