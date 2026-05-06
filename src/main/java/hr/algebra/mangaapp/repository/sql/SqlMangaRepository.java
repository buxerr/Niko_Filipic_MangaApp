package hr.algebra.mangaapp.repository.sql;

import hr.algebra.mangaapp.exception.RepositoryException;
import hr.algebra.mangaapp.model.Author;
import hr.algebra.mangaapp.model.BaseEntity;
import hr.algebra.mangaapp.model.Genre;
import hr.algebra.mangaapp.model.Manga;
import hr.algebra.mangaapp.model.Publisher;
import hr.algebra.mangaapp.model.StoryCharacter;
import hr.algebra.mangaapp.model.enums.AuthorType;
import hr.algebra.mangaapp.model.enums.CharacterRole;
import hr.algebra.mangaapp.model.enums.MangaStatus;
import hr.algebra.mangaapp.repository.MangaRepository;
import hr.algebra.mangaapp.repository.search.MangaSearchCriteria;
import hr.algebra.mangaapp.util.DatabaseUtils;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SqlMangaRepository implements MangaRepository {

    @Override
    public List<Manga> findAll() {
        List<Manga> mangas = new ArrayList<>();
        String sql = "SELECT * FROM fn_find_all_mangas()";

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                Manga manga = mapManga(resultSet);
                loadRelations(manga);
                mangas.add(manga);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error while loading mangas", e);
        }

        return mangas;
    }

    @Override
    public Optional<Manga> findById(Long id) {
        String sql = "SELECT * FROM fn_find_manga_by_id(?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Manga manga = mapManga(resultSet);
                    loadRelations(manga);
                    return Optional.of(manga);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error while finding manga by id", e);
        }

        return Optional.empty();
    }

    @Override
    public Long create(Manga manga) {
        String sql = "SELECT fn_create_manga(?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, manga.getTitle());
            statement.setString(2, manga.getDescription());
            statement.setInt(3, manga.getReleaseYear());
            statement.setInt(4, manga.getVolumes());
            statement.setString(5, manga.getStatus().name());
            statement.setString(6, manga.getImagePath());

            if (manga.getPublisher() != null && manga.getPublisher().getId() != null) {
                statement.setLong(7, manga.getPublisher().getId());
            } else {
                statement.setNull(7, Types.BIGINT);
            }

            Long newMangaId;

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new RepositoryException("Manga was not created");
                }

                newMangaId = resultSet.getLong(1);
            }

            addRelations(newMangaId, manga);

            return newMangaId;

        } catch (SQLException e) {
            throw new RepositoryException("Error while creating manga", e);
        }
    }

    @Override
    public void update(Manga manga) {
        String sql = "CALL sp_update_manga(?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setLong(1, manga.getId());
            statement.setString(2, manga.getTitle());
            statement.setString(3, manga.getDescription());
            statement.setInt(4, manga.getReleaseYear());
            statement.setInt(5, manga.getVolumes());
            statement.setString(6, manga.getStatus().name());
            statement.setString(7, manga.getImagePath());

            if (manga.getPublisher() != null && manga.getPublisher().getId() != null) {
                statement.setLong(8, manga.getPublisher().getId());
            } else {
                statement.setNull(8, Types.BIGINT);
            }

            statement.execute();

            syncRelations(manga);

        } catch (SQLException e) {
            throw new RepositoryException("Error while updating manga", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "CALL sp_delete_manga(?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setLong(1, id);
            statement.execute();
        } catch (SQLException e) {
            throw new RepositoryException("Error while deleting manga", e);
        }
    }

    @Override
    public List<Manga> search(MangaSearchCriteria criteria) {
        List<Manga> mangas = new ArrayList<>();

        String sql = "SELECT * FROM fn_search_mangas(?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            if (criteria == null || criteria.getTitle() == null || criteria.getTitle().isBlank()) {
                statement.setNull(1, Types.VARCHAR);
            } else {
                statement.setString(1, criteria.getTitle());
            }

            setNullableLong(statement, 2, criteria != null ? criteria.getGenreId() : null);
            setNullableLong(statement, 3, criteria != null ? criteria.getAuthorId() : null);
            setNullableLong(statement, 4, criteria != null ? criteria.getPublisherId() : null);

            if (criteria == null || criteria.getStatus() == null) {
                statement.setNull(5, Types.VARCHAR);
            } else {
                statement.setString(5, criteria.getStatus().name());
            }

            setNullableInteger(statement, 6, criteria != null ? criteria.getReleaseYearFrom() : null);
            setNullableInteger(statement, 7, criteria != null ? criteria.getReleaseYearTo() : null);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Manga manga = mapManga(resultSet);
                    loadRelations(manga);
                    mangas.add(manga);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error while searching mangas", e);
        }

        return mangas;
    }

    @Override
    public void addGenre(Long mangaId, Long genreId) {
        callTwoParamProcedure("CALL sp_add_manga_genre(?, ?)", mangaId, genreId);
    }

    @Override
    public void removeGenre(Long mangaId, Long genreId) {
        callTwoParamProcedure("CALL sp_remove_manga_genre(?, ?)", mangaId, genreId);
    }

    @Override
    public void addAuthor(Long mangaId, Long authorId) {
        callTwoParamProcedure("CALL sp_add_manga_author(?, ?)", mangaId, authorId);
    }

    @Override
    public void removeAuthor(Long mangaId, Long authorId) {
        callTwoParamProcedure("CALL sp_remove_manga_author(?, ?)", mangaId, authorId);
    }

    @Override
    public void addCharacter(Long mangaId, Long characterId) {
        callTwoParamProcedure("CALL sp_add_manga_character(?, ?)", mangaId, characterId);
    }

    @Override
    public void removeCharacter(Long mangaId, Long characterId) {
        callTwoParamProcedure("CALL sp_remove_manga_character(?, ?)", mangaId, characterId);
    }

    private Manga mapManga(ResultSet resultSet) throws SQLException {
        Long publisherId = getNullableLong(resultSet, "publisher_id");
        Publisher publisher = null;

        if (publisherId != null) {
            publisher = new Publisher(
                    publisherId,
                    resultSet.getString("publisher_name")
            );
        }

        return new Manga(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                resultSet.getInt("release_year"),
                resultSet.getInt("volumes"),
                publisher,
                resultSet.getString("image_path"),
                MangaStatus.valueOf(resultSet.getString("status")),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>()
        );
    }

    private void loadRelations(Manga manga) {
        manga.setGenres(loadGenres(manga.getId()));
        manga.setAuthors(loadAuthors(manga.getId()));
        manga.setCharacters(loadCharacters(manga.getId()));
    }

    private Set<Genre> loadGenres(Long mangaId) {
        Set<Genre> genres = new HashSet<>();
        String sql = "SELECT * FROM fn_find_genres_by_manga_id(?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, mangaId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    genres.add(new Genre(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("description")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error while loading manga genres", e);
        }

        return genres;
    }

    private Set<Author> loadAuthors(Long mangaId) {
        Set<Author> authors = new HashSet<>();
        String sql = "SELECT * FROM fn_find_authors_by_manga_id(?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, mangaId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    authors.add(new Author(
                            resultSet.getLong("id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            AuthorType.valueOf(resultSet.getString("orientation"))
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error while loading manga authors", e);
        }

        return authors;
    }

    private Set<StoryCharacter> loadCharacters(Long mangaId) {
        Set<StoryCharacter> characters = new HashSet<>();
        String sql = "SELECT * FROM fn_find_characters_by_manga_id(?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, mangaId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    characters.add(new StoryCharacter(
                            resultSet.getLong("id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            CharacterRole.valueOf(resultSet.getString("role"))
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error while loading manga characters", e);
        }

        return characters;
    }

    private void addRelations(Long mangaId, Manga manga) {
        if (manga.getGenres() != null) {
            manga.getGenres().stream()
                    .map(Genre::getId)
                    .filter(Objects::nonNull)
                    .forEach(genreId -> addGenre(mangaId, genreId));
        }

        if (manga.getAuthors() != null) {
            manga.getAuthors().stream()
                    .map(Author::getId)
                    .filter(Objects::nonNull)
                    .forEach(authorId -> addAuthor(mangaId, authorId));
        }

        if (manga.getCharacters() != null) {
            manga.getCharacters().stream()
                    .map(StoryCharacter::getId)
                    .filter(Objects::nonNull)
                    .forEach(characterId -> addCharacter(mangaId, characterId));
        }
    }

    private void syncRelations(Manga manga) {
        Long mangaId = manga.getId();

        Set<Long> currentGenreIds = loadGenres(mangaId).stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        Set<Long> targetGenreIds = extractIds(manga.getGenres());

        syncIds(
                mangaId,
                currentGenreIds,
                targetGenreIds,
                this::addGenre,
                this::removeGenre
        );

        Set<Long> currentAuthorIds = loadAuthors(mangaId).stream()
                .map(Author::getId)
                .collect(Collectors.toSet());

        Set<Long> targetAuthorIds = extractIds(manga.getAuthors());

        syncIds(
                mangaId,
                currentAuthorIds,
                targetAuthorIds,
                this::addAuthor,
                this::removeAuthor
        );

        Set<Long> currentCharacterIds = loadCharacters(mangaId).stream()
                .map(StoryCharacter::getId)
                .collect(Collectors.toSet());

        Set<Long> targetCharacterIds = extractIds(manga.getCharacters());

        syncIds(
                mangaId,
                currentCharacterIds,
                targetCharacterIds,
                this::addCharacter,
                this::removeCharacter
        );
    }

    private <E extends BaseEntity> Set<Long> extractIds(Set<E> entities) {
        if (entities == null) {
            return new HashSet<>();
        }

        return entities.stream()
                .map(BaseEntity::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private void syncIds(
            Long mangaId,
            Set<Long> currentIds,
            Set<Long> targetIds,
            RelationOperation addOperation,
            RelationOperation removeOperation
    ) {
        for (Long currentId : currentIds) {
            if (!targetIds.contains(currentId)) {
                removeOperation.apply(mangaId, currentId);
            }
        }

        for (Long targetId : targetIds) {
            if (!currentIds.contains(targetId)) {
                addOperation.apply(mangaId, targetId);
            }
        }
    }

    private void callTwoParamProcedure(String sql, Long firstId, Long secondId) {
        try (
                Connection connection = DatabaseUtils.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setLong(1, firstId);
            statement.setLong(2, secondId);
            statement.execute();
        } catch (SQLException e) {
            throw new RepositoryException("Error while executing relation procedure", e);
        }
    }

    private void setNullableLong(PreparedStatement statement, int index, Long value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.BIGINT);
        } else {
            statement.setLong(index, value);
        }
    }

    private void setNullableInteger(PreparedStatement statement, int index, Integer value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }

    private Long getNullableLong(ResultSet resultSet, String columnName) throws SQLException {
        long value = resultSet.getLong(columnName);

        if (resultSet.wasNull()) {
            return null;
        }

        return value;
    }

    @FunctionalInterface
    private interface RelationOperation {
        void apply(Long mangaId, Long relatedEntityId);
    }
}