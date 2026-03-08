package edu.ej.student;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Export {
    private static final String EXPORT_FILE = "students_export.csv";
    private static final String EXPORT_JSON = "students_export.json";
    private static final String EXPORT_SQLITE = "students_export.sqlite";


    public static void execute() {
        String choice = "csv";
        if (System.console() != null) {
            System.out.print("Export format (csv/json/sqlite): ");
            choice = System.console().readLine().trim().toLowerCase();
        }

        switch (choice) {
            case "json":
                exportJson();
                break;
            case "sqlite":
                exportSqlite();
                break;
            default:
                exportCsv();
                break;
        }
    }

    private static void exportCsv() {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             PrintWriter out = new PrintWriter(new FileWriter(EXPORT_FILE))) {

            out.println("id,name,grade");

            ResultSet rs = stmt.executeQuery("SELECT id,name,grade FROM students");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int grade = rs.getInt("grade");
                out.printf("%d,%s,%d%n", id, name, grade);
            }

            System.out.println("Exported students to " + EXPORT_FILE);

        } catch (SQLException e) {
            System.out.println("Database error during export: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error writing export file: " + e.getMessage());
        }
    }

    private static void exportJson() {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             PrintWriter out = new PrintWriter(new FileWriter(EXPORT_JSON))) {

            ResultSet rs = stmt.executeQuery("SELECT id,name,grade FROM students");
            out.println("[");
            boolean first = true;
            while (rs.next()) {
                if (!first) out.println(",");
                first = false;
                int id = rs.getInt("id");
                String name = rs.getString("name").replace("\"", "\\\"");
                int grade = rs.getInt("grade");
                out.printf("  {\"id\":%d,\"name\":\"%s\",\"grade\":%d}",
                           id, name, grade);
            }
            out.println();
            out.println("]");
            System.out.println("Exported students to " + EXPORT_JSON);

        } catch (SQLException e) {
            System.out.println("Database error during export: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error writing export file: " + e.getMessage());
        }
    }

    private static void exportSqlite() {
        java.nio.file.Path src = java.nio.file.Paths.get("students.db");
        java.nio.file.Path dst = java.nio.file.Paths.get(EXPORT_SQLITE);
        try {
            java.nio.file.Files.copy(src, dst, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copied database to " + EXPORT_SQLITE);
        } catch (IOException e) {
            System.out.println("I/O error copying database: " + e.getMessage());
        }
    }
}

