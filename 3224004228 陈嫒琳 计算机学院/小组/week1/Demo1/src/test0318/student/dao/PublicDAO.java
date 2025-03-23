package test0318.student.dao;

import java.sql.*;

public class PublicDAO {
    //1.检查数据库是否存在账户
    public boolean hasAccounts(Connection conn) throws SQLException {
        String sql = "SELECT (SELECT COUNT(*) FROM administrators) + (SELECT COUNT(*) FROM students) AS total";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() && rs.getInt("total") > 0;         //全部账号是否大于零
        }
    }

    //2.检查指定ID在表中是否已存在
    public boolean isIdExists(Connection conn, String table, int id) throws SQLException {
        String sql = "SELECT id FROM " + table + " WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // 存在结果则返回 true
            }
        }
    }
}
