package g_server.g_server.application.query.response;

import java.util.List;

// Представление проекта
public class ProjectView {
    private int systemProjectID;
    private int systemProjectAreaID;
    private int systemProjectAdvisorID;
    private String projectName;
    private String projectArea;
    private String projectAdvisor;
    private List<AssociatedStudentViewWithoutProject> occupiedStudents;

    public ProjectView(int systemID, int areaID, int systemProjectAdvisorID, String projectName, String projectArea, String projectAdvisor,
                       List<AssociatedStudentViewWithoutProject> occupiedStudents) {
        this.systemProjectID = systemID;
        this.systemProjectAreaID = areaID;
        this.systemProjectAdvisorID = systemProjectAdvisorID;
        this.projectName = projectName;
        this.projectArea = projectArea;
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

    public String getProjectArea() {
        return projectArea;
    }

    public void setProjectArea(String projectArea) {
        this.projectArea = projectArea;
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

    public int getSystemProjectAreaID() {
        return systemProjectAreaID;
    }

    public void setSystemProjectAreaID(int systemProjectAreaID) {
        this.systemProjectAreaID = systemProjectAreaID;
    }
}