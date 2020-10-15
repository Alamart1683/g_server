package g_server.g_server.application.entity.view;

public class TaskDataView {
    private String taskType;
    private String studentFio;
    private String studentGroup;
    private String studentTheme;
    private String advisorFio;
    private String headFio;
    private String cathedra;
    private String orderNumber;
    private String orderDate;
    private String orderStartDate;
    private String orderEndDate;
    private String orderSpeciality;
    private String toExplore;
    private String toCreate;
    private String toFamiliarize;
    private String additionalTask;

    public TaskDataView() { }

    public TaskDataView(String taskType, String studentFio, String studentGroup, String studentTheme, String advisorFio,
                        String headFio, String cathedra, String orderNumber, String orderDate, String orderStartDate,
                        String orderEndDate, String orderSpeciality, String toExplore, String toCreate, String toFamiliarize,
                        String additionalTask) {
        this.taskType = taskType;
        this.studentFio = studentFio;
        this.studentGroup = studentGroup;
        this.studentTheme = studentTheme;
        this.advisorFio = advisorFio;
        this.headFio = headFio;
        this.cathedra = cathedra;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.orderStartDate = orderStartDate;
        this.orderEndDate = orderEndDate;
        this.orderSpeciality = orderSpeciality;
        this.toExplore = toExplore;
        this.toCreate = toCreate;
        this.toFamiliarize = toFamiliarize;
        this.additionalTask = additionalTask;
    }

    public String getStudentFio() {
        return studentFio;
    }

    public void setStudentFio(String studentFio) {
        this.studentFio = studentFio;
    }

    public String getStudentGroup() {
        return studentGroup;
    }

    public void setStudentGroup(String studentGroup) {
        this.studentGroup = studentGroup;
    }

    public String getStudentTheme() {
        return studentTheme;
    }

    public void setStudentTheme(String studentTheme) {
        this.studentTheme = studentTheme;
    }

    public String getAdvisorFio() {
        return advisorFio;
    }

    public void setAdvisorFio(String advisorFio) {
        this.advisorFio = advisorFio;
    }

    public String getHeadFio() {
        return headFio;
    }

    public void setHeadFio(String headFio) {
        this.headFio = headFio;
    }

    public String getCathedra() {
        return cathedra;
    }

    public void setCathedra(String cathedra) {
        this.cathedra = cathedra;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStartDate() {
        return orderStartDate;
    }

    public void setOrderStartDate(String orderStartDate) {
        this.orderStartDate = orderStartDate;
    }

    public String getOrderEndDate() {
        return orderEndDate;
    }

    public void setOrderEndDate(String orderEndDate) {
        this.orderEndDate = orderEndDate;
    }

    public String getOrderSpeciality() {
        return orderSpeciality;
    }

    public void setOrderSpeciality(String orderSpeciality) {
        this.orderSpeciality = orderSpeciality;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
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
}