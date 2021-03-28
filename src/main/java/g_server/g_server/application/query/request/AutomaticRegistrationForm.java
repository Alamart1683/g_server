package g_server.g_server.application.query.request;

import org.springframework.web.multipart.MultipartFile;

public class AutomaticRegistrationForm {
    private String cathedra;
    private String type;
    private MultipartFile studentData;

    public AutomaticRegistrationForm() { }

    public AutomaticRegistrationForm(String cathedra, String type, MultipartFile studentData) {
        this.cathedra = cathedra;
        this.type = type;
        this.studentData = studentData;
    }

    public String getCathedra() {
        return cathedra;
    }

    public void setCathedra(String cathedra) {
        this.cathedra = cathedra;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MultipartFile getStudentData() {
        return studentData;
    }

    public void setStudentData(MultipartFile studentData) {
        this.studentData = studentData;
    }
}
