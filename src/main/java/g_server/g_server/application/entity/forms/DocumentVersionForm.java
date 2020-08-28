package g_server.g_server.application.entity.forms;

import org.springframework.web.multipart.MultipartFile;

public class DocumentVersionForm {
    String documentName;
    String editionDescription;
    String token;
    MultipartFile versionFile;

    public DocumentVersionForm() { }

    public DocumentVersionForm(String documentName, String editionDescription, String token, MultipartFile versionFile) {
        this.documentName = documentName;
        this.editionDescription = editionDescription;
        this.token = token;
        this.versionFile = versionFile;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getEditionDescription() {
        return editionDescription;
    }

    public void setEditionDescription(String editionDescription) {
        this.editionDescription = editionDescription;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public MultipartFile getVersionFile() {
        return versionFile;
    }

    public void setVersionFile(MultipartFile versionFile) {
        this.versionFile = versionFile;
    }
}