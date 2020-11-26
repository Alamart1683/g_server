package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.project.Project;
import g_server.g_server.application.entity.users.Users;

// Представление личного кабинета студента
public class PersonalStudentView {
    private int systemID;
    private String studentName;
    private String studentSurname;
    private String studentSecondName;
    private String studentFIO;
    private String studentGroup;
    private String studentType;
    private String studentCathedra;
    private String studentAdvisor;
    private String studentProject;
    private String studentPhone;
    private String studentEmail;
    private String studentRole;
    private String studentSpeciality;

    public PersonalStudentView(Users student, String advisorName, String projectName) {
        this.systemID = student.getId();
        this.studentName = student.getName();
        this.studentSurname = student.getSurname();
        this.studentSecondName = student.getSecond_name();
        this.studentFIO = student.getSurname() + " " + student.getName() + " " + student.getSecond_name();
        this.studentGroup = student.getStudentData().getStudentGroup().getStudentGroup();
        this.studentType = student.getStudentData().getStudentType().getStudentType();
        this.studentCathedra = student.getStudentData().getCathedras().getCathedraName();
        this.studentAdvisor = advisorName;
        this.studentProject = projectName;
        this.studentPhone = student.getPhone();
        this.studentEmail = student.getEmail();
        this.studentRole = "Студент";
        this.studentSpeciality = getSpecialityByGroup();
    }

    public int getSystemID() {
        return systemID;
    }

    public void setSystemID(int systemID) {
        this.systemID = systemID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentSurname() {
        return studentSurname;
    }

    public void setStudentSurname(String studentSurname) {
        this.studentSurname = studentSurname;
    }

    public String getStudentSecondName() {
        return studentSecondName;
    }

    public void setStudentSecondName(String studentSecondName) {
        this.studentSecondName = studentSecondName;
    }

    public String getStudentFIO() {
        return studentFIO;
    }

    public void setStudentFIO(String studentFIO) {
        this.studentFIO = studentFIO;
    }

    public String getStudentGroup() {
        return studentGroup;
    }

    public void setStudentGroup(String studentGroup) {
        this.studentGroup = studentGroup;
    }

    public String getStudentType() {
        return studentType;
    }

    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }

    public String getStudentCathedra() {
        return studentCathedra;
    }

    public void setStudentCathedra(String studentCathedra) {
        this.studentCathedra = studentCathedra;
    }

    public String getStudentAdvisor() {
        return studentAdvisor;
    }

    public void setStudentAdvisor(String studentAdvisor) {
        this.studentAdvisor = studentAdvisor;
    }

    public String getStudentProject() {
        return studentProject;
    }

    public void setStudentProject(String studentProject) {
        this.studentProject = studentProject;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentRole() {
        return studentRole;
    }

    public void setStudentRole(String studentRole) {
        this.studentRole = studentRole;
    }

    public String getStudentSpeciality() {
        return studentSpeciality;
    }

    public void setStudentSpeciality(String studentSpeciality) {
        this.studentSpeciality = studentSpeciality;
    }

    public String getSpecialityByGroup() {
        if (this.studentGroup.contains("ИКБО")) {
            return "09.03.04";
        } else if (this.studentGroup.contains("ИВБО")) {
            return "09.03.01";
        }
        return "Не определена";
    }
}