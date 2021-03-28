package g_server.g_server.application.query.response;

public class ShortTaskDataView {
    private String taskType;
    private String studentTheme;
    private String toExplore;
    private String toCreate;
    private String toFamiliarize;
    private String additionalTask;

    public ShortTaskDataView() { }

    public ShortTaskDataView(String taskType, String studentTheme, String toExplore,
                             String toCreate, String toFamiliarize, String additionalTask) {
        this.taskType = taskType;
        this.studentTheme = studentTheme;
        this.toExplore = toExplore;
        this.toCreate = toCreate;
        this.toFamiliarize = toFamiliarize;
        this.additionalTask = additionalTask;
    }

    public String getStudentTheme() {
        return studentTheme;
    }

    public void setStudentTheme(String studentTheme) {
        this.studentTheme = studentTheme;
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

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
}
