package g_server.g_server.application.query.response;

public class AdvisorShortVkrTaskDataView extends ShortVkrTaskDataView {
    private Integer studentID;

    public AdvisorShortVkrTaskDataView() {

    }

    public AdvisorShortVkrTaskDataView(Integer studentID) {
        this.studentID = studentID;
    }

    public AdvisorShortVkrTaskDataView(String taskType, String studentTheme, String vkrAim, String vkrTasks,
                                       String vkrDocs, Integer studentID) {
        super(taskType, studentTheme, vkrAim, vkrTasks, vkrDocs);
        this.studentID = studentID;
    }

    public Integer getStudentID() {
        return studentID;
    }

    public void setStudentID(Integer studentID) {
        this.studentID = studentID;
    }
}
