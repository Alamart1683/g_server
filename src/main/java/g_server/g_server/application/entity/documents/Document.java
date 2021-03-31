package g_server.g_server.application.entity.documents;

import g_server.g_server.application.entity.users.Users;
import javax.persistence.*;

@Entity
@Table(name = "document")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "creator_id")
    private int creator;

    @Column
    private String name;

    @Column(name = "document_path")
    private String documentPath;

    @Column(name = "creation_date")
    private String creationDate;

    @Column
    private int type;

    @Column
    private int kind;

    @Column
    private String description;

    @Column(name = "view_rights")
    private int viewRightsInteger;

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

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    private TemplateProperties templateProperties;

    public Document() { }

    public Document(int creator_id, String name, String documentPath,
                    String creationDate, int type, int kind, String description, int viewRightsInteger) {
        this.creator = creator_id;
        this.name = name;
        this.documentPath = documentPath;
        this.creationDate = creationDate;
        this.type = type;
        this.kind = kind;
        this.description = description;
        this.viewRightsInteger = viewRightsInteger;
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

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String document_path) {
        this.documentPath = document_path;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creation_date) {
        this.creationDate = creation_date;
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

    public int getViewRightsInteger() {
        return viewRightsInteger;
    }

    public void setViewRightsInteger(int view_rights) {
        this.viewRightsInteger = view_rights;
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

    public TemplateProperties getTemplateProperties() {
        return templateProperties;
    }

    public void setTemplateProperties(TemplateProperties templateProperties) {
        this.templateProperties = templateProperties;
    }
}