package g_server.g_server.application.query.request;

import org.springframework.web.multipart.MultipartFile;

public class DocumentFormReport extends DocumentForm {
    private boolean nowMerge;
    private String detailedContent;
    private String advisorConclusion;

    public DocumentFormReport(String type, String kind, String description, String viewRights, String projectArea,
            String projectName, String token, MultipartFile file, String detailedContent, String advisorConclusion, boolean nowMerge) {
        super(type, kind, description, viewRights, projectArea, projectName, token, file);
        this.detailedContent = detailedContent;
        this.advisorConclusion = advisorConclusion;
        this.nowMerge = nowMerge;
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

    public boolean isNowMerge() {
        return nowMerge;
    }

    public void setNowMerge(boolean nowMerge) {
        this.nowMerge = nowMerge;
    }
}