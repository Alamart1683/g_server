package g_server.g_server.application.entity.documents;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vkr_advisor_conlusion")
public class VkrAdvisorConclusion {
    @Id
    @Column(name = "versionID")
    private Integer versionID;

    @Column(name = "vkr_advisor_conclusion_status")
    private Integer conclusionStatus;

    public VkrAdvisorConclusion() { }

    public VkrAdvisorConclusion(Integer versionID, Integer conclusionStatus) {
        this.versionID = versionID;
        this.conclusionStatus = conclusionStatus;
    }

    public Integer getVersionID() {
        return versionID;
    }

    public void setVersionID(Integer versionID) {
        this.versionID = versionID;
    }

    public Integer getConclusionStatus() {
        return conclusionStatus;
    }

    public void setConclusionStatus(Integer conclusionStatus) {
        this.conclusionStatus = conclusionStatus;
    }
}