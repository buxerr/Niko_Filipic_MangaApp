package hr.algebra.mangaapp;

import hr.algebra.mangaapp.model.*;
import hr.algebra.mangaapp.model.enums.AuthorType;
import hr.algebra.mangaapp.model.enums.CharacterRole;
import hr.algebra.mangaapp.model.enums.MangaStatus;
import hr.algebra.mangaapp.model.enums.UserRole;
import hr.algebra.mangaapp.repository.*;
import hr.algebra.mangaapp.repository.search.MangaSearchCriteria;
import hr.algebra.mangaapp.repository.sql.*;

import java.util.HashSet;
import java.util.Set;

public final class RepositorySmokeTest {

    private RepositorySmokeTest() {
    }

    public static void main(String[] args) {
        GenreRepository genreRepository = new SqlGenreRepository();
        PublisherRepository publisherRepository = new SqlPublisherRepository();
        AuthorRepository authorRepository = new SqlAuthorRepository();
        StoryCharacterRepository characterRepository = new SqlStoryCharacterRepository();
        UserRepository userRepository = new SqlUserRepository();
        MangaRepository mangaRepository = new SqlMangaRepository();

        String suffix = String.valueOf(System.currentTimeMillis());

        System.out.println("=== GENRE TEST ===");
        Long genreId = genreRepository.create(
                new Genre("Test Genre " + suffix, "Created from RepositorySmokeTest")
        );
        Genre genre = genreRepository.findById(genreId).orElseThrow();
        System.out.println("Created genre: " + genre);

        genre.setDescription("Updated genre description");
        genreRepository.update(genre);
        System.out.println("Updated genre: " + genreRepository.findById(genreId).orElseThrow());

        System.out.println("Search genres:");
        genreRepository.search("Test Genre").forEach(System.out::println);

        System.out.println("=== PUBLISHER TEST ===");
        Long publisherId = publisherRepository.create(
                new Publisher("Test Publisher " + suffix)
        );
        Publisher publisher = publisherRepository.findById(publisherId).orElseThrow();
        System.out.println("Created publisher: " + publisher);

        publisher.setName("Updated Publisher " + suffix);
        publisherRepository.update(publisher);
        publisher = publisherRepository.findById(publisherId).orElseThrow();
        System.out.println("Updated publisher: " + publisher);

        System.out.println("Search publishers:");
        publisherRepository.search("Updated Publisher").forEach(System.out::println);

        System.out.println("=== AUTHOR TEST ===");
        Long authorId = authorRepository.create(
                new Author("Test", "Author" + suffix, AuthorType.MANGAKA)
        );
        Author author = authorRepository.findById(authorId).orElseThrow();
        System.out.println("Created author: " + author);

        author.setFirstName("Updated");
        author.setLastName("Author" + suffix);
        authorRepository.update(author);
        author = authorRepository.findById(authorId).orElseThrow();
        System.out.println("Updated author: " + author);

        System.out.println("Search authors:");
        authorRepository.search("Updated").forEach(System.out::println);

        System.out.println("=== STORY CHARACTER TEST ===");
        Long characterId = characterRepository.create(
                new StoryCharacter("Test", "Character" + suffix, CharacterRole.MAIN)
        );
        StoryCharacter character = characterRepository.findById(characterId).orElseThrow();
        System.out.println("Created character: " + character);

        character.setFirstName("Updated");
        character.setLastName("Character" + suffix);
        characterRepository.update(character);
        character = characterRepository.findById(characterId).orElseThrow();
        System.out.println("Updated character: " + character);

        System.out.println("Search characters:");
        characterRepository.search("Updated").forEach(System.out::println);

        System.out.println("=== USER TEST ===");
        String username = "testuser_" + suffix;

        Long userId = userRepository.create(
                new User(username, "temporary-password-hash", UserRole.USER)
        );

        User user = userRepository.findById(userId).orElseThrow();
        System.out.println("Created user: " + user);

        System.out.println("Find by username:");
        System.out.println(userRepository.findByUsername(username).orElseThrow());

        System.out.println("Username exists:");
        System.out.println(userRepository.usernameExists(username));

        System.out.println("=== MANGA TEST ===");

        Manga manga = new Manga(
                "Test Manga " + suffix,
                "Created from RepositorySmokeTest",
                2026,
                1,
                publisher,
                "assets/images/test-manga-" + suffix + ".jpg",
                MangaStatus.ONGOING,
                new HashSet<>(Set.of(character)),
                new HashSet<>(Set.of(genre)),
                new HashSet<>(Set.of(author))
        );

        Long mangaId = mangaRepository.create(manga);

        Manga createdManga = mangaRepository.findById(mangaId).orElseThrow();
        System.out.println("Created manga:");
        printManga(createdManga);

        createdManga.setDescription("Updated manga description");
        createdManga.setVolumes(2);
        mangaRepository.update(createdManga);

        Manga updatedManga = mangaRepository.findById(mangaId).orElseThrow();
        System.out.println("Updated manga:");
        printManga(updatedManga);

        System.out.println("Search manga by title:");
        MangaSearchCriteria criteria = new MangaSearchCriteria()
                .setTitle("Test Manga " + suffix);

        mangaRepository.search(criteria).forEach(RepositorySmokeTest::printManga);

        System.out.println("=== CLEANUP ===");

        mangaRepository.delete(mangaId);
        System.out.println("Deleted manga id: " + mangaId);

        userRepository.delete(userId);
        System.out.println("Deleted user id: " + userId);

        characterRepository.delete(characterId);
        System.out.println("Deleted character id: " + characterId);

        authorRepository.delete(authorId);
        System.out.println("Deleted author id: " + authorId);

        genreRepository.delete(genreId);
        System.out.println("Deleted genre id: " + genreId);

        publisherRepository.delete(publisherId);
        System.out.println("Deleted publisher id: " + publisherId);

        System.out.println("Repository smoke test finished successfully.");
    }

    private static void printManga(Manga manga) {
        System.out.println(manga);
        System.out.println("Publisher: " + manga.getPublisher());
        System.out.println("Genres: " + manga.getGenres());
        System.out.println("Authors: " + manga.getAuthors());
        System.out.println("Characters: " + manga.getCharacters());
        System.out.println("Image path: " + manga.getImagePath());
        System.out.println("-------------------------");
    }
}
