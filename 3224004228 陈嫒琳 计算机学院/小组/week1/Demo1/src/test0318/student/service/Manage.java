package test0318.student.service;

import test0318.student.dao.AdministratorsDAO;
import test0318.student.dao.CoursesDAO;
import test0318.student.util.DBUtil;
import test0318.student.entity.Administrator;
import test0318.student.entity.Course;
import test0318.student.entity.Student;
import test0318.student.dao.StudentsDAO;
import test0318.student.dao.PublicDAO;

import java.sql.*;
import java.util.Scanner;

public class Manage
{
    private final Scanner sc = new Scanner(System.in);
    public Administrator loginAdministrator;
    public Student loginStudent;
    private final AdministratorsDAO administratorsDAO=new AdministratorsDAO();
    private final CoursesDAO coursesDAO=new CoursesDAO();
    private final PublicDAO publicDAO=new PublicDAO();
    private final StudentsDAO studentsDAO;
    //通过构造函数注入 DAO
    public Manage(StudentsDAO studentsDAO) {
        this.studentsDAO = studentsDAO;
    }
    //开始界面
    public void beginMenu() {
        while (true) {
            System.out.println("===========================");
            System.out.println("学生选课管理系统");
            System.out.println("===========================");
            System.out.println("1. 登录");
            System.out.println("2. 注册");
            System.out.println("3. 退出");
            System.out.println("请选择操作（输入 1-3）：");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    createAccount();
                    break;
                case 3:
                    System.out.println("您已退出系统");
                    return;
                default:
                    System.out.println("没有该操作，请重新输入");
            }
        }
    }

