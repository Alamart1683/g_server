package g_server.g_server.application.entity.project;

import javax.persistence.*;

@Entity
@Table(name = "project_area")
public class ProjectArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "advisor")
    private int advisor;

    @Column(name = "area")
    private String area;

    public ProjectArea() { }

    public ProjectArea(Integer advisor, String area) {
        this.advisor = advisor;
        this.area = area;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String theme) {
        this.area = theme;
    }

    public int getAdvisor() {
        return advisor;
    }

    public void setAdvisor(int advisor) {
        this.advisor = advisor;
    }
}
