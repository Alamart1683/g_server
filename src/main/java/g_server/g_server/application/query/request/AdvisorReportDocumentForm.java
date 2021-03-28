package g_server.g_server.application.query.request;

import org.springframework.web.multipart.MultipartFile;

public class AdvisorReportDocumentForm extends DocumentForm {
    private int studentID;
    private String detailedContent;
    private String advisorConclusion;
    private boolean nowMerge;

    public AdvisorReportDocumentForm(String type, String kind, String description, String viewRights,
                                     String projectArea, String projectName, String token, MultipartFile file,
                                     int studentID, String detailedContent, String advisorConclusion,
                                     boolean nowMerge) {
        super(type, kind, description, viewRights, projectArea, projectName, token, file);
        this.studentID = studentID;
        this.detailedContent = detailedContent;
        this.advisorConclusion = advisorConclusion;
        this.nowMerge = nowMerge;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
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
