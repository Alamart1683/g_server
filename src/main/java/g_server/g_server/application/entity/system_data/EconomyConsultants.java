package g_server.g_server.application.entity.system_data;

import javax.persistence.*;

@Entity
@Table(name = "economy_consultants")
public class EconomyConsultants {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int consultantID;

    @Column(name = "post")
    private String post;

    @Column(name = "fio")
    private String fio;

    public EconomyConsultants() { }

    public int getConsultantID() {
        return consultantID;
    }

    public void setConsultantID(int consultantID) {
        this.consultantID = consultantID;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }
}
