package g_server.g_server.application.entity.forms;

import org.springframework.web.multipart.MultipartFile;

public class AdvisorReportDocumentForm extends DocumentForm {
    private int studentID;

    public AdvisorReportDocumentForm(String type, String kind, String description, String viewRights,
                                     String projectName, String token, MultipartFile file, int studentID) {
        super(type, kind, description, viewRights, projectName, token, file);
        this.studentID = studentID;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }
}
