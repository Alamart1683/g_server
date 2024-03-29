package g_server.g_server.application.query.request;

import g_server.g_server.application.entity.documents.Document;
import org.springframework.web.multipart.MultipartFile;

public class DocumentForm {
    private String documentFormType;
    private String documentFormKind;
    private String documentFormDescription;
    private String documentFormViewRights;
    private String projectArea;
    private String projectName;
    private String token;
    private MultipartFile file;

    public DocumentForm() { }

    public DocumentForm(String type, String kind, String description,
                        String viewRights, String projectArea, String projectName, String token,
                        MultipartFile file) {
        this.documentFormType = type;
        this.documentFormKind = kind;
        this.documentFormDescription = description;
        this.documentFormViewRights = viewRights;
        this.projectArea = projectArea;
        this.projectName = projectName;
        this.token = token;
        this.file = file;
    }

    public Document DocumentFormToDocument(int creator, String document_path, String creation_date,
                                          int type, int kind, int view_rights) {
        Document document = new Document(
                creator, getFile().getOriginalFilename(), document_path,
                creation_date, type, kind, getDocumentFormDescription(), view_rights
        );
        return document;
    }

    public String getDocumentFormType() {
        return documentFormType;
    }

    public void setDocumentFormType(String documentFormType) {
        this.documentFormType = documentFormType;
    }

    public String getDocumentFormKind() {
        return documentFormKind;
    }

    public void setDocumentFormKind(String kind) {
        this.documentFormKind = kind;
    }

    public String getDocumentFormDescription() {
        return documentFormDescription;
    }

    public void setDocumentFormDescription(String description) {
        this.documentFormDescription = description;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getDocumentFormViewRights() {
        return documentFormViewRights;
    }

    public void setDocumentFormViewRights(String documentFormViewRights) {
        this.documentFormViewRights = documentFormViewRights;
    }

    public String getProjectArea() {
        return projectArea;
    }

    public void setProjectArea(String projectArea) {
        this.projectArea = projectArea;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}