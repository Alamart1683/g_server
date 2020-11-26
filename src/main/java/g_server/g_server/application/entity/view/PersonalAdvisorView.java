package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.users.Users;

public class PersonalAdvisorView {
    private int systemID;
    private String advisorName;
    private String advisorSurname;
    private String advisorSecondName;
    private String advisorCathedra;
    private String advisorPhone;
    private String advisorEmail;
    private String advisorRole;

    public PersonalAdvisorView(Users advisor, String advisorRole) {
        this.systemID = advisor.getId();
        this.advisorName = advisor.getName();
        this.advisorSurname = advisor.getSurname();
        this.advisorSecondName = advisor.getSecond_name();
        this.advisorCathedra = advisor.getStudentData().getCathedras().getCathedraName();
        this.advisorPhone = advisor.getPhone();
        this.advisorEmail = advisor.getEmail();
        this.advisorRole = advisorRole;
    }

    public int getSystemID() {
        return systemID;
    }

    public void setSystemID(int systemID) {
        this.systemID = systemID;
    }

    public String getAdvisorName() {
        return advisorName;
    }

    public void setAdvisorName(String advisorName) {
        this.advisorName = advisorName;
    }

    public String getAdvisorSurname() {
        return advisorSurname;
    }

    public void setAdvisorSurname(String advisorSurname) {
        this.advisorSurname = advisorSurname;
    }

    public String getAdvisorSecondName() {
        return advisorSecondName;
    }

    public void setAdvisorSecondName(String advisorSecondName) {
        this.advisorSecondName = advisorSecondName;
    }

    public String getAdvisorCathedra() {
        return advisorCathedra;
    }

    public void setAdvisorCathedra(String advisorCathedra) {
        this.advisorCathedra = advisorCathedra;
    }

    public String getAdvisorPhone() {
        return advisorPhone;
    }

    public void setAdvisorPhone(String advisorPhone) {
        this.advisorPhone = advisorPhone;
    }

    public String getAdvisorEmail() {
        return advisorEmail;
    }

    public void setAdvisorEmail(String advisorEmail) {
        this.advisorEmail = advisorEmail;
    }

    public String getAdvisorRole() {
        return advisorRole;
    }

    public void setAdvisorRole(String advisorRole) {
        this.advisorRole = advisorRole;
    }
}
