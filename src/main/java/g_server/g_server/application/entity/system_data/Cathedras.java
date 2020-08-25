package g_server.g_server.application.entity.system_data;

import javax.persistence.*;

@Entity
@Table(name = "cathedras")
public class Cathedras {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "cathedra_name")
    private String cathedraName;

    public Cathedras() { }

    public Cathedras(String cathedra_name) {
        this.cathedraName = cathedra_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCathedraName() {
        return cathedraName;
    }

    public void setCathedraName(String cathedra_name) {
        this.cathedraName = cathedra_name;
    }
}
