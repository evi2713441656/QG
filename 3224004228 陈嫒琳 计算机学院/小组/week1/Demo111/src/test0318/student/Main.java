package test0318.student;

import test0318.student.dao.StudentsDAO;
import test0318.student.service.Manage;

public class Main {
    public static void main(String[] args) {
        StudentsDAO dao = new StudentsDAO();
        Manage manage = new Manage(dao);
        dao.setManage(manage); // 完成双向依赖
        manage.beginMenu();
    }
}