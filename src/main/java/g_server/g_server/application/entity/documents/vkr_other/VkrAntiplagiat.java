package g_server.g_server.application.entity.documents.vkr_other;

import g_server.g_server.application.entity.documents.DocumentStatus;

import javax.persistence.*;

@Entity
@Table(name = "vkr_antiplagiat")
public class VkrAntiplagiat {
    @Id
    @Column(name = "versionID")
    private Integer versionID;

    @Column(name = "vkr_aniplagiat_status")
    private Integer antiplagiatStatus;

    @ManyToOne
    @JoinColumn(name = "vkr_aniplagiat_status", referencedColumnName = "id", insertable = false, updatable = false)
    private DocumentStatus documentStatus;

    public VkrAntiplagiat() { }

    public VkrAntiplagiat(Integer versionID, Integer antiplagiatStatus) {
        this.versionID = versionID;
        this.antiplagiatStatus = antiplagiatStatus;
    }

    public Integer getVersionID() {
        return versionID;
    }

    public void setVersionID(Integer versionID) {
        this.versionID = versionID;
    }

    public Integer getAntiplagiatStatus() {
        return antiplagiatStatus;
    }

    public void setAntiplagiatStatus(Integer antiplagiatStatus) {
        this.antiplagiatStatus = antiplagiatStatus;
    }

    public DocumentStatus getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(DocumentStatus documentStatus) {
        this.documentStatus = documentStatus;
    }
}