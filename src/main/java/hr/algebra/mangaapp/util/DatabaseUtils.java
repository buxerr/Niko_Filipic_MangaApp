package hr.algebra.mangaapp.util;

import hr.algebra.mangaapp.model.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseUtils {

    private static final DatabaseConfig DATABASE_CONFIG =
            ConfigUtils.loadDatabaseConfig();

    private DatabaseUtils() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DATABASE_CONFIG.url(),
                DATABASE_CONFIG.username(),
                DATABASE_CONFIG.password()
        );
    }
}