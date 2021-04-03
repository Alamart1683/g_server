package g_server.g_server.application.query.response;

public class TwoOrdersTaskDataView {
    private String taskType;
    private String studentFio;
    private String studentGroup;
    private String studentTheme;
    private String advisorFio;
    private String headFio;
    private String cathedra;
    private String firstOrderNumber;
    private String firstOrderDate;
    private String secondOrderNumber;
    private String secondOrderDate;
    private String orderStartDate;
    private String orderEndDate;
    private String orderSpeciality;
    private String toExplore;
    private String toCreate;
    private String toFamiliarize;
    private String additionalTask;

    public TwoOrdersTaskDataView() { }

    public TwoOrdersTaskDataView(String taskType, String studentFio, String studentGroup, String studentTheme,
                                 String advisorFio, String headFio, String cathedra, String firstOrderNumber,
                                 String firstOrderDate, String secondOrderNumber, String secondOrderDate,
                                 String orderStartDate, String orderEndDate, String orderSpeciality, String toExplore,
                                 String toCreate, String toFamiliarize, String additionalTask) {
        this.taskType = taskType;
        this.studentFio = studentFio;
        this.studentGroup = studentGroup;
        this.studentTheme = studentTheme;
        this.advisorFio = advisorFio;
        this.headFio = headFio;
        this.cathedra = cathedra;
        this.firstOrderNumber = firstOrderNumber;
        this.firstOrderDate = firstOrderDate;
        this.secondOrderNumber = secondOrderNumber;
        this.secondOrderDate = secondOrderDate;
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

    public String getFirstOrderNumber() {
        return firstOrderNumber;
    }

    public void setFirstOrderNumber(String firstOrderNumber) {
        this.firstOrderNumber = firstOrderNumber;
    }

    public String getFirstOrderDate() {
        return firstOrderDate;
    }

    public void setFirstOrderDate(String firstOrderDate) {
        this.firstOrderDate = firstOrderDate;
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

    public String getSecondOrderNumber() {
        return secondOrderNumber;
    }

    public void setSecondOrderNumber(String secondOrderNumber) {
        this.secondOrderNumber = secondOrderNumber;
    }

    public String getSecondOrderDate() {
        return secondOrderDate;
    }

    public void setSecondOrderDate(String secondOrderDate) {
        this.secondOrderDate = secondOrderDate;
    }
}
