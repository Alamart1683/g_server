package g_server.g_server.application.entity;

import javax.persistence.*;

@Entity
@Table(name = "scientific_advisor_data")
public class ScientificAdvisorData {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "cathedra")
    private int cathedra;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id", referencedColumnName = "id")
    private UserRole userRole;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cathedra", referencedColumnName = "id", insertable = false, updatable = false)
    private Cathedras cathedras;

    public ScientificAdvisorData() { }

    public ScientificAdvisorData(int id, int cathedra) {
        this.id = id;
        this.cathedra = cathedra;
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

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public Cathedras getCathedras() {
        return cathedras;
    }

    public void setCathedras(Cathedras cathedras) {
        this.cathedras = cathedras;
    }
}
