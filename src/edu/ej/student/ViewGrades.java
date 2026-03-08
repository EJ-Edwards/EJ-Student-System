package edu.ej.student;

import java.util.Scanner;
import java.sql.SQLException;


public class ViewGrades {
    public static void execute() {
        main(new String[0]);
    }

    public static void main(String[] args) {
        Database.createTable();
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter student ID to view grade (or leave blank): ");
            String idInput = scanner.nextLine();
            if (!idInput.isBlank()) {
                int id = Integer.parseInt(idInput);
                try (var conn = Database.getConnection();
                     var ps = conn.prepareStatement("SELECT name,grade FROM students WHERE id = ?")) {
                    ps.setInt(1, id);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("Grade for " + rs.getString("name") + ": " + rs.getInt("grade"));
                        } else {
                            System.out.println("No student with ID " + id);
                        }
                    }
                }
            } else {
                System.out.print("Enter student name to view grade: ");
                String name = scanner.nextLine();
                try (var conn = Database.getConnection();
                     var ps = conn.prepareStatement("SELECT grade FROM students WHERE name = ?")) {
                    ps.setString(1, name);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("Grade for " + name + ": " + rs.getInt("grade"));
                        } else {
                            System.out.println("No student named " + name);
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
