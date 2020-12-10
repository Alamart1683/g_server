package g_server.g_server.application.entity.documents;

import javax.persistence.*;

@Entity
@Table(name = "area_document")
// Сущность привязки проектной области к документу
public class ViewRightsArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "document")
    private int document;

    @Column(name = "area")
    private int area;

    public ViewRightsArea() { }

    public ViewRightsArea(int document, int area) {
        this.document = document;
        this.area = area;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDocument() {
        return document;
    }

    public void setDocument(int document) {
        this.document = document;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }
}