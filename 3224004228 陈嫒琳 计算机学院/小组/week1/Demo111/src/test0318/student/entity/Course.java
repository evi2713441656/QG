package test0318.student.entity;

import java.util.List;

public class Course {
    private String CourseName;
    private int CourseId;
    private String Teacher;
    private boolean Selected;
    private int Score;
    private List<Student> Students;

    public List<Student> getStudents() {
        return Students;
    }

    public void setStudents(List<Student> students) {
        Students = students;
    }

    public String getCourseName() {
        return CourseName;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
    }

    public int getCourseId() {
        return CourseId;
    }

    public void setCourseId(int courseId) {
        CourseId = courseId;
    }

    public String getTeacher() {
        return Teacher;
    }

    public void setTeacher(String teacher) {
        Teacher = teacher;
    }

    public boolean isSelected() {
        return Selected;
    }

    public void setSelected(boolean selected) {
        Selected = selected;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }

    public Course() {
    }

    public Course(String courseName, int courseId, String teacher, boolean selected, int score, List<Student> students) {
        CourseName = courseName;
        CourseId = courseId;
        Teacher = teacher;
        Selected = selected;
        Score = score;
        Students = students;
    }

    public Course(String courseName, int courseId, int score) {
        CourseName = courseName;
        CourseId = courseId;
        Score = score;
    }

    public void addStudent(Student student) {
        Students.add(student);
    }

    public void removeStudent(Student student) {
        Students.remove(student);
    }

}
