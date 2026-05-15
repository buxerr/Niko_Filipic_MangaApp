package hr.algebra.mangaapp;

import hr.algebra.mangaapp.model.*;
import hr.algebra.mangaapp.model.enums.AuthorType;
import hr.algebra.mangaapp.model.enums.CharacterRole;
import hr.algebra.mangaapp.model.enums.MangaStatus;
import hr.algebra.mangaapp.model.enums.UserRole;
import hr.algebra.mangaapp.repository.*;
import hr.algebra.mangaapp.repository.search.MangaSearchCriteria;
import hr.algebra.mangaapp.repository.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public final class RepositorySmokeTest {

    private static final Logger log = LoggerFactory.getLogger(RepositorySmokeTest.class);

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

        log.info("=== GENRE TEST ===");
        Long genreId = genreRepository.create(
                new Genre("Test Genre " + suffix, "Created from RepositorySmokeTest")
        );
        Genre genre = genreRepository.findById(genreId).orElseThrow();
        log.info("Created genre: {}", genre);

        genre.setDescription("Updated genre description");
        genreRepository.update(genre);
        log.info("Updated genre: {}", genreRepository.findById(genreId).orElseThrow());

        log.info("Search genres:");
        genreRepository.search("Test Genre").forEach(g -> log.info("{}", g));


        log.info("=== PUBLISHER TEST ===");
        Long publisherId = publisherRepository.create(
                new Publisher("Test Publisher " + suffix)
        );
        Publisher publisher = publisherRepository.findById(publisherId).orElseThrow();
        log.info("Created publisher: {}", publisher);

        publisher.setName("Updated Publisher " + suffix);
        publisherRepository.update(publisher);
        publisher = publisherRepository.findById(publisherId).orElseThrow();
        log.info("Updated publisher: {}", publisher);

        log.info("Search publishers:");
        publisherRepository.search("Updated Publisher").forEach(p -> log.info("{}", p));


        log.info("=== AUTHOR TEST ===");
        Long authorId = authorRepository.create(
                new Author("Test", "Author" + suffix, AuthorType.MANGAKA)
        );
        Author author = authorRepository.findById(authorId).orElseThrow();
        log.info("Created author: {}", author);

        author.setFirstName("Updated");
        author.setLastName("Author" + suffix);
        authorRepository.update(author);
        author = authorRepository.findById(authorId).orElseThrow();
        log.info("Updated author: {}", author);

        log.info("Search authors:");
        authorRepository.search("Updated").forEach(a -> log.info("{}", a));


        log.info("=== STORY CHARACTER TEST ===");
        Long characterId = characterRepository.create(
                new StoryCharacter("Test", "Character" + suffix, CharacterRole.MAIN)
        );
        StoryCharacter character = characterRepository.findById(characterId).orElseThrow();
        log.info("Created character: {}", character);

        character.setFirstName("Updated");
        character.setLastName("Character" + suffix);
        characterRepository.update(character);
        character = characterRepository.findById(characterId).orElseThrow();
        log.info("Updated character: {}", character);

        log.info("Search characters:");
        characterRepository.search("Updated").forEach(c -> log.info("{}", c));


        log.info("=== USER TEST ===");
        String username = "testuser_" + suffix;

        Long userId = userRepository.create(
                new User(username, "temporary-password-hash", UserRole.USER)
        );

        User user = userRepository.findById(userId).orElseThrow();
        log.info("Created user: {}", user);

        log.info("Find by username:");
        log.info("{}", userRepository.findByUsername(username).orElseThrow());

        log.info("Username exists:");
        log.info("{}", userRepository.usernameExists(username));



        log.info("=== MANGA TEST ===");

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
        log.info("Created manga:");
        printManga(createdManga);

        createdManga.setDescription("Updated manga description");
        createdManga.setVolumes(2);
        mangaRepository.update(createdManga);

        Manga updatedManga = mangaRepository.findById(mangaId).orElseThrow();
        log.info("Updated manga:");
        printManga(updatedManga);

        log.info("Search manga by title:");
        MangaSearchCriteria criteria = new MangaSearchCriteria()
                .setTitle("Test Manga " + suffix);

        mangaRepository.search(criteria).forEach(RepositorySmokeTest::printManga);


        log.info("=== CLEANUP ===");

        mangaRepository.delete(mangaId);
        log.info("Deleted manga id: {}", mangaId);

        userRepository.delete(userId);
        log.info("Deleted user id: {}", userId);

        characterRepository.delete(characterId);
        log.info("Deleted character id: {}", characterId);

        authorRepository.delete(authorId);
        log.info("Deleted author id: {}", authorId);

        genreRepository.delete(genreId);
        log.info("Deleted genre id: {}", genreId);

        publisherRepository.delete(publisherId);
        log.info("Deleted publisher id: {}", publisherId);

        log.info("Repository smoke test finished successfully.");
    }

    private static void printManga(Manga manga) {
        log.info("{}", manga);
        log.info("Publisher: {}", manga.getPublisher());
        log.info("Genres: {}", manga.getGenres());
        log.info("Authors: {}", manga.getAuthors());
        log.info("Characters: {}", manga.getCharacters());
        log.info("Image path: {}", manga.getImagePath());
        log.info("-------------------------");
    }
}
