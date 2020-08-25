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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "editor", referencedColumnName = "id", insertable = false, updatable = false)
    private Users user;

    // Привязка айди редактора версии документа напрямую из версии к пользователю в будущем даст
    // реализации возможности выдачи прав на чтение и запись для созданных пользователем документов,
    // тогда как если привязать его к айди создателя документа, это поставит крест на этой возможости, ибо
    // редактировать документ сможет только его создатель
}