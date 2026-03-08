package edu.ej.student;

import java.util.Scanner;
import java.sql.SQLException;

public class AssignGrades {
    public static void execute() {
        main(new String[0]);
    }
    public static void main(String[] args) {
        Database.createTable();
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter student ID to assign grade: ");
            int id = scanner.nextInt();
            System.out.print("Enter grade to assign: ");
            int grade = scanner.nextInt();
            try (var conn = Database.getConnection();
                 var ps = conn.prepareStatement("UPDATE students SET grade = ? WHERE id = ?")) {
                ps.setInt(1, grade);
                ps.setInt(2, id);
                int changed = ps.executeUpdate();
                if (changed > 0) {
                    System.out.println("Assigned grade " + grade + " to student with ID: " + id);
                } else {
                    System.out.println("No student found with ID " + id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter valid integers for ID and grade.");
        }
    }
}