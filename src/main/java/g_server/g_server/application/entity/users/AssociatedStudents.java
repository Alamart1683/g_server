package g_server.g_server.application.entity.users;

import g_server.g_server.application.entity.project.ProjectTheme;

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

    @Column(name = "theme")
    private int theme;

    @Column(name = "is_accepted")
    private boolean Accepted;

    @ManyToOne
    @JoinColumn(name = "scientific_advisor", referencedColumnName = "id", insertable = false, updatable = false)
    private Users advisorUser;

    @ManyToOne
    @JoinColumn(name = "student", referencedColumnName = "id", insertable = false, updatable = false)
    private Users studentUser;

    @ManyToOne
    @JoinColumn(name = "theme", referencedColumnName = "id", insertable = false, updatable = false)
    private ProjectTheme projectTheme;

    public AssociatedStudents() { }

    public AssociatedStudents(int scientificAdvisor, int student, int theme, boolean Accepted) {
        this.scientificAdvisor = scientificAdvisor;
        this.student = student;
        this.theme = theme;
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

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public boolean isAccepted() {
        return Accepted;
    }

    public void setAccepted(boolean accepted) {
        Accepted = accepted;
    }

    public ProjectTheme getProjectTheme() {
        return projectTheme;
    }

    public void setProjectTheme(ProjectTheme projectTheme) {
        this.projectTheme = projectTheme;
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