//———————————————————————————公共类功能—————————————————————————————————
    //1.用户登录功能
    private void login() {
        System.out.println("===== 用户登录 =====");
        try (Connection conn = DBUtil.getConn()) {
            //检查是否存在账户
            if (!publicDAO.hasAccounts(conn)) {
                System.out.println("系统中无账户，请先注册");
                return;
            }
            while (true) {
                System.out.println("请您选择你的身份：\n1.管理员\n2.学生");
                int userChoice = sc.nextInt();
                if (userChoice == 1 || userChoice == 2) {
                    if (selectUser(conn, userChoice)) {
                        break; //登录成功退出循环
                    }
                } else {
                    System.out.println("无效选项，请重新输入");
                }
            }
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
        }
    }
    //用户认证逻辑
    private boolean selectUser(Connection conn, int userType) {
        String table = (userType == 1) ? "administrators" : "students";     //先判断身份
        System.out.println("请输入账号:");
        int id = sc.nextInt();
        sc.nextLine();
        // 从数据库查询用户
        String sql = "SELECT * FROM " + table + " WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("账号不存在");
                    return handleRetry();
                }
                // 验证密码
                String dbPassword = rs.getString("password");
                boolean isValid = validatePassword(dbPassword, 3);
                if (!isValid) {
                    System.exit(0); // 验证失败退出程序
                }
                // 初始化登录用户对象
                if (userType == 1) {
                    loginAdministrator = new Administrator(rs.getString("name"), rs.getInt("id"), dbPassword);
                    adminManage();
                } else {
                    loginStudent = new Student(rs.getInt("id"), rs.getString("name"), rs.getString("tel"), dbPassword);
                    stuManage();
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("数据库查询失败: " + e.getMessage());
            return false;
        }
    }
    //处理重试逻辑
    private boolean handleRetry() {
        System.out.println("1.重新输入\n" +
                "0.退出");
        int retry = sc.nextInt();
        sc.nextLine(); // 清除换行符
        return retry != 1;
    }
    //密码错误三次退出系统
    public static boolean validatePassword(String correctPassword, int maxAttempts) {
        Scanner scanner = new Scanner(System.in);
        int attempts = 0;
        System.out.println("=== 密码验证 ===");
        while (attempts < maxAttempts) {
            System.out.print("请输入密码（剩余次数：" + (maxAttempts - attempts) + "）: ");
            String input = scanner.nextLine();
            if (input.equals(correctPassword)) {
                System.out.println("密码正确！");
                return true;
            } else {
                System.out.println("密码错误！");
                attempts++;
            }
        }
        System.out.println("错误次数过多！");
        return false;
    }


    //2.注册功能
    private void createAccount() {
        try (Connection conn = DBUtil.getConn()) {
            while (true) {
                System.out.println("请选择你的身份：\n1.管理员\n2.学生");
                int choice = sc.nextInt();
                sc.nextLine(); // 清除输入缓冲
                if (choice == 1) {
                    registerAdministrator(conn);
                    break;
                } else if (choice == 2) {
                    registerStudent(conn);
                    break;
                } else {
                    System.out.println("无效选项，请重新输入");
                }
            }
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
        }
    }
    //注册管理员
    private void registerAdministrator(Connection conn) {
        Administrator admin = new Administrator();
        System.out.println("请输入工号:");
        admin.setAdminId(readValidId(conn, "administrators"));
        System.out.println("请输入姓名:");
        admin.setAdminName(sc.nextLine());
        admin.setPassword(confirmPassword());
        // 插入数据库
        String sql = "INSERT INTO administrators (id, name, password) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, admin.getAdminId());
            pstmt.setString(2, admin.getAdminName());
            pstmt.setString(3, admin.getPassword());
            pstmt.executeUpdate();
            System.out.println("管理员注册成功！");
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }
    //注册学生
    private void registerStudent(Connection conn) {
        Student student = new Student();
        System.out.println("请输入学号:");
        student.setStuId(readValidId(conn, "students"));
        System.out.println("请输入姓名:");
        student.setStuName(sc.nextLine());
        System.out.println("请输入电话号码:");
        student.setStuTel(sc.nextLine());
        student.setPassword(confirmPassword());
        // 插入数据库
        String sql = "INSERT INTO students (id, name, tel, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, student.getStuId());
            pstmt.setString(2, student.getStuName());
            pstmt.setString(3, student.getStuTel());
            pstmt.setString(4, student.getPassword()); // 直接存储明文
            pstmt.executeUpdate();
            System.out.println("学生注册成功！");
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }
    //确认密码
    private String confirmPassword() {
        while (true) {
            System.out.println("请输入密码:");
            String password = sc.nextLine();
            System.out.println("请确认密码:");
            String confirm = sc.nextLine();

            if (password.equals(confirm)) {
                return password; // 直接返回明文
            }
            System.out.println("两次输入不一致，请重新输入");
        }
    }
    //读取有效ID（检查是否已存在）
    private int readValidId(Connection conn, String table) {
        while (true) {
            int id = sc.nextInt();
            sc.nextLine(); // 清除换行符
                try {
                    if (!publicDAO.isIdExists(conn, table, id)) {
                        return id; // ID可用
                    }
                    System.out.println("该ID已被占用，请重新输入:");
                } catch (SQLException e) {
                System.err.println("数据库查询失败: " + e.getMessage());
            }
        }
    }
    //统一处理SQL异常
    private void handleSQLException(SQLException e) {
        if (e.getErrorCode() == 1062) { // 主键冲突
            System.err.println("注册失败：该账号已存在");
        } else {
            System.err.println("数据库操作失败: " + e.getMessage());
        }
    }


    //管理员菜单
    private void adminManage() {
        while(true) {
            System.out.println("===== 管理员菜单 =====");
            System.out.println("1. 查询所有学生");
            System.out.println("2. 修改学生手机号");
            System.out.println("3. 查询所有课程");
            System.out.println("4. 修改课程学分");
            System.out.println("5. 查询某课程的学生名单");
            System.out.println("6. 查询某学生的选课情况");
            System.out.println("7. 退出");
            System.out.println("请选择操作（输入 1-7）：");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    showAllStu();
                    break;
                case 2:
                    updateStuTel();
                    break;
                case 3:
                    showAllCour();
                    break;
                case 4:
                    updateScore();
                    break;
                case 5:
                    findStuByCour();
                    break;
                case 6:
                    findCourByStu();
                    break;
                case 7:
                    System.out.println("您已退出系统");
                    return;
                default:
                    System.out.println("选择错误");
                    return;
            }
        }
    }


    //学生菜单
    private void stuManage() {
        while(true){
            System.out.println("===== 学生菜单 =====");
            System.out.println("1.查看可选课程");
            System.out.println("2.选择课程");
            System.out.println("3.退选课程");
            System.out.println("4.查看已选课程");
            System.out.println("5.修改手机号");
            System.out.println("6.退出");
            System.out.println("请选择操作（输入 1-6）：");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    showUnselectedCourse();
                    break;
                case 2:
                    selectCourse();
                    break;
                case 3:
                    deleteCourse();
                    break;
                case 4:
                    showSelectedCourse();
                    break;
                case 5:
                    updateTel();
                    break;
                case 6:
                    System.out.println("您已退出系统");
                    return;
                default:
                    System.out.println("选择错误");
            }
        }
    }

