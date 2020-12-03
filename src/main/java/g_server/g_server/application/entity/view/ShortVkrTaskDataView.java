package g_server.g_server.application.entity.view;

public class ShortVkrTaskDataView {
    private String taskType;
    private String studentTheme;
    private String vkrAim;
    private String vkrTasks;
    private String vkrDocs;

    public ShortVkrTaskDataView() { }

    public ShortVkrTaskDataView(String taskType, String studentTheme, String vkrAim, String vkrTasks, String vkrDocs) {
        this.taskType = taskType;
        this.studentTheme = studentTheme;
        this.vkrAim = vkrAim;
        this.vkrTasks = vkrTasks;
        this.vkrDocs = vkrDocs;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getStudentTheme() {
        return studentTheme;
    }

    public void setStudentTheme(String studentTheme) {
        this.studentTheme = studentTheme;
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
}
