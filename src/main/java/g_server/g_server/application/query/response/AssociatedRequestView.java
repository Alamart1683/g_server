package g_server.g_server.application.query.response;

import g_server.g_server.application.entity.users.Users;

// Форма формирующая пользовательское представление студенческих заявок
public class AssociatedRequestView {
    private int systemID;
    private String FIO;
    private String group;
    private String type;
    private String cathedra;

    public AssociatedRequestView() { }

    public AssociatedRequestView(Users users, int systemID) {
        this.systemID = systemID;
        this.FIO = users.getSurname() + ' ' + users.getName() + ' ' + users.getSecond_name();
        this.group = users.getStudentData().getStudentGroup().getStudentGroup();
        this.type = users.getStudentData().getStudentType().getStudentType();
        this.cathedra = users.getStudentData().getCathedras().getCathedraName();
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

    public int getSystemID() {
        return systemID;
    }

    public void setSystemID(int systemID) {
        this.systemID = systemID;
    }
}
