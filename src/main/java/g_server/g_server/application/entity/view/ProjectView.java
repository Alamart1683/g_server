package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.project.Project;
import g_server.g_server.application.entity.users.Users;
import java.util.List;

// Представление проекта для научного руководителя
public class ProjectView {
    private int systemID;
    private int systemProjectID;
    private String projectName;
    private String creatorFIO;
    private String projectType;
    private String projectDescription;
    private List<OccupiedStudentsView> involvedStudents;

    public ProjectView(Project project, Users advisor, List<OccupiedStudentsView> involvedStudents) {
        this.systemProjectID = project.getId();
        this.projectName = project.getName();
        this.creatorFIO = advisor.getSecond_name() + " " + advisor.getName() + " " + advisor.getSecond_name();
        this.projectType = project.getProjectTheme().getTheme();
        this.projectDescription = project.getDescription();
        this.involvedStudents = involvedStudents;
    }

    public int getSystemProjectID() {
        return systemProjectID;
    }

    public void setSystemProjectID(int systemProjectID) {
        this.systemProjectID = systemProjectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCreatorFIO() {
        return creatorFIO;
    }

    public void setCreatorFIO(String creatorFIO) {
        this.creatorFIO = creatorFIO;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public List<OccupiedStudentsView> getInvolvedStudents() {
        return involvedStudents;
    }

    public void setInvolvedStudents(List<OccupiedStudentsView> involvedStudents) {
        this.involvedStudents = involvedStudents;
    }
}