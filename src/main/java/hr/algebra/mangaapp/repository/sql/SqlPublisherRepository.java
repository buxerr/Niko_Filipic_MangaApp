package hr.algebra.mangaapp.repository.sql;

import hr.algebra.mangaapp.exception.RepositoryException;
import hr.algebra.mangaapp.model.Publisher;
import hr.algebra.mangaapp.repository.PublisherRepository;
import hr.algebra.mangaapp.util.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlPublisherRepository extends SqlAbstractRepository<Publisher> implements PublisherRepository {

    @Override
    protected String getFindAllSql() {
        return "SELECT * FROM fn_find_all_publishers()";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT * FROM fn_find_publisher_by_id(?)";
    }

    @Override
    protected String getCreateSql() {
        return "SELECT fn_create_publisher(?)";
    }

    @Override
    protected String getUpdateSql() {
        return "CALL sp_update_publisher(?, ?)";
    }

    @Override
    protected String getDeleteSql() {
        return "CALL sp_delete_publisher(?)";
    }

    @Override
    protected Publisher map(ResultSet resultSet) throws SQLException {
        return new Publisher(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
    }

    @Override
    protected void setCreateParameters(PreparedStatement statement, Publisher publisher) throws SQLException {
        statement.setString(1, publisher.getName());
    }

    @Override
    protected void setUpdateParameters(CallableStatement statement, Publisher publisher) throws SQLException {
        statement.setLong(1, publisher.getId());
        statement.setString(2, publisher.getName());
    }

    @Override
    public List<Publisher> search(String query) {
        List<Publisher> publishers = new ArrayList<>();
        String sql = "SELECT * FROM fn_search_publishers(?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, query);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    publishers.add(map(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error while searching publishers", e);
        }

        return publishers;
    }
}