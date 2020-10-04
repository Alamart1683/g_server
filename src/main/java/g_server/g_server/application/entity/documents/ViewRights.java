package g_server.g_server.application.entity.documents;

import javax.persistence.*;

@Entity
@Table(name = "view_rights")
public class ViewRights {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "view_right")
    private String viewRight;

    public ViewRights() { }

    public ViewRights(String viewRight) {
        this.viewRight = viewRight;
    }

    public String getViewRight() {
        return viewRight;
    }

    public void setViewRight(String view_right) {
        this.viewRight = view_right;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}