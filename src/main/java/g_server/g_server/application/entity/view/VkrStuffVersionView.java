package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.documents.DocumentVersion;

public class VkrStuffVersionView extends DocumentVersionView {
    private String documentStatus;
    private Integer systemVersionID;

    public VkrStuffVersionView(DocumentVersion documentVersion, String documentStatus) {
        super(documentVersion);
        this.documentStatus = documentStatus;
        this.systemVersionID = documentVersion.getId();
    }

    public String getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(String documentStatus) {
        this.documentStatus = documentStatus;
    }

    public Integer getSystemVersionID() {
        return systemVersionID;
    }

    public void setSystemVersionID(Integer systemVersionID) {
        this.systemVersionID = systemVersionID;
    }
}