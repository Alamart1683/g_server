package g_server.g_server.application.entity.project;

import g_server.g_server.application.entity.users.StudentData;

import javax.persistence.*;

@Entity
@Table(name = "occupied_students")
public class OccupiedStudents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "student_id")
    private int studentID;

    @Column(name = "project_id")
    private int projectID;

    @OneToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id", insertable = false, updatable = false)
    private StudentData studentData;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Project project;

    public OccupiedStudents() { }

    public OccupiedStudents(int studentID, int projectID) {
        this.studentID = studentID;
        this.projectID = projectID;
    }

    public int getId() {
        return id;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public StudentData getStudentData() {
        return studentData;
    }

    public void setStudentData(StudentData studentData) {
        this.studentData = studentData;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}