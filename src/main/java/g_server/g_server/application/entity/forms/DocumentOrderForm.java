package g_server.g_server.application.entity.forms;

import org.springframework.web.multipart.MultipartFile;

public class DocumentOrderForm extends DocumentForm {
    private String number;
    private String orderDate;
    private String startDate;
    private String endDate;
    private String speciality;

    public DocumentOrderForm() { }

    public DocumentOrderForm(String type, String kind, String description, String viewRights,
        String projectArea, String projectName, String token, String number, String orderDate,
        String startDate, String endDate, String speciality, MultipartFile file) {
        super(type, kind, description, viewRights, projectArea, projectName, token, file);
        this.number = number;
        this.orderDate = orderDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.speciality = speciality;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }
}
