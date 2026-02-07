package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConfig {
    private static final String URL = "jdbc:postgresql://localhost:5432/event_scheduling_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "YOUR_PASSWORD";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
