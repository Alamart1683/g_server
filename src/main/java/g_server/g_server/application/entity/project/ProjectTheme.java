package g_server.g_server.application.entity.project;

import javax.persistence.*;

@Entity
@Table(name = "project_theme")
public class ProjectTheme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "advisor")
    private int advisor;

    @Column(name = "theme")
    private String theme;

    public ProjectTheme() { }

    public ProjectTheme(Integer advisor, String theme) {
        this.advisor = advisor;
        this.theme = theme;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public int getAdvisor() {
        return advisor;
    }

    public void setAdvisor(int advisor) {
        this.advisor = advisor;
    }
}
