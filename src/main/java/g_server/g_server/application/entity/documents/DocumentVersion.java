package g_server.g_server.application.entity.documents;

import g_server.g_server.application.entity.Users;

import javax.persistence.*;

@Entity
@Table(name = "document_version")
public class DocumentVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int editor;

    @Column
    private int document;

    @Column
    private String edition_date;

    @Column
    private String edition_description;

    @Column
    private String this_version_document_path;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "document", referencedColumnName = "id", insertable = false, updatable = false)
    private Document parentDocument;

    // Привязка айди редактора версии документа напрямую из версии к пользователю в будущем даст
    // реализации возможности выдачи прав на чтение и запись для созданных пользователем документов,
    // тогда как если привязать его к айди создателя документа, это поставит крест на этой возможости, ибо
    // редактировать документ сможет только его создатель
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "editor", referencedColumnName = "id", insertable = false, updatable = false)
    private Users user;

    public DocumentVersion() { }

    public DocumentVersion(int editor, int document, String edition_date,
           String edition_description, String this_version_document_path) {
        this.editor = editor;
        this.document = document;
        this.edition_date = edition_date;
        this.edition_description = edition_description;
        this.this_version_document_path = this_version_document_path;
    }

    public int getEditor() {
        return editor;
    }

    public void setEditor(int editor) {
        this.editor = editor;
    }

    public int getDocument() {
        return document;
    }

    public void setDocument(int document) {
        this.document = document;
    }

    public String getEdition_date() {
        return edition_date;
    }

    public void setEdition_date(String edition_date) {
        this.edition_date = edition_date;
    }

    public String getEdition_description() {
        return edition_description;
    }

    public void setEdition_description(String edition_description) {
        this.edition_description = edition_description;
    }

    public String getThis_version_document_path() {
        return this_version_document_path;
    }

    public void setThis_version_document_path(String this_version_document_path) {
        this.this_version_document_path = this_version_document_path;
    }

    public Document getParentDocument() {
        return parentDocument;
    }

    public void setParentDocument(Document parentDocument) {
        this.parentDocument = parentDocument;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}