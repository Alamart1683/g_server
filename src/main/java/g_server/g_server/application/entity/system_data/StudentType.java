package g_server.g_server.application.entity.system_data;

import javax.persistence.*;

@Entity
@Table(name = "student_type")
public class StudentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "type")
    private String studentType;

    public StudentType() { }

    public StudentType(String type) {
        this.studentType = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentType() {
        return studentType;
    }

    public void setStudentType(String type) {
        this.studentType = type;
    }
}
