package g_server.g_server.application.entity.documents;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vkr_presentation")
public class VkrPresentation {
    @Id
    @Column(name = "versionID")
    private Integer versionID;

    @Column(name = "vkr_presentation_status")
    private Integer presentationStatus;

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
}