package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.users.Users;

// Представление студентов занятых в проекте
public class OccupiedStudentsView {
    private int systemStudentID;
    private String FIO;
    private String group;
    private String type;
    private String cathedra;
    private String theme;

    public OccupiedStudentsView(Users student, String theme) {
        this.systemStudentID = student.getId();
        this.FIO = student.getSurname() + " " + student.getName() + " " + student.getSecond_name();
        this.group = student.getStudentData().getStudentGroup().getStudentGroup();
        this.type = student.getStudentData().getStudentType().getStudentType();
        this.cathedra = student.getStudentData().getCathedras().getCathedraName();
        this.theme = theme;
    }

    public int getSystemStudentID() {
        return systemStudentID;
    }

    public void setSystemStudentID(int systemStudentID) {
        this.systemStudentID = systemStudentID;
    }

    public String getFIO() {
        return FIO;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCathedra() {
        return cathedra;
    }

    public void setCathedra(String cathedra) {
        this.cathedra = cathedra;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}