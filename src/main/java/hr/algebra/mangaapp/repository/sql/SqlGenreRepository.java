package hr.algebra.mangaapp.repository.sql;

import hr.algebra.mangaapp.model.Genre;
import hr.algebra.mangaapp.repository.GenreRepository;
import hr.algebra.mangaapp.util.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlGenreRepository extends SqlAbstractRepository<Genre> implements GenreRepository {

    @Override
    protected String getFindAllSql() {
        return "SELECT * FROM fn_find_all_genres()";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT * FROM fn_find_genre_by_id(?)";
    }

    @Override
    protected String getCreateSql() {
        return "SELECT fn_create_genre(?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "CALL sp_update_genre(?, ?, ?)";
    }

    @Override
    protected String getDeleteSql() {
        return "CALL sp_delete_genre(?)";
    }

    @Override
    protected Genre map(ResultSet resultSet) throws SQLException {
        return new Genre(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("description")
        );
    }

    @Override
    protected void setCreateParameters(PreparedStatement statement, Genre genre) throws SQLException {
        statement.setString(1, genre.getName());
        statement.setString(2, genre.getDescription());
    }

    @Override
    protected void setUpdateParameters(CallableStatement statement, Genre genre) throws SQLException {
        statement.setLong(1, genre.getId());
        statement.setString(2, genre.getName());
        statement.setString(3, genre.getDescription());
    }

    @Override
    public List<Genre> search(String query) {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT * FROM fn_search_genres(?)";

        try (
                Connection connection = hr.algebra.mangaapp.util.DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, query);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    genres.add(map(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while searching genres", e);
        }

        return genres;
    }

    @Override
    public boolean existsByName(String name) {
        String sql = "SELECT fn_genre_exists_by_name(?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, name);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while checking if genre exists", e);
        }

        return false;
    }

}