package test0318.student.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;


//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    //用户名、密码、URL、驱动类定义为常量
    private static String USER = null;
    private static String PWD = null;
    private static String URL = null;
    private static String DRIVER = null;
    private static Connection conn;
    //该类不能被实例化
    private DBUtil() {
    }

    //在程序运行过程中,只需要做一次的注册驱动的代码放在静态代码块里面
    static {
        try {
            InputStream in=DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            Properties properties=new Properties();
            properties.load(in);
            DRIVER=properties.getProperty("DRIVER");
            USER=properties.getProperty("USER");
            PWD=properties.getProperty("PWD");
            URL=properties.getProperty("URL");
            Class.forName(DRIVER);
            //System.out.println("mysql驱动注册成功!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("mysql驱动注册失败!");
        }
    }

    //得到数据库的连接
    public static Connection getConn() throws SQLException {
        return DriverManager.getConnection(URL,USER,PWD);
    }

    //关闭所有打开的资源
    public static void release(Connection conn, Statement st,ResultSet rs) {
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
