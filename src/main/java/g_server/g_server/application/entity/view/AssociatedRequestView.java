package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.users.Users;

// Форма формирующая пользовательское представление студенческих заявок
public class AssociatedRequestView {
    private int systemRequestID;
    private String FIO;
    private String group;
    private String type;
    private String cathedra;
    private String theme;

    public AssociatedRequestView() { }

    public AssociatedRequestView(Users users, String theme, int requestID) {
        this.systemRequestID = requestID;
        this.FIO = users.getSurname() + ' ' + users.getName() + ' ' + users.getSecond_name();
        this.group = users.getStudentData().getStudentGroup().getStudentGroup();
        this.type = users.getStudentData().getStudentType().getStudentType();
        this.cathedra = users.getStudentData().getCathedras().getCathedraName();
        this.theme = theme;
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

    public int getSystemRequestID() {
        return systemRequestID;
    }

    public void setSystemRequestID(int systemRequestID) {
        this.systemRequestID = systemRequestID;
    }
}
