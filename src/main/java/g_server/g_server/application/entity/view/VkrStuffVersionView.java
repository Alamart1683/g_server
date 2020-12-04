package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.documents.DocumentVersion;

public class VkrStuffVersionView extends DocumentVersionView {
    private String documentStatus;

    public VkrStuffVersionView(DocumentVersion documentVersion, String documentStatus) {
        super(documentVersion);
        this.documentStatus = documentStatus;
    }

    public String getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(String documentStatus) {
        this.documentStatus = documentStatus;
    }
}