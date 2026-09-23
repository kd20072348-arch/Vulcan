package main.java.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class databaseManager {

    private static final String URL =
            "jdbc:mysql://localhost:3306/csv_analyser";

    private static final String USER = "root";

    private static final String PASSWORD = "Sashabindh@123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}