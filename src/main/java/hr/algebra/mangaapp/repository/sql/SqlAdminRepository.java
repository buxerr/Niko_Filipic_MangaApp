package hr.algebra.mangaapp.repository.sql;

import hr.algebra.mangaapp.exception.RepositoryException;
import hr.algebra.mangaapp.repository.AdminRepository;
import hr.algebra.mangaapp.util.DatabaseUtils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class SqlAdminRepository implements AdminRepository {

    @Override
    public void clearAllData() {
        String sql = "CALL sp_clear_all_data()";

        try (
                Connection connection = DatabaseUtils.getConnection();
                CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.execute();

        } catch (SQLException e) {
            throw new RepositoryException("Error while clearing all data", e);
        }
    }
}