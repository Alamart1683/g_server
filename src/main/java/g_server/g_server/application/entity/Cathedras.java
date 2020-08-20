package g_server.g_server.application.entity;

import javax.persistence.*;

@Entity
@Table(name = "cathedras")
public class Cathedras {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "cathedra_name")
    private String cathedra_name;

    public Cathedras() { }

    public Cathedras(int id, String cathedra_name) {
        this.id = id;
        this.cathedra_name = cathedra_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCathedra_name() {
        return cathedra_name;
    }

    public void setCathedra_name(String cathedra_name) {
        this.cathedra_name = cathedra_name;
    }
}
