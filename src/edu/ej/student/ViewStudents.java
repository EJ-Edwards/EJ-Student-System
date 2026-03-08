package edu.ej.student;

import java.util.Scanner;

public class ViewStudents {
    public static void execute() {
        main(new String[0]);
    }

    public static void main(String[] args) {
        Database.createTable(); 
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Viewing all students...");
            System.out.print("Do you want to look up a specific student? (yes/no): ");
            String response = scanner.nextLine();
            if (response.equalsIgnoreCase("yes")) {
                System.out.print("Lookup by ID or name? (id/name): ");
                String by = scanner.nextLine().trim().toLowerCase();
                if (by.equals("id")) {
                    System.out.print("Enter student ID: ");
                    int id = scanner.nextInt();
                    try (var conn = Database.getConnection();
                         var ps = conn.prepareStatement("SELECT id,name,grade FROM students WHERE id = ?")) {
                        ps.setInt(1, id);
                        try (var rs = ps.executeQuery()) {
                            if (rs.next()) {
                                System.out.println(rs.getInt("id") + " " + rs.getString("name") + " grade=" + rs.getInt("grade"));
                            } else {
                                System.out.println("No student with ID " + id);
                            }
                        }
                    }
                } else {
                    System.out.print("Enter student name: ");
                    String name = scanner.nextLine();
                    try (var conn = Database.getConnection();
                         var ps = conn.prepareStatement("SELECT id,name,grade FROM students WHERE name = ?")) {
                        ps.setString(1, name);
                        try (var rs = ps.executeQuery()) {
                            boolean found = false;
                            while (rs.next()) {
                                found = true;
                                System.out.println(rs.getInt("id") + " " + rs.getString("name") + " grade=" + rs.getInt("grade"));
                            }
                            if (!found) {
                                System.out.println("No student named " + name);
                            }
                        }
                    }
                }
            } else {
                try (var conn = Database.getConnection();
                     var stmt = conn.createStatement();
                     var rs = stmt.executeQuery("SELECT id,name,grade FROM students")) {
                    while (rs.next()) {
                        System.out.println(rs.getInt("id") + " " + rs.getString("name") + " grade=" + rs.getInt("grade"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Invalid input.");
        }
    }
}