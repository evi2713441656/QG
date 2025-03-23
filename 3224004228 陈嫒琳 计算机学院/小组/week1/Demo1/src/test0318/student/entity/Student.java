package test0318.student.entity;

import java.util.List;

public class Student {
    private int StuId;              //学号
    private String StuName;         //姓名
    private String StuTel;            //电话号码
    private List<Course> Courses;   //选修课程列表
    private String Password;        //密码

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public int getStuId() {
        return StuId;
    }

    public void setStuId(int stuId) {
        this.StuId = stuId;
    }

    public String getStuName() {
        return StuName;
    }

    public void setStuName(String stuName) {
        this.StuName = stuName;
    }

    public String getStuTel() {
        return StuTel;
    }

    public void setStuTel(String stuTel) {
        this.StuTel = stuTel;
    }

    public List<Course> getCourses() {
        return Courses;
    }

    public void setCourses(List<Course> courses) {
        this.Courses = courses;
    }

    public Student(int stuId, String stuName, String stuTel, String password) {
        StuId = stuId;
        StuName = stuName;
        StuTel = stuTel;
        this.Password = password;
    }

    public Student(int stuId, String stuName, String stuTel) {
        StuId = stuId;
        StuName = stuName;
        StuTel = stuTel;
    }

    public Student() {
    }

    public void addCourse(Course course) {
        Courses.add(course);
    }

    public void removeCourse(Course course) {
        Courses.remove(course);
    }
}
