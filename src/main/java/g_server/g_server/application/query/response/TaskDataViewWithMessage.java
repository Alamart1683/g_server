package g_server.g_server.application.query.response;

public class TaskDataViewWithMessage {
    private TaskDataView taskDataView;
    private String message;

    public TaskDataViewWithMessage(TaskDataView taskDataView, String message) {
        this.taskDataView = taskDataView;
        this.message = message;
    }

    public TaskDataViewWithMessage() { }

    public TaskDataView getTaskDataView() {
        return taskDataView;
    }

    public void setTaskDataView(TaskDataView taskDataView) {
        this.taskDataView = taskDataView;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}