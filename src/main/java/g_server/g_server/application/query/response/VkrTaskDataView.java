package g_server.g_server.application.query.response;

public class VkrTaskDataView {
    private String taskType;
    private String studentFio;
    private String studentGroup;
    private String studentTheme;
    private String studentCode;
    private String advisorFio;
    private String EconomyConsultantFio;
    private String headFio;
    private String cathedra;
    private String orderNumber;
    private String orderDate;
    private String orderStartDate;
    private String orderEndDate;
    private String orderSpeciality;
    private String taskAim;
    private String taskTasks;
    private String taskDocs;

    public VkrTaskDataView() { }

    public VkrTaskDataView(String taskType, String studentFio, String studentGroup, String studentTheme,
            String studentCode, String advisorFio, String economyConsultantFio, String headFio,
            String cathedra, String orderNumber, String orderDate, String orderStartDate,
            String orderEndDate, String orderSpeciality, String taskAim, String taskTasks, String taskDocs) {
        this.taskType = taskType;
        this.studentFio = studentFio;
        this.studentGroup = studentGroup;
        this.studentTheme = studentTheme;
        this.studentCode = studentCode;
        this.advisorFio = advisorFio;
        EconomyConsultantFio = economyConsultantFio;
        this.headFio = headFio;
        this.cathedra = cathedra;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.orderStartDate = orderStartDate;
        this.orderEndDate = orderEndDate;
        this.orderSpeciality = orderSpeciality;
        this.taskAim = taskAim;
        this.taskTasks = taskTasks;
        this.taskDocs = taskDocs;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
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

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getAdvisorFio() {
        return advisorFio;
    }

    public void setAdvisorFio(String advisorFio) {
        this.advisorFio = advisorFio;
    }

    public String getEconomyConsultantFio() {
        return EconomyConsultantFio;
    }

    public void setEconomyConsultantFio(String economyConsultantFio) {
        EconomyConsultantFio = economyConsultantFio;
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

    public String getTaskAim() {
        return taskAim;
    }

    public void setTaskAim(String taskAim) {
        this.taskAim = taskAim;
    }

    public String getTaskTasks() {
        return taskTasks;
    }

    public void setTaskTasks(String taskTasks) {
        this.taskTasks = taskTasks;
    }

    public String getTaskDocs() {
        return taskDocs;
    }

    public void setTaskDocs(String taskDocs) {
        this.taskDocs = taskDocs;
    }
}
