package edu.ej.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class AddStudent {
    public static void execute() {
        main(new String[0]);
    }

    public static void main(String[] args) {
        Database.createTable();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("Enter student name: ");
                String name = scanner.nextLine();

                System.out.print("Enter student ID: ");
                int id = scanner.nextInt();

                System.out.print("Enter student grade: ");
                int grade = scanner.nextInt();
                scanner.nextLine();

                Student student = new Student(name, id, grade);

                try (Connection conn = Database.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO students(name,id,grade) VALUES (?,?,?)")) {
                    ps.setString(1, student.getName());
                    ps.setInt(2, student.getId());
                    ps.setInt(3, student.getGrade());
                    ps.executeUpdate();
                    System.out.println("Added: " + student);
                } catch (SQLException e) {
                    System.out.println("Failed to add student: " + e.getMessage());
                }

                System.out.print("Add another student? (yes/no): ");
                String again = scanner.nextLine().trim().toLowerCase();
                if (!again.equals("yes")) {
                    break;
                }
            }
        }
    }

}