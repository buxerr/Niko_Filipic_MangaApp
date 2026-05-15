package hr.algebra.mangaapp.repository.sql;

import hr.algebra.mangaapp.exception.RepositoryException;
import hr.algebra.mangaapp.repository.Repository;
import hr.algebra.mangaapp.util.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class SqlAbstractRepository<T> implements Repository<T> {

    protected abstract String getFindAllSql();
    protected abstract String getFindByIdSql();
    protected abstract String getCreateSql();
    protected abstract String getUpdateSql();
    protected abstract String getDeleteSql();
    protected abstract T map(ResultSet resultSet) throws SQLException;
    protected abstract void setCreateParameters(PreparedStatement statement, T entity) throws SQLException;
    protected abstract void setUpdateParameters(CallableStatement statement, T entity) throws SQLException;

    @Override
    public List<T> findAll() {
        List<T> entities = new ArrayList<>();

        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(getFindAllSql());
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                entities.add(map(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error while loading entities", e);
        };

        return entities;
    }

    @Override
    public Optional<T> findById(Long id) {
        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(getFindByIdSql())
        ) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error while finding entity by id", e);
        }

        return Optional.empty();
    }

    @Override
    public Long create(T entity) {
        try (
                Connection connection = DatabaseUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(getCreateSql())
        ) {
            setCreateParameters(statement, entity);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error while creating entity", e);
        }

        throw new RepositoryException("Entity was not created");
    }

    @Override
    public void update(T entity) {
        try (
                Connection connection = DatabaseUtils.getConnection();
                CallableStatement statement = connection.prepareCall(getUpdateSql())
        ) {
            setUpdateParameters(statement, entity);
            statement.execute();
        } catch (SQLException e) {
            throw new RepositoryException("Error while updating entity", e);
        }
    }

    @Override
    public void delete(Long id) {
        try (
                Connection connection = DatabaseUtils.getConnection();
                CallableStatement statement = connection.prepareCall(getDeleteSql())
        ) {
            statement.setLong(1, id);
            statement.execute();
        } catch (SQLException e) {
            throw new RepositoryException("Error while deleting entity", e);
        }
    }
}
