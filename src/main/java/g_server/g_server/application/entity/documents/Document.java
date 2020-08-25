package g_server.g_server.application.entity.documents;

import g_server.g_server.application.entity.Users;

import javax.persistence.*;

@Entity
@Table(name = "document")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int creator_id;

    @Column
    private String name;

    @Column
    private String document_path;

    @Column
    private String creation_date;

    @Column
    private int type;

    @Column
    private String description;

    @Column
    private int view_rights;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "view_rights", referencedColumnName = "id", insertable = false, updatable = false)
    private ViewRights viewRights;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "type", referencedColumnName = "id", insertable = false, updatable = false)
    private DocumentType documentType;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "creator_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Users user;

}
