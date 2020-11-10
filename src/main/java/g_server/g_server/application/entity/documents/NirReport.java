package g_server.g_server.application.entity.documents;

import javax.persistence.*;

@Entity
@Table(name = "nir_report")
public class NirReport {
    @Id
    @Column(name = "versionID")
    private int versionID;

    @Column(name = "nir_report_status")
    private int nirReportStatus;

    @ManyToOne
    @JoinColumn(name = "nir_report_status", referencedColumnName = "id", insertable = false, updatable = false)
    private DocumentStatus documentStatus;

    public NirReport() { }

    public NirReport(int versionID, int nirReportStatus) {
        this.versionID = versionID;
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
}