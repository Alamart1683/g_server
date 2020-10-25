package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.entity.documents.NirReport;

public class ReportVersionDocumentView extends DocumentVersionView {
    private int systemVersionID;
    private String status;

    public ReportVersionDocumentView(DocumentVersion documentVersion, NirReport nirReport) {
        super(documentVersion);
        this.systemVersionID = nirReport.getVersionID();
        this.status = nirReport.getDocumentStatus().getStatus();
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
}
