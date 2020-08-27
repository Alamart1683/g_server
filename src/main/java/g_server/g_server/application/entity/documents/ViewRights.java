package g_server.g_server.application.entity.documents;

import javax.persistence.*;

@Entity
@Table(name = "view_rights")
public class ViewRights {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String view_right;

    public ViewRights() { }

    public ViewRights(String view_right) {
        this.view_right = view_right;
    }

    public String getView_right() {
        return view_right;
    }

    public void setView_right(String view_right) {
        this.view_right = view_right;
    }
}