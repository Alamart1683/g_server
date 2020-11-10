package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.documents.Document;
import java.util.List;

public class DocumentViewTemplate extends DocumentView {
    private boolean isApproved;

    public DocumentViewTemplate(Document document, List<DocumentVersionView> documentVersions, boolean isApproved) {
        super(document, documentVersions);
        this.isApproved = isApproved;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}
