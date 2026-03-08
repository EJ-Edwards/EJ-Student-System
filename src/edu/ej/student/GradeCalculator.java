package edu.ej.student;

import java.util.Scanner;

public class GradeCalculator {
    public static void execute() {
        main(new String[0]);
    }



    private double percentage;

    private double assignments = 0.4;
    private double exams = 0.5;
    private double quizzes = 0.1;
    private double participation = 0.7;

    public void setAssignments(double assignments) { this.assignments = assignments; }
    public void setExams(double exams) { this.exams = exams; }
    public void setQuizzes(double quizzes) { this.quizzes = quizzes; }
    public void setParticipation(double participation) { this.participation = participation; }

    public void calculateGrade(double totalMarks, double marksObtained) {
        this.percentage = (marksObtained / totalMarks) * 100;

        if (percentage >= 90) {
            System.out.println("Grade: A");
        } else if (percentage >= 80) {
            System.out.println("Grade: B");
        } else if (percentage >= 70) {
            System.out.println("Grade: C");
        } else if (percentage >= 65) {
            System.out.println("Grade: D");
        } else {
            System.out.println("Grade: F");
        }
    }

    public double calculateWeightedGrade() {
        return (assignments * 0.4) + (exams * 0.5) + (quizzes * 0.1) + (participation * 0.7);
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            GradeCalculator gc = new GradeCalculator();

        System.out.print("Enter total marks: ");
        double total = scanner.nextDouble();

        System.out.print("Enter marks obtained: ");
        double obtained = scanner.nextDouble();

        gc.calculateGrade(total, obtained);

        System.out.println("Do you want to calculate GPA based on weighted grades? (yes/no)");
        String response = scanner.next();
        if (response.equalsIgnoreCase("yes")) {
            System.out.print("Please Enter Assignments 0-100: ");
            System.out.print("Please Enter Exams 0-100: ");
            System.out.print("Please Enter Quizzes 0-100: ");
            System.out.print("Please Enter Participation 0-100: ");
            double assignments = scanner.nextDouble();
            double exams = scanner.nextDouble();
            double quizzes = scanner.nextDouble();
            double participation = scanner.nextDouble();

            gc.setAssignments(assignments);
            gc.setExams(exams);
            gc.setQuizzes(quizzes);
            gc.setParticipation(participation);

            double weightedGrade = gc.calculateWeightedGrade();
            System.out.println("Weighted Grade: " + weightedGrade);
            System.out.println("Your Student GPA is: " + (weightedGrade / 20)); 

            } else {
                System.out.println("GPA calculation skipped.");
                System.out.println("Thank you for using EJ Student System. Goodbye!");
            }
        }
    }
}