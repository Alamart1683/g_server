package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.entity.documents.NirReport;

public class ReportVersionDocumentView extends DocumentVersionView {
    private int systemVersionID;
    private String status;
    private String detailedContent;
    private String advisorConclusion;

    public ReportVersionDocumentView(DocumentVersion documentVersion, NirReport nirReport) {
        super(documentVersion);
        this.systemVersionID = nirReport.getVersionID();
        this.status = nirReport.getDocumentStatus().getStatus();
        this.detailedContent = nirReport.getDetailedContent();
        this.advisorConclusion = nirReport.getAdvisorConclusion();
    }

    public int getSystemVersionID() {
        return systemVersionID;
    }

    public void setSystemVersionID(int systemVersionID) {
        this.systemVersionID = systemVersionID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
