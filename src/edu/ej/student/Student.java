package edu.ej.student;

public class Student {
    private String name;
    private int id;
    private int grade;

    public Student() {
    }

    public Student(String name, int id, int grade) {
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

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Student{name='" + name + "', id=" + id + ", grade=" + grade + "}";
    }

    /**
     * Convert this student to a JSON object string.
     * Example: {"name":"Alice","id":1,"grade":90}
     */
    public String toJson() {
        return "{\"name\":\"" + escape(name) + "\",\"id\":" + id + ",\"grade\":" + grade + "}";
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * Parse a student from a simple JSON object.  Returns null on failure.
     */
    public static Student fromJson(String json) {
        if (json == null) return null;
        try {
            String n = json.replaceAll(".*\"name\"\s*:\s*\"([^\"]+)\".*", "$1");
            String idStr = json.replaceAll(".*\"id\"\s*:\s*(\\d+).*", "$1");
            String gStr = json.replaceAll(".*\"grade\"\s*:\s*(\\d+).*", "$1");
            return new Student(n, Integer.parseInt(idStr), Integer.parseInt(gStr));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Render a list of students as JSON array.
     */
    public static String listToJson(java.util.List<Student> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).toJson());
            if (i < list.size() - 1) sb.append(',');
        }
        sb.append("]");
        return sb.toString();
    }
}
