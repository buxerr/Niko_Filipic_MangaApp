package hr.algebra.mangaapp.repository.sql;

import hr.algebra.mangaapp.exception.RepositoryException;
import hr.algebra.mangaapp.model.User;
import hr.algebra.mangaapp.model.enums.UserRole;
import hr.algebra.mangaapp.repository.UserRepository;
import hr.algebra.mangaapp.util.DatabaseUtils;

import java.sql.*;
import java.util.Optional;

public class SqlUserRepository extends SqlAbstractRepository<User> implements UserRepository {

    @Override
    protected String getFindAllSql() {
        return "SELECT * FROM fn_find_all_users()";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT * FROM fn_find_user_by_id(?)";
    }

    @Override
    protected String getCreateSql() {
        return "SELECT fn_create_user(?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "CALL sp_update_user(?, ?, ?, ?)";
    }

    @Override
    protected String getDeleteSql() {
        return "CALL sp_delete_user(?)";
    }

    @Override
    protected User map(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("username"),
                resultSet.getString("password_hash"),
                UserRole.valueOf(resultSet.getString("role"))
        );
    }

    @Override
    protected void setCreateParameters(PreparedStatement statement, User user) throws SQLException {
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPasswordHash());
        statement.setString(3, user.getRole().name());
    }

    @Override
    protected void setUpdateParameters(CallableStatement statement, User user) throws SQLException {
        statement.setLong(1, user.getId());
        statement.setString(2, user.getUsername());
        statement.setString(3, user.getPasswordHash());
        statement.setString(4, user.getRole().name());
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM fn_find_user_by_username(?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error while finding user by username", e);
        }

        return Optional.empty();
    }

    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT fn_username_exists(?)";

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error while checking username existence", e);
        }

        return false;
    }
}