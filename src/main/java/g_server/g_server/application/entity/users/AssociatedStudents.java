package g_server.g_server.application.entity.users;

import g_server.g_server.application.entity.project.ProjectArea;

import javax.persistence.*;

@Entity
@Table(name = "associated_students")
public class AssociatedStudents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "scientific_advisor")
    private int scientificAdvisor;

    @Column(name = "student")
    private int student;

    @Column(name = "area")
    private int area;

    @Column(name = "is_accepted")
    private boolean Accepted;

    @ManyToOne
    @JoinColumn(name = "scientific_advisor", referencedColumnName = "id", insertable = false, updatable = false)
    private Users advisorUser;

    @ManyToOne
    @JoinColumn(name = "student", referencedColumnName = "id", insertable = false, updatable = false)
    private Users studentUser;

    @ManyToOne
    @JoinColumn(name = "area", referencedColumnName = "id", insertable = false, updatable = false)
    private ProjectArea projectArea;

    public AssociatedStudents() { }

    public AssociatedStudents(int scientificAdvisor, int student, int theme, boolean Accepted) {
        this.scientificAdvisor = scientificAdvisor;
        this.student = student;
        this.area = theme;
        this.Accepted = Accepted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScientificAdvisor() {
        return scientificAdvisor;
    }

    public void setScientificAdvisor(int scientificAdvisor) {
        this.scientificAdvisor = scientificAdvisor;
    }

    public int getStudent() {
        return student;
    }

    public void setStudent(int student) {
        this.student = student;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int theme) {
        this.area = theme;
    }

    public boolean isAccepted() {
        return Accepted;
    }

    public void setAccepted(boolean accepted) {
        Accepted = accepted;
    }

    public ProjectArea getProjectTheme() {
        return projectArea;
    }

    public void setProjectTheme(ProjectArea projectArea) {
        this.projectArea = projectArea;
    }

    public Users getAdvisorUser() {
        return advisorUser;
    }

    public void setAdvisorUser(Users advisorUser) {
        this.advisorUser = advisorUser;
    }

    public Users getStudentUser() {
        return studentUser;
    }

    public void setStudentUser(Users studentUser) {
        this.studentUser = studentUser;
    }
}