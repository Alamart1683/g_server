package g_server.g_server.application.entity.documents.tasks;

import g_server.g_server.application.entity.documents.DocumentStatus;

import javax.persistence.*;

@Entity
@Table(name = "vkr_task")
public class VkrTask {
    @Id
    @Column(name = "versionID")
    private Integer versionID;

    @Column(name = "theme")
    private String vkrTheme;

    @Column(name = "aim")
    private String vkrAim;

    @Column(name = "tasks")
    private String vkrTask;

    @Column(name = "docs")
    private String vkrDocs;

    @Column(name = "vkr_status")
    private Integer vkr_status;

    @ManyToOne
    @JoinColumn(name = "vkr_status", referencedColumnName = "id", insertable = false, updatable = false)
    private DocumentStatus documentStatus;

    public VkrTask() { }

    public VkrTask(Integer versionID, String vkrTheme, String vkrAim, String vkrTask, String vkrDocs, Integer vkr_status) {
        this.versionID = versionID;
        this.vkrTheme = vkrTheme;
        this.vkrAim = vkrAim;
        this.vkrTask = vkrTask;
        this.vkrDocs = vkrDocs;
        this.vkr_status = vkr_status;
    }

    public Integer getVkr_status() {
        return vkr_status;
    }

    public void setVkr_status(Integer vkr_status) {
        this.vkr_status = vkr_status;
    }

    public Integer getVersionID() {
        return versionID;
    }

    public void setVersionID(Integer versionID) {
        this.versionID = versionID;
    }

    public String getVkrTheme() {
        return vkrTheme;
    }

    public void setVkrTheme(String vkrTheme) {
        this.vkrTheme = vkrTheme;
    }

    public String getVkrAim() {
        return vkrAim;
    }

    public void setVkrAim(String vkrAim) {
        this.vkrAim = vkrAim;
    }

    public String getVkrTask() {
        return vkrTask;
    }

    public void setVkrTask(String vkrTask) {
        this.vkrTask = vkrTask;
    }

    public String getVkrDocs() {
        return vkrDocs;
    }

    public void setVkrDocs(String vkrDocs) {
        this.vkrDocs = vkrDocs;
    }

    public DocumentStatus getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(DocumentStatus documentStatus) {
        this.documentStatus = documentStatus;
    }
}