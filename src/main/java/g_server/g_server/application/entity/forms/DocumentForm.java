package g_server.g_server.application.entity.forms;

import g_server.g_server.application.entity.documents.Document;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

public class DocumentForm {
    private String name;
    private String type;
    private String kind;
    private String description;
    private String token;
    private MultipartFile file;

    public DocumentForm() { }

    public DocumentForm(String name, String type, String kind, String description,
                        String token, MultipartFile file) {
        this.name = name;
        this.type = type;
        this.kind = kind;
        this.description = description;
        this.token = token;
        this.file = file;
    }

    public Document DocumentFormToDocument(int creator, String document_path, String creation_date,
                                          int type, int kind, int view_rights ) {
        Document document = new Document(
                creator, getDocumentFormName(), document_path, creation_date, type,
                kind, getDocumentFormDescription(), view_rights
        );
        return document;
    }

    public String getDocumentFormName() {
        return name;
    }

    public void setDocumentFormName(String name) {
        this.name = name;
    }

    public String getDocumentFormType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDocumentFormKind() {
        return kind;
    }

    public void setDocumentFormKind(String kind) {
        this.kind = kind;
    }

    public String getDocumentFormDescription() {
        return description;
    }

    public void setDocumentFormDescription(String description) {
        this.description = description;
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
}