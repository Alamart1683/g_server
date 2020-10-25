package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.entity.documents.NirTask;

public class TaskDocumentVersionView extends DocumentVersionView {
    private Integer systemVersionID;
    private String theme;
    private String toExplore;
    private String toCreate;
    private String toFamiliarize;
    private String additionalTask;
    private String status;

    public TaskDocumentVersionView(DocumentVersion documentVersion, NirTask nirTask) {
        super(documentVersion);
        this.systemVersionID = documentVersion.getId();
        this.theme = nirTask.getTheme();
        this.toExplore = nirTask.getToExplore();
        this.toCreate = nirTask.getToCreate();
        this.toFamiliarize = nirTask.getToFamiliarize();
        this.additionalTask = nirTask.getAdditionalTask();
        this.status = nirTask.getDocumentStatus().getStatus();
    }

    public TaskDocumentVersionView(DocumentVersion documentVersion, String status) {
        super(documentVersion);
        this.status = status;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSystemVersionID() {
        return systemVersionID;
    }

    public void setSystemVersionID(Integer systemVersionID) {
        this.systemVersionID = systemVersionID;
    }
}
