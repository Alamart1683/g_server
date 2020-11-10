package g_server.g_server.application.entity.documents;

import javax.persistence.*;

@Entity
@Table(name = "nir_report")
public class NirReport {
    @Id
    @Column(name = "versionID")
    private int versionID;

    @Column(name = "detailed_content")
    private String detailedContent;

    @Column(name = "advisor_conclusion")
    private String advisorConclusion;

    @Column(name = "nir_report_status")
    private int nirReportStatus;

    @ManyToOne
    @JoinColumn(name = "nir_report_status", referencedColumnName = "id", insertable = false, updatable = false)
    private DocumentStatus documentStatus;

    public NirReport() { }

    public NirReport(int versionID, int nirReportStatus) {
        this.versionID = versionID;
        this.detailedContent = "Подробное содержание проделанной работы";
        this.advisorConclusion = "Заключение научного руководителя о выполненной работе";
        this.nirReportStatus = nirReportStatus;
    }

    public NirReport(int versionID, String detailedContent, String advisorConclusion,
                     int nirReportStatus) {
        this.versionID = versionID;
        this.detailedContent = detailedContent;
        this.advisorConclusion = advisorConclusion;
        this.nirReportStatus = nirReportStatus;
    }

    public int getNirReportStatus() {
        return nirReportStatus;
    }

    public void setNirReportStatus(int nirReportStatus) {
        this.nirReportStatus = nirReportStatus;
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
}