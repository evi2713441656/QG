package test0318.student.entity;

public class Administrator {
    private int AdminId;
    private String Password;
    private String AdminName;

    public String getAdminName() {
        return AdminName;
    }

    public void setAdminName(String adminName) {
        AdminName = adminName;
    }

    public int getAdminId() {
        return AdminId;
    }

    public void setAdminId(int adminId) {
        AdminId = adminId;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public Administrator() {
    }

    public Administrator(int adminId, String password, String adminName) {
        AdminId = adminId;
        Password = password;
        AdminName= adminName;
    }

    public Administrator(int adminId, String password) {
        AdminId = adminId;
        Password = password;
    }
}
