package g_server.g_server.application.entity.forms;

public class NewRightViewForm {
    private String documentName;
    private String newViewRights;
    private String projectName;

    public NewRightViewForm() { }

    public NewRightViewForm(String documentName, String newViewRights, String projectName) {
        this.documentName = documentName;
        this.newViewRights = newViewRights;
        this.projectName = projectName;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getNewViewRights() {
        return newViewRights;
    }

    public void setNewViewRights(String newViewRights) {
        this.newViewRights = newViewRights;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}