package g_server.g_server.application.entity.forms;

import org.springframework.web.multipart.MultipartFile;

public class DocumentVersionForm {
    Integer documentID;
    String editionDescription;
    String token;
    MultipartFile versionFile;

    public DocumentVersionForm() { }

    public DocumentVersionForm(Integer documentID, String editionDescription, String token, MultipartFile versionFile) {
        this.documentID = documentID;
        this.editionDescription = editionDescription;
        this.token = token;
        this.versionFile = versionFile;
    }

    public Integer getDocumentID() {
        return documentID;
    }

    public void setDocumentID(Integer documentID) {
        this.documentID = documentID;
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