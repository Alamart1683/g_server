package g_server.g_server.application.query.request;

// Форма проекта для его создания
public class ProjectForm {
    private String projectName;
    private String projectTheme;
    private String projectDescription;

    public ProjectForm(String projectName, String projectTheme, String projectDescription) {
        this.projectName = projectName;
        this.projectTheme = projectTheme;
        this.projectDescription = projectDescription;
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

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }
}
