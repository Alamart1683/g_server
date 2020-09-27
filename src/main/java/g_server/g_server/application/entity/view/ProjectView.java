package g_server.g_server.application.entity.view;

import java.util.List;

// Представление проекта
public class ProjectView {
    private int systemProjectID;
    private int systemProjectAdvisorID;
    private String projectName;
    private String projectTheme;
    private String projectAdvisor;
    private List<AssociatedStudentViewWithoutProject> occupiedStudents;

    public ProjectView(int systemID, int systemProjectAdvisorID, String projectName, String projectTheme, String projectAdvisor,
                       List<AssociatedStudentViewWithoutProject> occupiedStudents) {
        this.systemProjectID = systemID;
        this.systemProjectAdvisorID = systemProjectAdvisorID;
        this.projectName = projectName;
        this.projectTheme = projectTheme;
        this.projectAdvisor = projectAdvisor;
        this.occupiedStudents = occupiedStudents;
    }

    public int getSystemProjectID() {
        return systemProjectID;
    }

    public void setSystemProjectID(int systemProjectID) {
        this.systemProjectID = systemProjectID;
    }

    public int getSystemProjectAdvisorID() {
        return systemProjectAdvisorID;
    }

    public void setSystemProjectAdvisorID(int systemProjectAdvisorID) {
        this.systemProjectAdvisorID = systemProjectAdvisorID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectTheme() {
        return projectTheme;
    }

    public void setProjectTheme(String projectTheme) {
        this.projectTheme = projectTheme;
    }

    public String getProjectAdvisor() {
        return projectAdvisor;
    }

    public void setProjectAdvisor(String projectAdvisor) {
        this.projectAdvisor = projectAdvisor;
    }

    public List<AssociatedStudentViewWithoutProject> getOccupiedStudents() {
        return occupiedStudents;
    }

    public void setOccupiedStudents(List<AssociatedStudentViewWithoutProject> occupiedStudents) {
        this.occupiedStudents = occupiedStudents;
    }
}