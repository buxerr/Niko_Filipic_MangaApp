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
        return RepositoryHolder.GENRE_REPOSITORY;
    }

    public static PublisherRepository getPublisherRepository() {
        return RepositoryHolder.PUBLISHER_REPOSITORY;
    }

    public static AuthorRepository getAuthorRepository() {
        return RepositoryHolder.AUTHOR_REPOSITORY;
    }

    public static StoryCharacterRepository getStoryCharacterRepository() {
        return RepositoryHolder.STORY_CHARACTER_REPOSITORY;
    }

    public static MangaRepository getMangaRepository() {
        return RepositoryHolder.MANGA_REPOSITORY;
    }

    public static UserRepository getUserRepository() {
        return RepositoryHolder.USER_REPOSITORY;
    }

    public static AdminRepository getAdminRepository() {
        return RepositoryHolder.ADMIN_REPOSITORY;
    }

    private static final class RepositoryHolder {

        private static final GenreRepository GENRE_REPOSITORY = new SqlGenreRepository();
        private static final PublisherRepository PUBLISHER_REPOSITORY = new SqlPublisherRepository();
        private static final AuthorRepository AUTHOR_REPOSITORY = new SqlAuthorRepository();
        private static final StoryCharacterRepository STORY_CHARACTER_REPOSITORY = new SqlStoryCharacterRepository();
        private static final MangaRepository MANGA_REPOSITORY = new SqlMangaRepository();
        private static final UserRepository USER_REPOSITORY = new SqlUserRepository();
        private static final AdminRepository ADMIN_REPOSITORY = new SqlAdminRepository();

        private RepositoryHolder() {
        }
    }
}
