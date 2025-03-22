package test0318.student.dao;

import java.sql.*;

import test0318.student.entity.Student;
import test0318.student.service.Manage;

public class StudentsDAO {
    private Manage manage;
    // 通过 setter 注入 Manage（非构造函数）
    public void setManage(Manage manage) {
        this.manage = manage;
    }

    //根据电话号码查询学生（返回完整对象）
    public Student getStudentByPhone(Connection conn, String phone) throws SQLException {
        String sql = "SELECT id, name, tel, password FROM students WHERE tel = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("tel"),  // 使用String类型存储电话号码
                            rs.getString("password")
                    );
                }
                return null;
            }
        }
    }

    //2.更新数据库中的选择课程
    public boolean updateDBCourse(Connection conn, int courseId){
        //同时执行需要在数据库连接URL中添加 allowMultiQueries=true
        String sql = "INSERT INTO courses (selected) VALUES (1)" +
                "WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("退选课程失败: " + e.getMessage());
            return false;
        }
    }

    //3.更新数据库中的退选课程
    public boolean deleteDBCourse(Connection conn, int courseId){
        //同时执行需要在数据库连接URL中添加 allowMultiQueries=true
        String sql ="DELETE FROM student_courses WHERE CourseId = ?;" +
                "UPDATE courses SET `selected`=0 WHERE id = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            //pstmt.setInt(2, 0);
            pstmt.setInt(2, courseId);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("退选课程失败: " + e.getMessage());
            return false;
        }
    }

    //5.更新数据库中的学生电话号码
    public boolean updateDBTel(Connection conn, String newTel) {
        String sql = "UPDATE students SET tel = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newTel);
            pstmt.setInt(2, manage.loginStudent.getStuId());
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("更新电话号码失败: " + e.getMessage());
            return false;
        }
    }

    //根据ID查询学生
    public Student getStuById(Connection conn, int stuId) throws SQLException {
        String sql = "SELECT id, name, tel FROM students WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, stuId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(rs.getInt("id"),rs.getString("name"), rs.getString("tel"));
                }
                return null;
            }
        }
    }

}
