package g_server.g_server.application.entity.documents.tasks;

import g_server.g_server.application.entity.documents.DocumentStatus;

import javax.persistence.*;

@Entity
@Table(name = "pd_task")
public class PdTask {
    @Id
    @Column(name = "versionID")
    private int versionID;

    @Column(name = "theme")
    private String theme;

    @Column(name = "to_explore")
    private String toExplore;

    @Column(name = "to_create")
    private String toCreate;

    @Column(name = "to_familiarize")
    private String toFamiliarize;

    @Column(name = "additional_task")
    private String additionalTask;

    @Column(name = "pd_status")
    private int status;

    @ManyToOne
    @JoinColumn(name = "pd_status", referencedColumnName = "id", insertable = false, updatable = false)
    private DocumentStatus documentStatus;

    public PdTask() { }

    public PdTask(int studentID, String theme, String toExplore, String toCreate,
                         String toFamiliarize, String additionalTask, int status) {
        this.versionID = studentID;
        this.theme = theme;
        this.toExplore = toExplore;
        this.toCreate = toCreate;
        this.toFamiliarize = toFamiliarize;
        this.additionalTask = additionalTask;
        this.status = status;
    }

    public int getVersionID() {
        return versionID;
    }

    public void setVersionID(int versionID) {
        this.versionID = versionID;
    }

    public String getToExplore() {
        return toExplore;
    }

    public void setToExplore(String toExplore) {
        this.toExplore = toExplore;
    }

    public String getToCreate() {
        return toCreate;
    }

    public void setToCreate(String toCreate) {
        this.toCreate = toCreate;
    }

    public String getToFamiliarize() {
        return toFamiliarize;
    }

    public void setToFamiliarize(String toFamiliarize) {
        this.toFamiliarize = toFamiliarize;
    }

    public String getAdditionalTask() {
        return additionalTask;
    }

    public void setAdditionalTask(String additionalTask) {
        this.additionalTask = additionalTask;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DocumentStatus getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(DocumentStatus documentStatus) {
        this.documentStatus = documentStatus;
    }
}