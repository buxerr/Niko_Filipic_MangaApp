package hr.algebra.mangaapp.xml;

import hr.algebra.mangaapp.model.Author;
import hr.algebra.mangaapp.model.BaseEntity;
import hr.algebra.mangaapp.model.Genre;
import hr.algebra.mangaapp.model.Manga;
import hr.algebra.mangaapp.model.Publisher;
import hr.algebra.mangaapp.model.StoryCharacter;
import hr.algebra.mangaapp.model.User;
import hr.algebra.mangaapp.repository.AuthorRepository;
import hr.algebra.mangaapp.repository.GenreRepository;
import hr.algebra.mangaapp.repository.MangaRepository;
import hr.algebra.mangaapp.repository.PublisherRepository;
import hr.algebra.mangaapp.repository.RepositoryFactory;
import hr.algebra.mangaapp.repository.StoryCharacterRepository;
import hr.algebra.mangaapp.repository.UserRepository;
import hr.algebra.mangaapp.xml.dto.AuthorBackupXmlDto;
import hr.algebra.mangaapp.xml.dto.DatabaseBackupXmlDto;
import hr.algebra.mangaapp.xml.dto.GenreBackupXmlDto;
import hr.algebra.mangaapp.xml.dto.MangaBackupXmlDto;
import hr.algebra.mangaapp.xml.dto.PublisherBackupXmlDto;
import hr.algebra.mangaapp.xml.dto.StoryCharacterBackupXmlDto;
import hr.algebra.mangaapp.xml.dto.UserBackupXmlDto;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class DatabaseBackupXmlService {

    private final PublisherRepository publisherRepository =
            RepositoryFactory.getPublisherRepository();

    private final AuthorRepository authorRepository =
            RepositoryFactory.getAuthorRepository();

    private final GenreRepository genreRepository =
            RepositoryFactory.getGenreRepository();

    private final StoryCharacterRepository characterRepository =
            RepositoryFactory.getStoryCharacterRepository();

    private final MangaRepository mangaRepository =
            RepositoryFactory.getMangaRepository();

    private final UserRepository userRepository =
            RepositoryFactory.getUserRepository();

    public BackupResult exportBackup(File destinationFile) {
        if (destinationFile == null) {
            throw new IllegalArgumentException("Destination file is required.");
        }

        List<PublisherBackupXmlDto> publishers = sortedById(publisherRepository.findAll()).stream()
                .map(this::mapPublisher)
                .toList();

        List<AuthorBackupXmlDto> authors = sortedById(authorRepository.findAll()).stream()
                .map(this::mapAuthor)
                .toList();

        List<GenreBackupXmlDto> genres = sortedById(genreRepository.findAll()).stream()
                .map(this::mapGenre)
                .toList();

        List<StoryCharacterBackupXmlDto> characters = sortedById(characterRepository.findAll()).stream()
                .map(this::mapCharacter)
                .toList();

        List<MangaBackupXmlDto> mangas = sortedById(mangaRepository.findAll()).stream()
                .map(this::mapManga)
                .toList();

        List<UserBackupXmlDto> users = sortedById(userRepository.findAll()).stream()
                .map(this::mapUser)
                .toList();

        DatabaseBackupXmlDto backup = new DatabaseBackupXmlDto(
                OffsetDateTime.now().toString(),
                publishers,
                authors,
                genres,
                characters,
                mangas,
                users
        );

        try {
            JAXBContext context = JAXBContext.newInstance(DatabaseBackupXmlDto.class);
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(backup, destinationFile);

            return new BackupResult(
                    publishers.size(),
                    authors.size(),
                    genres.size(),
                    characters.size(),
                    mangas.size(),
                    users.size()
            );

        } catch (Exception e) {
            throw new RuntimeException("Error while exporting database backup to XML", e);
        }
    }

    private PublisherBackupXmlDto mapPublisher(Publisher publisher) {
        return new PublisherBackupXmlDto(
                publisher.getId(),
                publisher.getName()
        );
    }

    private AuthorBackupXmlDto mapAuthor(Author author) {
        return new AuthorBackupXmlDto(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                author.getOrientation() != null ? author.getOrientation().name() : null
        );
    }

    private GenreBackupXmlDto mapGenre(Genre genre) {
        return new GenreBackupXmlDto(
                genre.getId(),
                genre.getName(),
                genre.getDescription()
        );
    }

    private StoryCharacterBackupXmlDto mapCharacter(StoryCharacter character) {
        return new StoryCharacterBackupXmlDto(
                character.getId(),
                character.getFirstName(),
                character.getLastName(),
                character.getRole() != null ? character.getRole().name() : null
        );
    }

    private MangaBackupXmlDto mapManga(Manga manga) {
        Long publisherId = manga.getPublisher() != null
                ? manga.getPublisher().getId()
                : null;

        return new MangaBackupXmlDto(
                manga.getId(),
                manga.getTitle(),
                manga.getDescription(),
                manga.getReleaseYear(),
                manga.getVolumes(),
                manga.getStatus() != null ? manga.getStatus().name() : null,
                manga.getImagePath(),
                publisherId,
                extractSortedIds(manga.getAuthors()),
                extractSortedIds(manga.getGenres()),
                extractSortedIds(manga.getCharacters())
        );
    }

    private UserBackupXmlDto mapUser(User user) {
        return new UserBackupXmlDto(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getRole() != null ? user.getRole().name() : null
        );
    }

    private <T extends BaseEntity> List<T> sortedById(Collection<T> entities) {
        return entities.stream()
                .sorted(Comparator.comparing(
                        BaseEntity::getId,
                        Comparator.nullsLast(Long::compareTo)
                ))
                .toList();
    }

    private List<Long> extractSortedIds(Collection<? extends BaseEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(BaseEntity::getId)
                .filter(Objects::nonNull)
                .sorted()
                .toList();
    }

    public record BackupResult(
            int publishers,
            int authors,
            int genres,
            int characters,
            int mangas,
            int users
    ) {

        public String toLogDetails() {
            return "publishers=" + publishers
                    + ", authors=" + authors
                    + ", genres=" + genres
                    + ", characters=" + characters
                    + ", mangas=" + mangas
                    + ", users=" + users;
        }

        public String toUserMessage() {
            return """
                    XML database backup exported successfully.

                    Publishers: %d
                    Authors: %d
                    Genres: %d
                    Characters: %d
                    Manga: %d
                    Users: %d
                    """.formatted(
                    publishers,
                    authors,
                    genres,
                    characters,
                    mangas,
                    users
            );
        }
    }
}
