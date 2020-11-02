package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.users.Users;

// Представление списка студентов научного руководителя
public class AssociatedStudentView extends AssociatedRequestView {
    private int systemStudentID;
    private String projectName;
    private String projectArea;
    private String phone;
    private String email;
    private StudentDocumentsStatusView studentDocumentsStatusView;

    public AssociatedStudentView(Users user, int systemID, String projectName, String projectArea,
                                 String phone, String email, StudentDocumentsStatusView statusView) {
        this.systemStudentID = user.getId();
        this.setSystemID(systemID);
        this.setFIO(user.getSurname() + ' ' + user.getName() + ' ' + user.getSecond_name());
        this.setGroup(user.getStudentData().getStudentGroup().getStudentGroup());
        this.setType(user.getStudentData().getStudentType().getStudentType());
        this.setCathedra(user.getStudentData().getCathedras().getCathedraName());
        this.projectName = projectName;
        this.projectArea = projectArea;
        this.phone = phone;
        this.email = email;
        this.studentDocumentsStatusView = statusView;
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

    public StudentDocumentsStatusView getStudentDocumentsStatusView() {
        return studentDocumentsStatusView;
    }

    public void setStudentDocumentsStatusView(StudentDocumentsStatusView studentDocumentsStatusView) {
        this.studentDocumentsStatusView = studentDocumentsStatusView;
    }

    public String getProjectArea() {
        return projectArea;
    }

    public void setProjectArea(String projectArea) {
        this.projectArea = projectArea;
    }

    public int getSystemStudentID() {
        return systemStudentID;
    }

    public void setSystemStudentID(int systemStudentID) {
        this.systemStudentID = systemStudentID;
    }
}