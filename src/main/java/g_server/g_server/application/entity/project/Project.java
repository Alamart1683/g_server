package g_server.g_server.application.entity.project;

import g_server.g_server.application.entity.users.ScientificAdvisorData;
import javax.persistence.*;

@Entity
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "area")
    private int area;

    @Column(name = "name")
    private String name;

    @Column(name = "scientific_advisor_id")
    private int scientificAdvisorID;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "area", insertable = false, updatable = false)
    private ProjectArea projectArea;

    @ManyToOne
    @JoinColumn(name = "scientific_advisor_id", insertable = false, updatable = false)
    private ScientificAdvisorData scientificAdvisorData;

    public Project() { }

    public Project(int area, String name, int scientificAdvisorID, String description) {
        this.area = area;
        this.name = name;
        this.scientificAdvisorID = scientificAdvisorID;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int type) {
        this.area = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScientificAdvisorID() {
        return scientificAdvisorID;
    }

    public void setScientificAdvisorID(int scientificAdvisorID) {
        this.scientificAdvisorID = scientificAdvisorID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectArea getProjectArea() {
        return projectArea;
    }

    public void setProjectArea(ProjectArea projectArea) {
        this.projectArea = projectArea;
    }

    public ScientificAdvisorData getScientificAdvisorData() {
        return scientificAdvisorData;
    }

    public void setScientificAdvisorData(ScientificAdvisorData scientificAdvisorData) {
        this.scientificAdvisorData = scientificAdvisorData;
    }
}
