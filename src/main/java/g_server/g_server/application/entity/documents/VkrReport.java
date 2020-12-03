package g_server.g_server.application.entity.documents;

import javax.persistence.*;

@Entity
@Table(name = "vkr_report")
public class VkrReport {
    @Id
    @Column(name = "versionID")
    private Integer versionID;

    @Column(name = "vkr_report_status")
    private Integer vkrReportStatus;

    @ManyToOne
    @JoinColumn(name = "vkr_report_status", referencedColumnName = "id", insertable = false, updatable = false)
    private DocumentStatus documentStatus;

    public VkrReport() { }

    public VkrReport(Integer versionID, Integer vkrReportStatus, DocumentStatus documentStatus) {
        this.versionID = versionID;
        this.vkrReportStatus = vkrReportStatus;
        this.documentStatus = documentStatus;
    }

    public Integer getVersionID() {
        return versionID;
    }

    public void setVersionID(Integer versionID) {
        this.versionID = versionID;
    }

    public Integer getVkrReportStatus() {
        return vkrReportStatus;
    }

    public void setVkrReportStatus(Integer vkrReportStatus) {
        this.vkrReportStatus = vkrReportStatus;
    }

    public DocumentStatus getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(DocumentStatus documentStatus) {
        this.documentStatus = documentStatus;
    }
}