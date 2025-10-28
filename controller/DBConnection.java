package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central DB connection for the app.
 * Update URL, USER, PASS as required for your environment.
 */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/restaurant_db";
    private static final String USER = "javadb"; // update if needed
    private static final String PASS = "1234";             // update if needed

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Connector/J
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
