package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.documents.DocumentVersion;

// Представление списка версий документов
public class DocumentVersionView {
    private int systemEditorID;
    private int systemDocumentID;
    private String editorName;
    private String versionEditionDate;
    private String versionDescription;

    public DocumentVersionView(DocumentVersion documentVersion) {
        this.systemEditorID = documentVersion.getEditor();
        this.systemDocumentID = documentVersion.getDocument();
        this.editorName = documentVersion.getUser().getSurname() + " " + documentVersion.getUser().getName() + " " +
                documentVersion.getUser().getSecond_name();
        this.versionEditionDate = documentVersion.getEditionDate();
        this.versionDescription = documentVersion.getEdition_description();
    }

    public int getSystemEditorID() {
        return systemEditorID;
    }

    public void setSystemEditorID(int systemEditorID) {
        this.systemEditorID = systemEditorID;
    }

    public int getSystemDocumentID() {
        return systemDocumentID;
    }

    public void setSystemDocumentID(int systemDocumentID) {
        this.systemDocumentID = systemDocumentID;
    }

    public String getEditorName() {
        return editorName;
    }

    public void setEditorName(String editorName) {
        this.editorName = editorName;
    }

    public String getVersionEditionDate() {
        return versionEditionDate;
    }

    public void setVersionEditionDate(String versionEditionDate) {
        this.versionEditionDate = versionEditionDate;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }
}
