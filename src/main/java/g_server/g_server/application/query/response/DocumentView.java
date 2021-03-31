package g_server.g_server.application.query.response;

import g_server.g_server.application.entity.documents.Document;

import java.util.List;

// Класс студенческого представления документа
public class DocumentView {
    private int systemCreatorID;
    private String documentName;
    private String documentDownloader;
    private String documentDownloadDate;
    private String documentType;
    private String documentKind;
    private String documentDescription;
    private List<DocumentVersionView> documentVersions;

    public DocumentView(Document document, List<DocumentVersionView> documentVersions) {
        this.systemCreatorID = document.getCreator();
        this.documentName = document.getName();
        this.documentDownloader = document.getUser().getSurname() + " " + document.getUser().getName() + " " +
                document.getUser().getSecond_name();
        this.documentDownloadDate = getRussianDate(document.getCreationDate());
        this.documentType = document.getDocumentType().getType();
        this.documentKind = document.getDocumentKind().getKind();
        this.documentDescription = document.getDescription();
        this.documentVersions = documentVersions;
    }

    public String getRussianDate(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        return day + "." + month + "." + year;
    }

    public int getSystemCreatorID() {
        return systemCreatorID;
    }

    public void setSystemCreatorID(int systemCreatorID) {
        this.systemCreatorID = systemCreatorID;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentDownloader() {
        return documentDownloader;
    }

    public void setDocumentDownloader(String documentDownloader) {
        this.documentDownloader = documentDownloader;
    }

    public String getDocumentDownloadDate() {
        return documentDownloadDate;
    }

    public void setDocumentDownloadDate(String documentDownloadDate) {
        this.documentDownloadDate = documentDownloadDate;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentKind() {
        return documentKind;
    }

    public void setDocumentKind(String documentKind) {
        this.documentKind = documentKind;
    }

    public String getDocumentDescription() {
        return documentDescription;
    }

    public void setDocumentDescription(String documentDescription) {
        this.documentDescription = documentDescription;
    }

    public List<DocumentVersionView> getDocumentVersions() {
        return documentVersions;
    }

    public void setDocumentVersions(List<DocumentVersionView> documentVersions) {
        this.documentVersions = documentVersions;
    }
}
