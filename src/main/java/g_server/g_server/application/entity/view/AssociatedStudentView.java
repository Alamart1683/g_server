package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.users.Users;

// Представление списка студентов научного руководителя
public class AssociatedStudentView extends AssociatedRequestView {
    private String projectName;
    private String phone;
    private String email;

    public AssociatedStudentView(Users user, String theme, int systemID, String projectName,
                                 String phone, String email) {
        this.setSystemID(systemID);
        this.setFIO(user.getSurname() + ' ' + user.getName() + ' ' + user.getSecond_name());
        this.setGroup(user.getStudentData().getStudentGroup().getStudentGroup());
        this.setType(user.getStudentData().getStudentType().getStudentType());
        this.setCathedra(user.getStudentData().getCathedras().getCathedraName());
        this.setTheme(theme);
        this.projectName = projectName;
        this.phone = phone;
        this.email = email;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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
}