package g_server.g_server.application.query.response;

public class AssociatedStudentViewWithAdvisor {
    private Integer systemID;
    private Integer systemAdvisorID;
    private String advisorFio;
    private Integer systemStudentID;
    private String studentFio;
    private String studentGroup;
    private String studentSpeciality;
    private boolean studentIsConfirmed;

    public AssociatedStudentViewWithAdvisor(Integer systemID, Integer systemAdvisorID, String advisorFio,
            Integer systemStudentID, String studentFio, String studentGroup, String studentSpeciality,
            boolean studentIsConfirmed) {
        this.systemID = systemID;
        this.systemAdvisorID = systemAdvisorID;
        this.advisorFio = advisorFio;
        this.systemStudentID = systemStudentID;
        this.studentFio = studentFio;
        this.studentGroup = studentGroup;
        this.studentSpeciality = studentSpeciality;
        this.studentIsConfirmed = studentIsConfirmed;
    }

    public Integer getSystemAdvisorID() {
        return systemAdvisorID;
    }

    public void setSystemAdvisorID(Integer systemAdvisorID) {
        this.systemAdvisorID = systemAdvisorID;
    }

    public String getAdvisorFio() {
        return advisorFio;
    }

    public void setAdvisorFio(String advisorFio) {
        this.advisorFio = advisorFio;
    }

    public Integer getSystemID() {
        return systemID;
    }

    public void setSystemID(Integer systemID) {
        this.systemID = systemID;
    }

    public Integer getSystemStudentID() {
        return systemStudentID;
    }

    public void setSystemStudentID(Integer systemStudentID) {
        this.systemStudentID = systemStudentID;
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

    public String getStudentSpeciality() {
        return studentSpeciality;
    }

    public void setStudentSpeciality(String studentSpeciality) {
        this.studentSpeciality = studentSpeciality;
    }

    public boolean isStudentIsConfirmed() {
        return studentIsConfirmed;
    }

    public void setStudentIsConfirmed(boolean studentIsConfirmed) {
        this.studentIsConfirmed = studentIsConfirmed;
    }
}
