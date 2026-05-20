package hr.algebra.mangaapp.util;

import hr.algebra.mangaapp.model.config.DatabaseConfig;
import javafx.geometry.Dimension2D;

public final class ConfigUtils {

    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5433/mangaapp";
    private static final String DATABASE_USERNAME = "postgres";
    private static final String DATABASE_PASSWORD = "postgres";

    private static final double SMALL_SCREEN_WIDTH = 400;
    private static final double SMALL_SCREEN_HEIGHT = 300;

    private static final double BIG_SCREEN_WIDTH = 1100;
    private static final double BIG_SCREEN_HEIGHT = 1100;

    private ConfigUtils() {
    }

    public static DatabaseConfig loadDatabaseConfig() {
        return new DatabaseConfig(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
    }

    public static Dimension2D getSmallScreen() {
        return new Dimension2D(SMALL_SCREEN_WIDTH, SMALL_SCREEN_HEIGHT);
    }

    public static Dimension2D getBigScreen() {
        return new Dimension2D(BIG_SCREEN_WIDTH, BIG_SCREEN_HEIGHT);
    }
}
