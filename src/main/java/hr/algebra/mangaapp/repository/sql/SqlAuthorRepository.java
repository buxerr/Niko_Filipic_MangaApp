package hr.algebra.mangaapp.repository.sql;

import hr.algebra.mangaapp.exception.RepositoryException;
import hr.algebra.mangaapp.model.Author;
import hr.algebra.mangaapp.model.enums.AuthorType;
import hr.algebra.mangaapp.repository.AuthorRepository;
import hr.algebra.mangaapp.util.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlAuthorRepository extends SqlAbstractRepository<Author> implements AuthorRepository {

    @Override
    protected String getFindAllSql() {
        return "SELECT * FROM fn_find_all_authors()";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT * FROM fn_find_author_by_id(?)";
    }

    @Override
    protected String getCreateSql() {
        return "SELECT fn_create_author(?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "CALL sp_update_author(?, ?, ?, ?)";
    }

    @Override
    protected String getDeleteSql() {
        return "CALL sp_delete_author(?)";
    }

    @Override
    protected Author map(ResultSet resultSet) throws SQLException {
        return new Author(
                resultSet.getLong("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                AuthorType.valueOf(resultSet.getString("orientation"))
        );
    }

    @Override
    protected void setCreateParameters(PreparedStatement statement, Author author) throws SQLException {
        statement.setString(1, author.getFirstName());
        statement.setString(2, author.getLastName());
        statement.setString(3, author.getOrientation().name());
    }

    @Override
    protected void setUpdateParameters(CallableStatement statement, Author author) throws SQLException {
        statement.setLong(1, author.getId());
        statement.setString(2, author.getFirstName());
        statement.setString(3, author.getLastName());
        statement.setString(4, author.getOrientation().name());
    }

    @Override
    public List<Author> search(String query) {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM fn_search_authors(?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, query);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    authors.add(map(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error while searching authors", e);
        }

        return authors;
    }
}