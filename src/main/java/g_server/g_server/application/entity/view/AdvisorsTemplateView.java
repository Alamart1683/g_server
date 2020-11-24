package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.documents.Document;
import java.util.List;

public class AdvisorsTemplateView extends DocumentView {
    private Integer systemAreaID;
    private String area;
    private String project;
    private Integer projectID;

    public AdvisorsTemplateView(Document document, List<DocumentVersionView>
            documentVersions, int projectID, String project, boolean flag) {
        super(document, documentVersions);
        this.projectID = projectID;
        this.project = project;
    }

    public AdvisorsTemplateView(Document document, List<DocumentVersionView>
            documentVersions, int systemAreaID, String area) {
        super(document, documentVersions);
        this.systemAreaID = systemAreaID;
        this.area = area;
    }

    public Integer getSystemAreaID() {
        return systemAreaID;
    }

    public void setSystemAreaID(int systemAreaID) {
        this.systemAreaID = systemAreaID;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }

}
