package g_server.g_server.application.entity.documents;

import g_server.g_server.application.entity.users.Users;

import javax.persistence.*;

@Entity
@Table(name = "document_version")
public class DocumentVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int editor;

    @Column(name = "document")
    private int document;

    @Column(name = "edition_date")
    private String editionDate;

    @Column
    private String edition_description;

    @Column
    private String this_version_document_path;

    @ManyToOne
    @JoinColumn(name = "document", referencedColumnName = "id", insertable = false, updatable = false)
    private Document parentDocument;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "versionID", insertable = false, updatable = false)
    private NirTask nirTask;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "versionID", insertable = false, updatable = false)
    private NirReport nirReport;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "versionID", insertable = false, updatable = false)
    private PpppuiopdTask ppppuiopdTask;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "versionID", insertable = false, updatable = false)
    private PpppuiopdReport ppppuiopdReport;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "versionID", insertable = false, updatable = false)
    private PdTask pdTask;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "versionID", insertable = false, updatable = false)
    private PdReport pdReport;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "versionID", insertable = false, updatable = false)
    private VkrTask vkrTask;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "versionID", insertable = false, updatable = false)
    private VkrReport vkrReport;

    // Привязка айди редактора версии документа напрямую из версии к пользователю в будущем даст
    // реализации возможности выдачи прав на чтение и запись для созданных пользователем документов,
    // тогда как если привязать его к айди создателя документа, это поставит крест на этой возможости, ибо
    // редактировать документ сможет только его создатель
    @ManyToOne
    @JoinColumn(name = "editor", referencedColumnName = "id", insertable = false, updatable = false)
    private Users user;

    public DocumentVersion() { }

    public DocumentVersion(int editor, int document, String edition_date,
           String edition_description, String this_version_document_path) {
        this.editor = editor;
        this.document = document;
        this.editionDate = edition_date;
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

    public String getEditionDate() {
        return editionDate;
    }

    public void setEditionDate(String edition_date) {
        this.editionDate = edition_date;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public NirTask getNirTask() {
        return nirTask;
    }

    public void setNirTask(NirTask nirTask) {
        this.nirTask = nirTask;
    }

    public NirReport getNirReport() {
        return nirReport;
    }

    public void setNirReport(NirReport nirReport) {
        this.nirReport = nirReport;
    }

    public PpppuiopdTask getPpppuiopdTask() {
        return ppppuiopdTask;
    }

    public void setPpppuiopdTask(PpppuiopdTask ppppuiopdTask) {
        this.ppppuiopdTask = ppppuiopdTask;
    }

    public PpppuiopdReport getPpppuiopdReport() {
        return ppppuiopdReport;
    }

    public void setPpppuiopdReport(PpppuiopdReport ppppuiopdReport) {
        this.ppppuiopdReport = ppppuiopdReport;
    }

    public PdTask getPdTask() {
        return pdTask;
    }

    public void setPdTask(PdTask pdTask) {
        this.pdTask = pdTask;
    }

    public PdReport getPdReport() {
        return pdReport;
    }

    public void setPdReport(PdReport pdReport) {
        this.pdReport = pdReport;
    }

    public VkrTask getVkrTask() {
        return vkrTask;
    }

    public void setVkrTask(VkrTask vkrTask) {
        this.vkrTask = vkrTask;
    }

    public VkrReport getVkrReport() {
        return vkrReport;
    }

    public void setVkrReport(VkrReport vkrReport) {
        this.vkrReport = vkrReport;
    }
}