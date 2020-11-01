package g_server.g_server.application.entity.documents;

import javax.persistence.*;

@Entity
@Table(name = "project_document")
// Класс таблицы привзяки области видимости документа к конкретному проекту
public class ViewRightsProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int project;

    @Column
    private int document;

    public ViewRightsProject() { }

    public ViewRightsProject(int project, int document) {
        this.project = project;
        this.document = document;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProject() {
        return project;
    }

    public void setProject(int project) {
        this.project = project;
    }

    public int getDocument() {
        return document;
    }

    public void setDocument(int document) {
        this.document = document;
    }
}