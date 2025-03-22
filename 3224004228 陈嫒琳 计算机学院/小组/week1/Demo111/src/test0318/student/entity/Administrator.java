package test0318.student.entity;

public class Administrator {
    private String AdminName;
    private int AdminId;
    private String Password;

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

    public Administrator(String adminName, int adminId, String password) {
        AdminName = adminName;
        AdminId = adminId;
        Password = password;
    }
}
