package g_server.g_server.application.entity.users;

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

    @Column(name = "is_accepted")
    private boolean isAccepted;

    @ManyToOne
    @JoinColumn(name = "scientific_advisor", referencedColumnName = "id", insertable = false, updatable = false)
    private Users advisorUser;

    @ManyToOne
    @JoinColumn(name = "student", referencedColumnName = "id", insertable = false, updatable = false)
    private Users studentUser;

    public AssociatedStudents() { }

    public AssociatedStudents(int scientificAdvisor, int student, boolean isAccepted) {
        this.scientificAdvisor = scientificAdvisor;
        this.student = student;
        this.isAccepted = isAccepted;
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

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}