package g_server.g_server.application.entity.documents;

import javax.persistence.*;

@Entity
@Table(name = "document_kind")
public class DocumentKind {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String kind;

    public DocumentKind() {}

    public DocumentKind(String kind) {
        this.kind = kind;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}