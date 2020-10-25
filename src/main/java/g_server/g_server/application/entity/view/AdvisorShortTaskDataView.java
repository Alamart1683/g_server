package g_server.g_server.application.entity.view;

public class AdvisorShortTaskDataView extends ShortTaskDataView {
    private int studentID;

    public AdvisorShortTaskDataView(String taskType, String studentTheme, String toExplore, String toCreate,
                                    String toFamiliarize, String additionalTask, int studentID) {
        super(taskType, studentTheme, toExplore, toCreate, toFamiliarize, additionalTask);
        this.studentID = studentID;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }
}
