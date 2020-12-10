package g_server.g_server.application.entity.documents.reports;

import g_server.g_server.application.entity.documents.DocumentStatus;

import javax.persistence.*;

@Entity
@Table(name = "pd_report")
public class PdReport {
    @Id
    @Column(name = "versionID")
    private int versionID;

    @Column(name = "detailed_content")
    private String detailedContent;

    @Column(name = "advisor_conclusion")
    private String advisorConclusion;

    @Column(name = "pd_report_status")
    private int pdReportStatus;

    @Column(name = "is_hoc_rate")
    private boolean isHocRate;

    @ManyToOne
    @JoinColumn(name = "pd_report_status", referencedColumnName = "id", insertable = false, updatable = false)
    private DocumentStatus documentStatus;

    public PdReport() { }

    public PdReport(int versionID, int ppppuiopdReportStatus) {
        this.versionID = versionID;
        this.detailedContent = "Подробное содержание проделанной работы";
        this.advisorConclusion = "Заключение научного руководителя о выполненной работе";
        this.pdReportStatus = ppppuiopdReportStatus;
    }

    public PdReport(int versionID, String detailedContent, String advisorConclusion,
                           int nirReportStatus) {
        this.versionID = versionID;
        this.detailedContent = detailedContent;
        this.advisorConclusion = advisorConclusion;
        this.pdReportStatus = nirReportStatus;
    }

    public int getVersionID() {
        return versionID;
    }

    public void setVersionID(int versionID) {
        this.versionID = versionID;
    }

    public DocumentStatus getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(DocumentStatus documentStatus) {
        this.documentStatus = documentStatus;
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

    public int getPdReportStatus() {
        return pdReportStatus;
    }

    public void setPdReportStatus(int pdReportStatus) {
        this.pdReportStatus = pdReportStatus;
    }

    public boolean isHocRate() {
        return isHocRate;
    }

    public void setHocRate(boolean hocRate) {
        isHocRate = hocRate;
    }
}
