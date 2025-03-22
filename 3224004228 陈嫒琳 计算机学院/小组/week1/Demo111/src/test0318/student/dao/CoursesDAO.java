package test0318.student.dao;

import test0318.student.entity.Course;

import java.sql.*;

public class CoursesDAO {
    //更新课程学分到数据库
    public boolean updateCourseScore(Connection conn, int courseId, int newScore) {
        String sql = "UPDATE courses SET score = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newScore);
            pstmt.setInt(2, courseId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("数据库更新失败: " + e.getMessage());
            return false;
        }
    }

    //根据ID查询课程
    public Course getCourseById(Connection conn, int courseId) throws SQLException {
        String sql = "SELECT id, name, score FROM courses WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Course(rs.getString("name"),rs.getInt("id"), rs.getInt("score"));
                }
                return null;
            }
        }
    }
}
