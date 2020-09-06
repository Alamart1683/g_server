package g_server.g_server.application.entity.users;

import g_server.g_server.application.entity.system_data.Cathedras;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "scientific_advisor_data")
public class ScientificAdvisorData {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "cathedra")
    private int cathedra;

    @Column(name = "places")
    private int places;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    private Roles roles;

    @ManyToOne
    @JoinColumn(name = "cathedra", referencedColumnName = "id", insertable = false, updatable = false)
    private Cathedras cathedras;

    public ScientificAdvisorData() { }

    public ScientificAdvisorData(int id, int cathedra, int places) {
        this.id = id;
        this.cathedra = cathedra;
        this.places = places;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCathedra() {
        return cathedra;
    }

    public void setCathedra(int cathedra) {
        this.cathedra = cathedra;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public Cathedras getCathedras() {
        return cathedras;
    }

    public void setCathedras(Cathedras cathedras) {
        this.cathedras = cathedras;
    }

    public int getPlaces() {
        return places;
    }

    public void setPlaces(int places) {
        this.places = places;
    }
}