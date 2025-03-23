package test0318.student.dao;

import java.sql.*;

public class AdministratorsDAO {
    //2.更新数据库中的电话号码
    public boolean updateStudentPhone(Connection conn, int stuId, String newTel) {
        String sql = "UPDATE students SET tel = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newTel);
            pstmt.setInt(2, stuId);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("更新电话号码失败: " + e.getMessage());
            return false;
        }
    }
}
