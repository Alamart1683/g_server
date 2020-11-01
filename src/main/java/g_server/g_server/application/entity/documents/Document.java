package g_server.g_server.application.entity.documents;

import g_server.g_server.application.entity.users.Roles;
import g_server.g_server.application.entity.users.Users;
import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "document")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "creator_id") // Костыль ибо генератор методов JPA не воспринимает подчеркивания в названиях полей
    private int creator;

    @Column
    private String name;

    @Column
    private String document_path;

    @Column
    private String creation_date;

    @Column
    private int type;

    @Column
    private int kind;

    @Column
    private String description;

    @Column
    private int view_rights;

    @ManyToOne
    @JoinColumn(name = "view_rights", referencedColumnName = "id", insertable = false, updatable = false)
    private ViewRights viewRights;

    @ManyToOne
    @JoinColumn(name = "type", referencedColumnName = "id", insertable = false, updatable = false)
    private DocumentType documentType;

    @ManyToOne
    @JoinColumn(name = "kind", referencedColumnName = "id", insertable = false, updatable = false)
    private DocumentKind documentKind;

    @ManyToOne
    @JoinColumn(name = "creator_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Users user;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    private OrderProperties orderProperties;

    public Document() { }

    public Document(int creator_id, String name, String document_path,
                    String creation_date, int type, int kind, String description, int view_rights) {
        this.creator = creator_id;
        this.name = name;
        this.document_path = document_path;
        this.creation_date = creation_date;
        this.type = type;
        this.kind = kind;
        this.description = description;
        this.view_rights = view_rights;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocument_path() {
        return document_path;
    }

    public void setDocument_path(String document_path) {
        this.document_path = document_path;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getView_rights() {
        return view_rights;
    }

    public void setView_rights(int view_rights) {
        this.view_rights = view_rights;
    }

    public ViewRights getViewRights() {
        return viewRights;
    }

    public void setViewRights(ViewRights viewRights) {
        this.viewRights = viewRights;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public DocumentKind getDocumentKind() {
        return documentKind;
    }

    public void setDocumentKind(DocumentKind documentKind) {
        this.documentKind = documentKind;
    }

    public OrderProperties getOrderProperties() {
        return orderProperties;
    }

    public void setOrderProperties(OrderProperties orderProperties) {
        this.orderProperties = orderProperties;
    }
}
