package g_server.g_server.application.query.response;

import g_server.g_server.application.entity.users.Users;

// Представление списка студентов научного руководителя
public class AssociatedStudentView extends AssociatedRequestView {
    private int systemStudentID;
    private String projectName;
    private String projectArea;
    private String phone;
    private String email;
    private StudentDocumentsStatusView studentDocumentsStatusView;
    private String studentVkrTheme;
    private String advisorFIO;
    private String specialityCode;
    private boolean isStudentVkrThemeEditable;

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
        this.studentVkrTheme = user.getStudentData().getVkrTheme();
        this.isStudentVkrThemeEditable = user.getStudentData().isVkrThemeEditable();
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

    public String getStudentVkrTheme() {
        return studentVkrTheme;
    }

    public void setStudentVkrTheme(String studentVkrTheme) {
        this.studentVkrTheme = studentVkrTheme;
    }

    public boolean isStudentVkrThemeEditable() {
        return isStudentVkrThemeEditable;
    }

    public void setStudentVkrThemeEditable(boolean studentVkrThemeEditable) {
        isStudentVkrThemeEditable = studentVkrThemeEditable;
    }

    public String getAdvisorFIO() {
        return advisorFIO;
    }

    public void setAdvisorFIO(String advisorFIO) {
        this.advisorFIO = advisorFIO;
    }

    public String getSpecialityCode() {
        return specialityCode;
    }

    public void setSpecialityCode(String specialityCode) {
        this.specialityCode = specialityCode;
    }
}