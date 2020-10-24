package g_server.g_server.application.entity.documents;

import javax.persistence.*;

@Entity
@Table(name = "document_status")
public class DocumentStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "status")
    private String status;

    public DocumentStatus() { }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
