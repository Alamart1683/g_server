package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.users.Users;

// Представление студента который не определился с проектом
public class AssociatedStudentViewWithoutProject extends AssociatedRequestView {
    private int systemStudentID;
    private String phone;
    private String email;

    public AssociatedStudentViewWithoutProject(Users user, String theme, int systemID, String phone, String email) {
        this.systemStudentID = user.getId();
        this.setSystemID(systemID);
        this.setFIO(user.getSurname() + ' ' + user.getName() + ' ' + user.getSecond_name());
        this.setGroup(user.getStudentData().getStudentGroup().getStudentGroup());
        this.setType(user.getStudentData().getStudentType().getStudentType());
        this.setCathedra(user.getStudentData().getCathedras().getCathedraName());
        this.setTheme(theme);
        this.phone = phone;
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSystemStudentID() {
        return systemStudentID;
    }

    public void setSystemStudentID(int systemStudentID) {
        this.systemStudentID = systemStudentID;
    }
}