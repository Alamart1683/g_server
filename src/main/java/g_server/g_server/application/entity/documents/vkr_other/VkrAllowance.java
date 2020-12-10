package g_server.g_server.application.entity.documents.vkr_other;

import g_server.g_server.application.entity.documents.DocumentStatus;

import javax.persistence.*;

@Entity
@Table(name = "vkr_allowance")
public class VkrAllowance {
    @Id
    @Column(name = "versionID")
    private Integer versionID;

    @Column(name = "vkr_allowance_status")
    private Integer allowanceStatus;

    @ManyToOne
    @JoinColumn(name = "vkr_allowance_status", referencedColumnName = "id", insertable = false, updatable = false)
    private DocumentStatus documentStatus;

    public VkrAllowance() { }

    public VkrAllowance(Integer versionID, Integer allowanceStatus) {
        this.versionID = versionID;
        this.allowanceStatus = allowanceStatus;
    }

    public Integer getVersionID() {
        return versionID;
    }

    public void setVersionID(Integer versionID) {
        this.versionID = versionID;
    }

    public Integer getAllowanceStatus() {
        return allowanceStatus;
    }

    public void setAllowanceStatus(Integer allowanceStatus) {
        this.allowanceStatus = allowanceStatus;
    }

    public DocumentStatus getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(DocumentStatus documentStatus) {
        this.documentStatus = documentStatus;
    }
}