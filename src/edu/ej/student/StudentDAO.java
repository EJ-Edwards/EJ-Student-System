package edu.ej.student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    public static List<Student> getAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT id,name,grade FROM students";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getString("name"),
                        rs.getInt("id"),
                        rs.getInt("grade")
                ));
            }
        }
        return list;
    }

    public static Student getById(int id) throws SQLException {
        String sql = "SELECT id,name,grade FROM students WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getString("name"),
                            rs.getInt("id"),
                            rs.getInt("grade")
                    );
                }
            }
        }
        return null;
    }

    public static boolean insert(Student s) throws SQLException {
        String sql = "INSERT INTO students(id,name,grade) VALUES (?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getId());
            ps.setString(2, s.getName());
            ps.setInt(3, s.getGrade());
            return ps.executeUpdate() == 1;
        }
    }

    public static boolean update(Student s) throws SQLException {
        String sql = "UPDATE students SET name = ?, grade = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setInt(2, s.getGrade());
            ps.setInt(3, s.getId());
            return ps.executeUpdate() == 1;
        }
    }

    public static boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Simple name search (case insensitive, prefix match).
     */
    public static List<Student> searchByName(String name) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT id,name,grade FROM students WHERE name LIKE ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Student(
                            rs.getString("name"),
                            rs.getInt("id"),
                            rs.getInt("grade")
                    ));
                }
            }
        }
        return list;
    }

    /**
     * Insert or update the student depending on existence.
     * Returns true if a row was modified/inserted.
     */
    public static boolean upsert(Student s) throws SQLException {
        // try update first
        if (update(s)) {
            return true;
        }
        // if update affected nothing, try insert
        return insert(s);
    }
}
