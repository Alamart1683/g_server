package g_server.g_server.application.entity.documents;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vkr_antiplagiat")
public class VkrAntiplagiat {
    @Id
    @Column(name = "versionID")
    private Integer versionID;

    @Column(name = "vkr_aniplagiat_status")
    private Integer antiplagiatStatus;

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
}