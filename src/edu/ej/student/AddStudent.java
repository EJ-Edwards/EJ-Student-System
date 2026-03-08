package edu.ej.student;

import java.util.Scanner;


public class AddStudent {
    public static void execute() {
        main(new String[0]);
    }

    static class Student {
        String name;
        int id;
        int grade;

        Student(String name, int id, int grade) {
            this.name = name;
            this.id = id;
            this.grade = grade;
        }
        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public int getGrade() {
            return grade;
        }
    }

    public static void main(String[] args) {
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
                System.out.println(student.getName());
            }
        }
    }

}