//———————————————————————————管理员类功能—————————————————————————————————
    //1.查询所有学生
    private void showAllStu() {
        try (Connection conn = DBUtil.getConn()) {
            //查询已选课程 SQL
            String sql = "SELECT id, name, tel " +
                    "FROM students " ;
            //使用预编译语句
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                //执行查询并处理结果
                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("===== 学生列表 =====");
                    //学生的数量
                    int index = 1;
                    while (rs.next()) {
                        System.out.println("序号：" + index++);
                        System.out.println("学生ID：" + rs.getInt("id"));
                        System.out.println("学生姓名：" + rs.getString("name"));
                        System.out.println("学生电话号码：" + rs.getString("tel"));
                        System.out.println("-----------------------");
                    }
                    if (index == 1) {
                        System.out.println("暂无学生信息");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("查询课程失败: " + e.getMessage());
        }
    }


    //2.修改学生手机号
    private void updateStuTel() {
        try (Connection conn = DBUtil.getConn()) {
            // 1. 获取旧电话号码输入
            System.out.println("请输入要修改的旧电话号码：");
            String oldTel =sc.nextLine();
            // 2. 查询学生是否存在
            Student student = studentsDAO.getStudentByPhone(conn, oldTel);
            if (student == null) {
                System.out.println("错误：未找到该电话号码对应的学生");
                return;
            }
            // 3. 获取新电话号码（带格式验证）
            System.out.println("请输入新电话号码：");
            String newTel1 =sc.nextLine();
            System.out.println("请再次确认新电话号码：");
            String newTel2 = sc.nextLine();
            if (!newTel1.equals(newTel2)) {
                System.out.println("两次输入的电话号码不一致，修改失败");
                return;
            }
            // 4. 执行数据库更新
            if (administratorsDAO.updateStudentPhone(conn, student.getStuId(), newTel1)) {
                System.out.println("电话号码修改成功");
                // 更新当前登录学生对象的缓存数据
                if (loginStudent != null && loginStudent.getStuId() == student.getStuId()) {
                    loginStudent.setStuTel(newTel1);
                }
            } else {
                System.out.println("电话号码修改失败");
            }
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
        }
    }


    //3.查询所有课程
    private void showAllCour() {
        try (Connection conn = DBUtil.getConn()) {
            //查询课程 SQL
            String sql = "SELECT id, name, teacher, score " +
                    "FROM courses " ;
            //使用预编译语句
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                //执行查询并处理结果
                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("===== 课程列表 =====");
                    //课程的数量
                    int index = 1;
                    while (rs.next()) {
                        System.out.println("序号：" + index++);
                        System.out.println("课程ID：" + rs.getInt("id"));
                        System.out.println("课程名称：" + rs.getString("name"));
                        System.out.println("课程任课老师：" + rs.getString("teacher"));
                        System.out.println("课程学分：" + rs.getInt("score"));
                        System.out.println("-----------------------");
                    }
                    if (index == 1) {
                        System.out.println("暂无课程信息");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("查询课程失败: " + e.getMessage());
        }
    }


    //4.修改课程学分
    private void updateScore() {
        try (Connection conn = DBUtil.getConn()) {
            //获取课程ID输入
            System.out.println("请输入要修改的课程ID：");
            //判断输入的是否为int类型
            while (!sc.hasNextInt()) {
                System.out.println("输入无效，请输入数字课程ID：");
                sc.nextLine(); // 清除无效输入
            }
            int courseId = sc.nextInt();
            sc.nextLine(); // 清除换行符
            //查询课程是否存在
            Course course = coursesDAO.getCourseById(conn, courseId);
            if (course == null) {
                System.out.println("错误：未找到ID为 " + courseId + " 的课程");
                return;
            }
            //显示当前信息
            System.out.println("当前课程信息：");
            System.out.println("课程名称：" + course.getCourseName());
            System.out.println("当前学分：" + course.getScore());
            //获取新学分输入
            int newScore=getValidScoreInput();
//            do {
//                System.out.println("请输入新的课程学分（1-10之间的整数）：");
//                if (sc.hasNextInt()) {
//                    newScore = sc.nextInt();
//                    sc.nextLine(); // 清除换行符
//                    if (newScore >= 1 && newScore <= 10) {
//                        break;
//                    }
//                    System.out.println("学分必须在1到10之间");
//                } else {
//                    System.out.println("请输入有效的整数");
//                    sc.nextLine(); // 清除无效输入
//                }
//            }
//            while (true);
            // 5. 执行数据库更新
            if (coursesDAO.updateCourseScore(conn, courseId, newScore)) {
                System.out.println("成功将课程 [" + course.getCourseName() + "] 的学分更新为：" + newScore);
            } else {
                System.out.println("学分更新失败");
            }
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
        }
    }
    //获取有效的学分输入
    private int getValidScoreInput() {
        while (true) {
            System.out.println("请输入新的课程学分（1-10之间的整数）：");
            if (sc.hasNextInt()) {
                int input = sc.nextInt();
                sc.nextLine(); // 清除换行符
                if (input >= 1 && input <= 10) {
                    return input;
                }
                System.out.println("学分必须在1到10之间");
            } else {
                System.out.println("请输入有效的整数");
                sc.nextLine(); // 清除无效输入
            }
        }
    }


    //5.查询某课程的学生名单
    public void findStuByCour() {
        try (Connection conn = DBUtil.getConn()) {
            System.out.println("请输入你想查找的课程id：");
            //判断输入的是否为int类型
            while (!sc.hasNextInt()) {
                System.out.println("输入无效，请输入数字课程ID：");
                sc.nextLine(); // 清除无效输入
            }
            int courseId=sc.nextInt();
            sc.nextLine(); // 清除换行符
            // 2. 查询课程是否存在
            Course course = coursesDAO.getCourseById(conn, courseId);
            if (course == null) {
                System.out.println("错误：未找到ID为 " + courseId + " 的课程");
                return;
            }
            //查询课程 SQL
            String sql = "SELECT id, name, tel FROM students WHERE id = ?" ;
            //使用预编译语句
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, courseId);
                //执行查询并处理结果
                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("===== 选择该课程的学生列表 =====");
                    //学生的数量
                    int index = 1;
                    while (rs.next()) {
                        System.out.println("序号：" + index++);
                        System.out.println("学生ID：" + rs.getInt("id"));
                        System.out.println("学生姓名：" + rs.getString("name"));
                        System.out.println("学生电话号码：" + rs.getString("tel"));
                        System.out.println("-----------------------");
                    }
                    if (index == 1) {
                        System.out.println("暂无学生选择");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("查询课程失败: " + e.getMessage());
        }
    }


    //6.查询某学生的选课情况
    public void findCourByStu() {
        try (Connection conn = DBUtil.getConn()) {
            System.out.println("请输入你想查找的学生ID：");
            //判断输入的是否为int类型
            while (!sc.hasNextInt()) {
                System.out.println("输入无效，请输入数字学生ID：");
                sc.nextLine(); // 清除无效输入
            }
            int stuId =sc.nextInt();
            sc.nextLine(); // 清除换行符
            // 2. 查询学生是否存在
            Student stu = studentsDAO.getStuById(conn, stuId);
            if (stu == null) {
                System.out.println("错误：未找到ID为 " + stuId + " 的学生");
                return;
            }
            //查询学生 SQL
            String sql = "SELECT id, name, teacher, score FROM courses WHERE id = ?" ;
            //使用预编译语句
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, stuId);
                //执行查询并处理结果
                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("===== 该学生的课程列表 =====");
                    //课程的数量
                    int index = 1;
                    while (rs.next()) {
                        System.out.println("序号：" + index++);
                        System.out.println("课程ID：" + rs.getInt("id"));
                        System.out.println("课程名称：" + rs.getString("name"));
                        System.out.println("课程任课老师：" + rs.getString("teacher"));
                        System.out.println("课程学分：" + rs.getInt("score"));
                        System.out.println("-----------------------");
                    }
                    if (index == 1) {
                        System.out.println("该学生暂无课程");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("查询课程失败: " + e.getMessage());
        }
    }

//———————————————————————————学生类功能—————————————————————————————————
    //1.查看可选课程
    private void showUnselectedCourse(){
        try (Connection conn = DBUtil.getConn()) {
            // 1. 查询已选课程 SQL
            String sql = "SELECT c.id, c.name, c.teacher, c.score " +
                    "FROM courses c " +
                    "WHERE c.selected = 0";
            // 2. 使用预编译语句
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // 3. 执行查询并处理结果
                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("===== 可选课程列表 =====");
                    //选课的数量
                    int index = 1;
                    while (rs.next()) {
                        System.out.println("序号：" + index++);
                        System.out.println("课程ID：" + rs.getInt("id"));
                        System.out.println("课程名称：" + rs.getString("name"));
                        System.out.println("任课老师：" + rs.getString("teacher"));
                        System.out.println("课程学分：" + rs.getInt("score"));
                        System.out.println("-----------------------");
                    }
                    if (index == 1) {
                        System.out.println("暂无可选课程");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("查询课程失败: " + e.getMessage());
        }
    }


    //2.选择课程
    private void selectCourse(){
        try (Connection conn = DBUtil.getConn()) {
            System.out.println("请输入要选择课程的id：");
            int courseId=sc.nextInt();
            sc.nextLine(); // 清除换行符
            //执行数据库更新
            if (studentsDAO.updateDBCourse(conn,courseId)) {
                System.out.println("选择课程成功");
            } else {
                System.out.println("选择课程失败");
            }
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
        }
    }


    //3.退选课程
    private void deleteCourse(){
        try (Connection conn = DBUtil.getConn()) {
            // 获取新电话号码
            System.out.println("请输入要退选课程的id：");
            int courseId=sc.nextInt();
            //执行数据库更新
            if (studentsDAO.deleteDBCourse(conn,courseId)) {
                System.out.println("退选课程成功");
            } else {
                System.out.println("退选课程失败");
            }
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
        }
    }


    //4.查看已选课程
    private void showSelectedCourse() {
        try (Connection conn = DBUtil.getConn()) {
            //查询已选课程 SQL
            String sql = "SELECT c.id, c.name, c.teacher, c.score " +
                    "FROM courses c " +
                    "JOIN student_courses sc ON c.id = sc.CourseId " +
                    "WHERE sc.StuId = ?";
            //使用预编译语句
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, loginStudent.getStuId());
                //执行查询并处理结果
                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("===== 已选课程列表 =====");
                    //选课的数量
                    int index = 1;
                    while (rs.next()) {
                        System.out.println("序号：" + index++);
                        System.out.println("课程ID：" + rs.getInt("id"));
                        System.out.println("课程名称：" + rs.getString("name"));
                        System.out.println("任课老师：" + rs.getString("teacher"));
                        System.out.println("课程学分：" + rs.getInt("score"));
                        System.out.println("-----------------------");
                    }
                    if (index == 1) {
                        System.out.println("暂无已选课程");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("查询课程失败: " + e.getMessage());
        }
    }


    //5.修改手机号
    private void updateTel() {
        try (Connection conn = DBUtil.getConn()) {
            // 获取新电话号码
            System.out.println("请输入新电话号码：");
            String newTel1 =sc.nextLine();
            System.out.println("请再次确认新电话号码：");
            String newTel2 = sc.nextLine();
            if (!newTel1.equals(newTel2)) {
                System.out.println("两次输入的电话号码不一致，修改失败");
                return;
            }
            //执行数据库更新
            if (studentsDAO.updateDBTel(conn,newTel1)) {
                System.out.println("电话号码修改成功");
                // 更新当前登录学生对象的缓存数据
                loginStudent.setStuTel(newTel1);
            } else {
                System.out.println("电话号码修改失败");
            }
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
        }
    }

}
