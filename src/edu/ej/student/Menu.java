package edu.ej.student;

import java.util.Scanner;


public class Menu {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== EJ Student System ===");
            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. Grade Calculator");
            System.out.println("4. Assign Grades");
            System.out.println("5. View Grades");
            System.out.println("6. Export Grades");
            System.out.println("7. Exit");
            System.out.print("Please select an option: ");

            while (!scanner.hasNextInt()) {
                System.out.print("Invalid input. Enter a number between 1 and 7: ");
                scanner.next();
            }
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    AddStudent.execute();
                    break;
                case 2:
                    ViewStudents.execute();
                    break;
                case 3:
                    GradeCalculator.execute();
                    break;
                case 4:
                    AssignGrades.execute();
                    break;
                case 5:
                    ViewGrades.execute();
                    break;
                case 6:
                    Export.execute();
                    break;
                case 7:
                    System.out.println("Exiting. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 7);

        scanner.close();
    }
}
