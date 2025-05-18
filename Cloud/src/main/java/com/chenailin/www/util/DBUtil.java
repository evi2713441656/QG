package com.chenailin.www.util;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @author evi
 */
public class DBUtil {

    // 用户名、密码、URL、驱动类定义为常量
    private static String USER = null;
    private static String PWD = null;
    private static String URL = null;
    private static String DRIVER = null;
    private static Connection conn;
    // 该类不能被实例化
    private DBUtil() {
    }

    // 在程序运行过程中,只需要做一次的注册驱动的代码放在静态代码块里面
    static {
        try {
            InputStream in=DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            Properties properties=new Properties();
            properties.load(in);
            DRIVER=properties.getProperty("DRIVER");
            USER=properties.getProperty("USER");
            PWD=properties.getProperty("PWD");
            URL=properties.getProperty("URL");
            // 触发驱动类的静态初始化块，向DriverManager注册自己
            Class.forName(DRIVER);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("mysql驱动注册失败!");
        }
    }

    // 得到数据库的连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL,USER,PWD);
    }

    // 关闭所有打开的资源（但这个基本用不上，因为前面的方法都使用了try-with-resources自动管理PreparedStatement，无需手动关闭）
    public static void release(Connection conn, Statement st, ResultSet rs) {
        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}