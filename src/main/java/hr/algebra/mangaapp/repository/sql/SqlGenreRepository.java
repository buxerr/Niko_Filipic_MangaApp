package hr.algebra.mangaapp.repository.sql;

import hr.algebra.mangaapp.model.Genre;
import hr.algebra.mangaapp.repository.GenreRepository;
import hr.algebra.mangaapp.util.DatabaseUtils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlGenreRepository implements GenreRepository {

    @Override
    public List<Genre> findAll() {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT * FROM fn_find_all_genres()";

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                genres.add(mapGenre(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while loading genres", e);
        }

        return genres;
    }

    @Override
    public Optional<Genre> findById(Long id) {
        String sql = "SELECT * FROM fn_find_genre_by_id(?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapGenre(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding genre by id", e);
        }

        return Optional.empty();
    }

    @Override
    public Long create(Genre genre) {
        String sql = "SELECT fn_create_genre(?, ?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, genre.getName());
            statement.setString(2, genre.getDescription());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while creating genre", e);
        }

        throw new RuntimeException("Genre was not created");
    }

    @Override
    public void update(Genre genre) {
        String sql = "CALL sp_update_genre(?, ?, ?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setLong(1, genre.getId());
            statement.setString(2, genre.getName());
            statement.setString(3, genre.getDescription());

            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating genre", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "CALL sp_delete_genre(?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setLong(1, id);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting genre", e);
        }
    }

    @Override
    public List<Genre> search(String query) {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT * FROM fn_search_genres(?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, query);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    genres.add(mapGenre(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while searching genres", e);
        }

        return genres;
    }

    private Genre mapGenre(ResultSet resultSet) throws SQLException {
        return new Genre(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("description")
        );
    }
}