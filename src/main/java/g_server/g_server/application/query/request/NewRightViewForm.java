package g_server.g_server.application.query.request;

public class NewRightViewForm {
    private String documentName;
    private String newViewRights;
    private String projectName;
    private String projectArea;

    public NewRightViewForm() { }

    public NewRightViewForm(String documentName, String newViewRights, String projectName, String projectArea) {
        this.documentName = documentName;
        this.newViewRights = newViewRights;
        this.projectName = projectName;
        this.projectArea = projectArea;
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

    public String getProjectArea() {
        return projectArea;
    }

    public void setProjectArea(String projectArea) {
        this.projectArea = projectArea;
    }
}