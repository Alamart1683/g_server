package g_server.g_server.application.query.response;

import g_server.g_server.application.entity.documents.Document;
import java.util.List;

public class DocumentViewOrder extends DocumentView {
    private String orderDate;
    private String startDate;
    private String endDate;
    private String number;
    private String speciality;
    private String specialityCode;
    private boolean isApproved;

    public DocumentViewOrder(Document document, List<DocumentVersionView> documentVersions, String orderDate,
             String startDate, String endDate, String number, String speciality, String specialityCode, boolean isApproved) {
        super(document, documentVersions);
        this.orderDate = orderDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.number = number;
        this.speciality = speciality;
        this.specialityCode = specialityCode;
        this.isApproved = isApproved;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getSpecialityCode() {
        return specialityCode;
    }

    public void setSpecialityCode(String specialityCode) {
        this.specialityCode = specialityCode;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}