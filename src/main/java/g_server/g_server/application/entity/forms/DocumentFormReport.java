package g_server.g_server.application.entity.forms;

import org.springframework.web.multipart.MultipartFile;

public class DocumentFormReport extends DocumentForm {
    private String detailedContent;
    private String advisorConclusion;

    public DocumentFormReport(String type, String kind, String description, String viewRights, String projectArea,
            String projectName, String token, MultipartFile file, String detailedContent, String advisorConclusion) {
        super(type, kind, description, viewRights, projectArea, projectName, token, file);
        this.detailedContent = detailedContent;
        this.advisorConclusion = advisorConclusion;
    }

    public String getDetailedContent() {
        return detailedContent;
    }

    public void setDetailedContent(String detailedContent) {
        this.detailedContent = detailedContent;
    }

    public String getAdvisorConclusion() {
        return advisorConclusion;
    }

    public void setAdvisorConclusion(String advisorConclusion) {
        this.advisorConclusion = advisorConclusion;
    }
}