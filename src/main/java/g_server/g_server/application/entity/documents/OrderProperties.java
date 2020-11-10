package g_server.g_server.application.entity.documents;

import g_server.g_server.application.entity.system_data.Speciality;
import javax.persistence.*;

@Entity
@Table(name = "order_properties")
public class OrderProperties {
    @Id
    private int id;

    @Column(name = "number")
    private String number;

    @Column(name = "order_date")
    private String orderDate;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "end_date")
    private String endDate;

    @Column(name = "speciality")
    private int speciality;

    @Column(name = "is_approved")
    private boolean isApproved;

    @ManyToOne
    @JoinColumn(name = "speciality", referencedColumnName = "id", insertable = false, updatable = false)
    private Speciality specialityTable;

    public OrderProperties() { }

    public OrderProperties(int id, String number, String orderDate, String startDate,
        String endDate, int speciality) {
        this.id = id;
        this.number = number;
        this.orderDate = orderDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.speciality = speciality;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getSpeciality() {
        return speciality;
    }

    public void setSpeciality(int speciality) {
        this.speciality = speciality;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}