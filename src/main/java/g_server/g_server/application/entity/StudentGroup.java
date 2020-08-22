package g_server.g_server.application.entity;

import javax.persistence.*;

@Entity
@Table(name = "student_group")
public class StudentGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "student_group")
    private String studentGroup;

    public StudentGroup() { }

    public StudentGroup(String student_group) {
        this.studentGroup = student_group;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentGroup() {
        return studentGroup;
    }

    public void setStudentGroup(String student_group) {
        this.studentGroup = student_group;
    }
}
