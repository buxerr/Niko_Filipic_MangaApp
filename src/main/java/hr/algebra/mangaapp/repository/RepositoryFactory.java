package hr.algebra.mangaapp.repository;

import hr.algebra.mangaapp.repository.sql.SqlAdminRepository;
import hr.algebra.mangaapp.repository.sql.SqlAuthorRepository;
import hr.algebra.mangaapp.repository.sql.SqlGenreRepository;
import hr.algebra.mangaapp.repository.sql.SqlMangaRepository;
import hr.algebra.mangaapp.repository.sql.SqlPublisherRepository;
import hr.algebra.mangaapp.repository.sql.SqlStoryCharacterRepository;
import hr.algebra.mangaapp.repository.sql.SqlUserRepository;

public final class RepositoryFactory {

    private RepositoryFactory() {
    }

    public static GenreRepository getGenreRepository() {
        return new SqlGenreRepository();
    }

    public static PublisherRepository getPublisherRepository() {
        return new SqlPublisherRepository();
    }

    public static AuthorRepository getAuthorRepository() {
        return new SqlAuthorRepository();
    }

    public static StoryCharacterRepository getStoryCharacterRepository() {
        return new SqlStoryCharacterRepository();
    }

    public static MangaRepository getMangaRepository() {
        return new SqlMangaRepository();
    }

    public static UserRepository getUserRepository() {
        return new SqlUserRepository();
    }

    public static AdminRepository getAdminRepository() {
        return new SqlAdminRepository();
    }
}