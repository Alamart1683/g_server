package g_server.g_server.application.entity.documents;

import g_server.g_server.application.entity.documents.DocumentType;
import g_server.g_server.application.entity.system_data.Speciality;

import javax.persistence.*;

@Entity
@Table(name = "template_properties")
public class TemplateProperties {
    @Id
    private int id;

    @Column(name = "type")
    private int type;

    @Column(name = "is_approved")
    private boolean isApproved;

    @ManyToOne
    @JoinColumn(name = "type", referencedColumnName = "id", insertable = false, updatable = false)
    private DocumentType documentType;

    public TemplateProperties() { }

    public TemplateProperties(int id, int type, boolean isApproved, DocumentType documentType) {
        this.id = id;
        this.type = type;
        this.isApproved = isApproved;
        this.documentType = documentType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
}
