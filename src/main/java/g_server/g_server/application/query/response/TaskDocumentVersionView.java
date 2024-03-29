package g_server.g_server.application.query.response;

import g_server.g_server.application.entity.documents.*;
import g_server.g_server.application.entity.documents.tasks.NirTask;
import g_server.g_server.application.entity.documents.tasks.PdTask;
import g_server.g_server.application.entity.documents.tasks.PpppuiopdTask;
import g_server.g_server.application.entity.documents.tasks.VkrTask;

public class TaskDocumentVersionView extends DocumentVersionView {
    private Integer systemVersionID;
    private String theme;
    private String toExplore;
    private String toCreate;
    private String toFamiliarize;
    private String additionalTask;
    private boolean isVkr = false;
    private String vkrAim;
    private String vkrTasks;
    private String vkrDocs;
    private String status;
    private boolean isHocRate;

    public TaskDocumentVersionView(DocumentVersion documentVersion, NirTask nirTask) {
        super(documentVersion);
        this.systemVersionID = documentVersion.getId();
        this.theme = nirTask.getTheme();
        this.toExplore = nirTask.getToExplore();
        this.toCreate = nirTask.getToCreate();
        this.toFamiliarize = nirTask.getToFamiliarize();
        this.additionalTask = nirTask.getAdditionalTask();
        this.status = nirTask.getDocumentStatus().getStatus();
        this.isHocRate = nirTask.isHocRate();
    }

    public TaskDocumentVersionView(DocumentVersion documentVersion, PpppuiopdTask ppppuiopdTask) {
        super(documentVersion);
        this.systemVersionID = documentVersion.getId();
        this.theme = ppppuiopdTask.getTheme();
        this.toExplore = ppppuiopdTask.getToExplore();
        this.toCreate = ppppuiopdTask.getToCreate();
        this.toFamiliarize = ppppuiopdTask.getToFamiliarize();
        this.additionalTask = ppppuiopdTask.getAdditionalTask();
        this.status = ppppuiopdTask.getDocumentStatus().getStatus();
        this.isHocRate = ppppuiopdTask.isHocRate();
    }

    public TaskDocumentVersionView(DocumentVersion documentVersion, PdTask pdTask) {
        super(documentVersion);
        this.systemVersionID = documentVersion.getId();
        this.theme = pdTask.getTheme();
        this.toExplore = pdTask.getToExplore();
        this.toCreate = pdTask.getToCreate();
        this.toFamiliarize = pdTask.getToFamiliarize();
        this.additionalTask = pdTask.getAdditionalTask();
        this.status = pdTask.getDocumentStatus().getStatus();
        this.isHocRate = pdTask.isHocRate();
    }

    public TaskDocumentVersionView(DocumentVersion documentVersion, VkrTask vkrTask) {
        super(documentVersion);
        this.isVkr = true;
        this.systemVersionID = documentVersion.getId();
        this.theme = vkrTask.getVkrTheme();
        this.vkrAim = vkrTask.getVkrAim();
        this.vkrTasks = vkrTask.getVkrTask();
        this.vkrDocs = vkrTask.getVkrDocs();
        this.status = vkrTask.getDocumentStatus().getStatus();
        this.isHocRate = vkrTask.isHocRate();
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

    public boolean isVkr() {
        return isVkr;
    }

    public void setVkr(boolean vkr) {
        isVkr = vkr;
    }

    public String getVkrAim() {
        return vkrAim;
    }

    public void setVkrAim(String vkrAim) {
        this.vkrAim = vkrAim;
    }

    public String getVkrTasks() {
        return vkrTasks;
    }

    public void setVkrTasks(String vkrTasks) {
        this.vkrTasks = vkrTasks;
    }

    public String getVkrDocs() {
        return vkrDocs;
    }

    public void setVkrDocs(String vkrDocs) {
        this.vkrDocs = vkrDocs;
    }

    public boolean isHocRate() {
        return isHocRate;
    }

    public void setHocRate(boolean hocRate) {
        isHocRate = hocRate;
    }
}
