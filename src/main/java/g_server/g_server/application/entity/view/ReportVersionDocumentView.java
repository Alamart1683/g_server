package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.documents.*;
import g_server.g_server.application.entity.documents.reports.NirReport;
import g_server.g_server.application.entity.documents.reports.PdReport;
import g_server.g_server.application.entity.documents.reports.PpppuiopdReport;
import g_server.g_server.application.entity.documents.reports.VkrReport;

public class ReportVersionDocumentView extends DocumentVersionView {
    private int systemVersionID;
    private String status;
    private String detailedContent;
    private String advisorConclusion;
    private boolean isHocRate;

    public ReportVersionDocumentView(DocumentVersion documentVersion, NirReport nirReport) {
        super(documentVersion);
        this.systemVersionID = nirReport.getVersionID();
        this.status = nirReport.getDocumentStatus().getStatus();
        this.detailedContent = nirReport.getDetailedContent();
        this.advisorConclusion = nirReport.getAdvisorConclusion();
        this.isHocRate = nirReport.isHocRate();
    }

    public ReportVersionDocumentView(DocumentVersion documentVersion, PpppuiopdReport ppppuiopdReport) {
        super(documentVersion);
        this.systemVersionID = ppppuiopdReport.getVersionID();
        this.status = ppppuiopdReport.getDocumentStatus().getStatus();
        this.detailedContent = ppppuiopdReport.getDetailedContent();
        this.advisorConclusion = ppppuiopdReport.getAdvisorConclusion();
        this.isHocRate = ppppuiopdReport.isHocRate();
    }

    public ReportVersionDocumentView(DocumentVersion documentVersion, PdReport pdReport) {
        super(documentVersion);
        this.systemVersionID = pdReport.getVersionID();
        this.status = pdReport.getDocumentStatus().getStatus();
        this.detailedContent = pdReport.getDetailedContent();
        this.advisorConclusion = pdReport.getAdvisorConclusion();
        this.isHocRate = pdReport.isHocRate();
    }

    public ReportVersionDocumentView(DocumentVersion documentVersion, VkrReport vkrReport) {
        super(documentVersion);
        this.systemVersionID = vkrReport.getVersionID();
        this.status = vkrReport.getDocumentStatus().getStatus();
        this.isHocRate = vkrReport.isHocRate();
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

    public boolean isHocRate() {
        return isHocRate;
    }

    public void setHocRate(boolean hocRate) {
        isHocRate = hocRate;
    }
}