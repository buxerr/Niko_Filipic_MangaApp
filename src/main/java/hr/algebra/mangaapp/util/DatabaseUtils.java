package hr.algebra.mangaapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseUtils {

    // TODO: Load database configuration from config.xml

    private static final String URL = "jdbc:postgresql://localhost:5433/mangaapp";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";

    private DatabaseUtils() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}