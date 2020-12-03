package g_server.g_server.application.entity.documents;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vkr_allowance")
public class VkrAllowance {
    @Id
    @Column(name = "versionID")
    private Integer versionID;

    @Column(name = "vkr_allowance_status")
    private Integer allowanceStatus;

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
}