package g_server.g_server.application.entity.documents.vkr_other;

import g_server.g_server.application.entity.documents.DocumentStatus;

import javax.persistence.*;

@Entity
@Table(name = "vkr_presentation")
public class VkrPresentation {
    @Id
    @Column(name = "versionID")
    private Integer versionID;

    @Column(name = "vkr_presentation_status")
    private Integer presentationStatus;

    @ManyToOne
    @JoinColumn(name = "vkr_presentation_status", referencedColumnName = "id", insertable = false, updatable = false)
    private DocumentStatus documentStatus;

    public VkrPresentation() { }

    public VkrPresentation(Integer versionID, Integer presentationStatus) {
        this.versionID = versionID;
        this.presentationStatus = presentationStatus;
    }

    public Integer getVersionID() {
        return versionID;
    }

    public Integer getPresentationStatus() {
        return presentationStatus;
    }

    public void setVersionID(Integer versionID) {
        this.versionID = versionID;
    }

    public void setPresentationStatus(Integer presentationStatus) {
        this.presentationStatus = presentationStatus;
    }

    public DocumentStatus getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(DocumentStatus documentStatus) {
        this.documentStatus = documentStatus;
    }
}