//package test0317.student.service;
//
//import test0318.student.entity.Administrator;
//import test0318.student.entity.Course;
//import test0318.student.entity.Student;
//
//import java.util.ArrayList;
//import java.util.Scanner;
//
//public class StudentManage
//{
//    private ArrayList<Student> students = new ArrayList<>();
//    private ArrayList<Administrator> administrators = new ArrayList<>();
//    private ArrayList<Course> courses=new ArrayList<>();
//    private Scanner sc = new Scanner(System.in);
//    public Administrator loginAdministrator;
//    public Student loginStudent;
//
//
//    //开始界面
//    public void beginMenu() {
//        while (true) {
//            System.out.println("===========================");
//            System.out.println("学生选课管理系统");
//            System.out.println("===========================");
//            System.out.println("1. 登录");
//            System.out.println("2. 注册");
//            System.out.println("3. 退出");
//            System.out.println("请选择操作（输入 1-3）：");
//            int choice = sc.nextInt();
//            switch (choice) {
//                case 1:
//                    login();
//                    break;
//                case 2:
//                    createAccount();
//                    break;
//                case 3:
//                    System.out.println("您已退出系统");
//                    return;
//                default:
//                    System.out.println("没有该操作，请重新输入");
//            }
//        }
//    }
//
//
//    // 用户登录功能
//    private void login() {
//        System.out.println("===== 用户登录 =====");
//        if (students.isEmpty() && administrators.isEmpty()){
//            System.out.println("系统中无账户，请先注册");
//            return;
//        }
//        while (true) {
//            System.out.println("请您选择要登录：1 还是退出：0");
//            int choice = sc.nextInt();
//            if (choice==1) {
//                while (true) {
//                    System.out.println("请您选择你的身份：");
//                    System.out.println("1，管理员");
//                    System.out.println("2.学生");
//                    int userChoice = sc.nextInt();
//                    if (userChoice==1){
//                        System.out.println("请输入你登录的账号:");
//                        int id = sc.nextInt();
//                        Administrator acc = getAdminId(id);
//                        if(acc == null){
//                            System.out.println("卡号不存在，请重新输入：1 或 选择退出：0");
//                            int exit = sc.nextInt();
//                            if (exit==1){
//                                System.out.println("重新输入");
//                            }else{
//                                System.out.println("退出系统");
//                                return;
//                            }
//                        }
//                        else{
//                            while (true) {
//                                System.out.println("请您输入密码");
//                                String password = sc.next();
//                                if (acc.getPassword().equals(password)){
//                                    loginAdministrator=acc;
//                                    System.out.println("恭喜您，登陆成功");
//                                    adminManage();
//                                    return;
//                                }
//                                else{
//                                    System.out.println("密码错误,请重新输入：1 或 选择退出：0");
//                                    int exit = sc.nextInt();
//                                    if (exit==1){
//                                        System.out.println("重新输入");
//                                    }else{
//                                        System.out.println("退出系统");
//                                        return;
//                                    }
//                                }
//                            }
//                        }
//                        break;
//                    }else if (userChoice==2) {
//                        System.out.println("请输入你登录的账号:");
//                        int id = sc.nextInt();
//                        Student acc = getStuId(id);
//                        if(acc == null){
//                            System.out.println("卡号不存在，请重新输入：1 或 选择退出：0");
//                            int exit = sc.nextInt();
//                            if (exit==1){
//                                System.out.println("重新输入");
//                            }else{
//                                System.out.println("退出系统");
//                                return;
//                            }
//                        }
//                        else{
//                            while (true) {
//                                System.out.println("请您输入密码");
//                                String password = sc.next();
//                                if (acc.getPassword().equals(password)){
//                                    loginStudent=acc;
//                                    System.out.println("恭喜您，登陆成功");
//                                    stuManage();
//                                    return;
//                                }
//                                else{
//                                    System.out.println("密码错误,请重新输入：1 或 选择退出：0");
//                                    int exit = sc.nextInt();
//                                    if (exit==1){
//                                        System.out.println("重新输入");
//                                    }else{
//                                        System.out.println("退出系统");
//                                        return;
//                                    }
//                                }
//                            }
//                        }
//                        break;
//                    }else {
//                        System.out.println("没有该操作，请重新输入");
//                    }
//                }
//            }
//            else{
//                System.out.println("您已退出");
//                //return;
//            }
//        }
//    }
//
//
//    //根据学号查找学生账户
//    private Student getStuId(int cardId){
//        for (int i = 0; i < students.size(); i++) {
//            Student acc = students.get(i);
//            if(acc.getStuId()==cardId){
//                return acc;
//            }
//        }
//        return null;
//    }
//
//
//    //根据工号查找管理员账户
//    private Administrator getAdminId(int cardId){
//        for (int i = 0; i < administrators.size(); i++) {
//            Administrator acc = administrators.get(i);
//            if(acc.getAdminId()==cardId){
//                return acc;
//            }
//        }
//        return null;
//    }
//
//
//    //用户注册操作
//    private void createAccount() {
//        while(true)
//        {
//            System.out.println("你的身份是：");
//            System.out.println("1.管理员");
//            System.out.println("2.学生");
//            int choice = sc.nextInt();
//            if(choice==1)
//            {
//                Administrator administrator = new Administrator();
//                System.out.println("请输入你的工号");
//                int id = sc.nextInt();
//                administrator.setAdminId(id);
//                System.out.println("请你输入您的姓名：");
//                String name = sc.next();
//                administrator.setAdminName(name);
//                while (true) {
//                    System.out.println("请您输入密码：");
//                    String password = sc.next();
//                    System.out.println("请您确认您的密码");
//                    String okPassword = sc.next();
//                    if (okPassword.equals(password)) {
//                        System.out.println("密码输入成功");
//                        administrator.setPassword(password);
//                        break;
//                    } else {
//                        System.out.println("密码不一致重新确认");
//                    }
//                }
//                System.out.println("注册成功！");
//                administrators.add(administrator);
//                break;
//            }
//            else if(choice==2){
//                Student student = new Student();
//                System.out.println("请输入你的学号");
//                int stuId = sc.nextInt();
//                student.setStuId(stuId);
//                System.out.println("请你输入您的姓名：");
//                String stuName = sc.next();
//                student.setStuName(stuName);
//                System.out.println("请输入你的电话号码");
//                long tel = sc.nextLong();
//                student.setStuTel(tel);
//                while (true) {
//                    System.out.println("请您输入密码：");
//                    String password = sc.next();
//                    System.out.println("请您确认您的密码");
//                    String okPassword = sc.next();
//                    if (okPassword.equals(password)) {
//                        System.out.println("密码输入成功");
//                        student.setPassword(password);
//                        break;
//                    } else {
//                        System.out.println("密码不一致重新确认");
//                    }
//                }
//
//                System.out.println("注册成功！");
//                students.add(student);
//                break;
//            }
//            else {
//                System.out.println("没有该操作，请重新输入");
//            }
//        }
//    }
//
//
//    //管理员菜单
//    private void adminManage() {
//        System.out.println("===== 管理员菜单 =====");
//        System.out.println("1. 查询所有学生");
//        System.out.println("2. 修改学生手机号");
//        System.out.println("3. 查询所有课程");
//        System.out.println("4. 修改课程学分");
//        System.out.println("5. 查询某课程的学生名单");
//        System.out.println("6. 查询某学生的选课情况");
//        System.out.println("7. 退出");
//        System.out.println("请选择操作（输入 1-7）：");
//        while(true) {
//            int choice = sc.nextInt();
//            switch (choice) {
//                case 1:
//                    showAllStu();
//                    break;
//                case 2:
//                    updateStuTel();
//                    break;
//                case 3:
//                    showAllCour();
//                    break;
//                case 4:
//                    updateScore();
//                    break;
//                case 5:
//                    findStu();
//                    break;
//                case 6:
//                    findCour();
//                    break;
//                case 7:
//                    System.out.println("您已退出系统");
//                    return;
//                default:
//                    System.out.println("选择错误");
//                    return;
//            }
//        }
//    }
//
//
//    //学生菜单
//    private void stuManage() {
//        System.out.println("===== 学生菜单 =====");
//        System.out.println("1.查看可选课程");
//        System.out.println("2.选择课程");
//        System.out.println("3.退选课程");
//        System.out.println("4.查看已选课程");
//        System.out.println("5.修改手机号");
//        System.out.println("6.退出");
//        System.out.println("请选择操作（输入 1-6）：");
//        int choice = sc.nextInt();
//        while(true) {
//            switch (choice) {
//                case 1:
//                    showUnselectedCourse();
//                    break;
//                case 2:
//                    selectCourse();
//                    break;
//                case 3:
//                    deleteCourse();
//                    break;
//                case 4:
//                    showSelectedCourse();
//                    break;
//                case 5:
//                    updateTel();
//                    break;
//                case 6:
//                    System.out.println("您已退出系统");
//                    return;
//                default:
//                    System.out.println("选择错误");
//            }
//        }
//    }
//
//
//    //查询所有学生
//    private void showAllStu() {
//        System.out.println("学生信息如下：");
//        for (int i = 0; i < students.size(); i++) {
//            Student stu = students.get(i);
//            System.out.println("学号："+stu.getStuId());
//            System.out.println("姓名："+stu.getStuName());
//            System.out.println("电话号码："+stu.getStuTel());
//        }
//
//    }
//
//
//    //修改学生手机号
//    private void updateStuTel(){
//        System.out.println("请输入你想修改的学生电话号码：");
//        long stuTel =sc.nextLong();
//        for (int i = 0; i < students.size(); i++) {
//            Student stu = students.get(i);
//            if (stu.getStuTel()==stuTel){
//                System.out.println("选择成功");
//                while (true) {
//                    System.out.println("请您输入新电话号码");
//                    long newTel1 = sc.nextLong();
//                    System.out.println("请您输入确认电话号码");
//                    long newTel2 = sc.nextLong();
//                    if (newTel1==newTel2){
//                        loginStudent.setStuTel(newTel1);
//                        System.out.println("修改成功");
//                        return;
//                    }else {
//                        System.out.println("两次电话号码输入不一致，请重新输入");
//                    }
//                }
//            }else{
//                System.out.println("两次输入电话号码不正确");
//            }
//        }
//        System.out.println("没有此电话号码");
//    }
//
//
//    //查询所有课程
//    private void showAllCour() {
//        System.out.println("课程信息如下：");
//        for (int i = 0; i < courses.size(); i++) {
//            Course cour = courses.get(i);
//            System.out.println("课程id："+cour.getCourseId());
//            System.out.println("名称："+cour.getCourseName());
//            System.out.println("任课老师："+cour.getTeacher());
//            System.out.println("学分："+cour.getScore());
//            System.out.println(cour.isSelect() ?"已选":"未选");
//        }
//
//    }
//
//
//    //修改课程学分
//    private void updateScore(){
//        System.out.println("请输入你想修改课程id：");
//        int courId =sc.nextInt();
//        for (int i = 0; i < courses.size(); i++) {
//            Course cour = courses.get(i);
//            if (cour.getCourseId()== courId){
//                System.out.println("课程名称："+cour.getCourseName());
//                System.out.println("课程学分：" + cour.getScore());
//                while (true) {
//                    System.out.println("请您输入新的学分：");
//                    int newScore = sc.nextInt();
//                    cour.setScore(newScore);
//                    System.out.println("学分修改成功");
//                }
//            }
//        }
//        System.out.println("没有此课程");
//    }
//
//
//    //查询某课程的学生名单
//    private void findStu(){
//        System.out.println("请输入你要查询的课程id：");
//        int courId =sc.nextInt();
//        for (int i = 0; i < courses.size(); i++) {
//            Course cour = courses.get(i);
//            if (cour.getCourseId()==courId) {
//                for (int j = 0; j < cour.getStudents().size(); j++) {
//                    Student stu = cour.getStudents().get(j);
//                    System.out.println("学生姓名：" + stu.getStuName());
//                }
//            }
//            break;
//        }
//    }
//
//
//    //查询某学生的选课情况
//    private void findCour(){
//        System.out.println("请输入你要查询的学生学号：");
//        int stuId =sc.nextInt();
//        for (int i = 0; i < students.size(); i++) {
//            Student stu = students.get(i);
//            if (stu.getStuId()==stuId){
//                for (int j = 0; j < stu.getCourses().size(); j++) {
//                    Course cour=stu.getCourses().get(j);
//                    System.out.println("课程名称："+cour.getCourseName());
//                }
//            }
//            break;
//        }
//    }
//
//
//
//
//
//    //查看可选课程
//    private void showUnselectedCourse(){
//        System.out.println("可选课程如下：");
//        for (int i = 0; i < courses.size(); i++) {
//            Course cour = courses.get(i);
//            if(!cour.isSelect()){
//                System.out.println(i + 1);
//                System.out.println("课程id："+cour.getCourseId());
//                System.out.println("课程名称" + cour.getCourseName());
//                System.out.println("课程任课老师：" + cour.getTeacher());
//                System.out.println("课程学分：" + cour.getScore());
//            }
//        }
//    }
//
//
//    //选择课程
//    private void selectCourse(){
//        System.out.println("请输入要选择课程的id：");
//        int courseId=sc.nextInt();
//        for (int i = 0; i < courses.size(); i++) {
//            Course cour = courses.get(i);
//            if (cour.getCourseId()==courseId){
//                System.out.println("选择成功");
//                loginStudent.addCourse(cour);
//                cour.setSelect(true);       //标记为已选择
//                cour.addStudent(loginStudent);  //将学生添加至课程
//                break;
//            }
//        }
//        System.out.println("没有此id");
//    }
//
//
//    //退选课程
//    private void deleteCourse(){
//        System.out.println("请输入要退选课程的id：");
//        int courseId=sc.nextInt();
//        for (int i = 0; i < courses.size(); i++) {
//            Course cour = courses.get(i);
//            if (cour.getCourseId()==courseId){
//                System.out.println("退选成功");
//                loginStudent.removeCourse(cour);
//                cour.setSelect(false);       //标记为未选择
//                break;
//            }
//        }
//        System.out.println("没有此id");
//    }
//
//
//    //查看已选课程
//    private void showSelectedCourse(){
//        System.out.println("可选课程如下：");
//        for (int i = 0; i < courses.size(); i++) {
//            Course cour = courses.get(i);
//            if(cour.isSelect()){
//                System.out.println(i + 1);
//                System.out.println("课程id："+cour.getCourseId());
//                System.out.println("课程名称" + cour.getCourseName());
//                System.out.println("课程任课老师：" + cour.getTeacher());
//                System.out.println("课程学分：" + cour.getScore());
//            }
//        }
//    }
//
//
//    //修改手机号
//    private void updateTel(){
//        System.out.println("请输入当前电话号码：");
//        long stuTel =sc.nextLong();
//        if (loginStudent.getStuTel()==stuTel){
//            while (true) {
//                System.out.println("请您输入新电话号码");
//                long newTel1 = sc.nextLong();
//                System.out.println("请您再次确认电话号码");
//                long newTel2 = sc.nextLong();
//                if (newTel1==newTel2){
//                    loginStudent.setStuTel(newTel1);
//                    System.out.println("修改成功");
//                    return;
//                }else {
//                    System.out.println("两次电话号码输入不一致，请重新输入");
//                }
//            }
//        }
//        System.out.println("电话号码错误");
//    }
//}
//
//
//
//
