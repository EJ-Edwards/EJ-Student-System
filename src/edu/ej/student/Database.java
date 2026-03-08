package edu.ej.student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Database {
    public static final String URL = "jdbc:sqlite:students.db";

   
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

 
    public static void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS students (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    grade INTEGER
                );
                """;
        try (Connection conn = getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Could not create students table: " + e.getMessage());
        }
    }
}
