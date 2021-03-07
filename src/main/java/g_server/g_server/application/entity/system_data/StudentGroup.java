package g_server.g_server.application.entity.system_data;

import javax.persistence.*;
import java.util.Set;

@NamedNativeQuery(
        name = "getStudentGroupByStudentGroup",
        query = "select student_group from student_group where student_group = ?",
        resultClass = StudentGroup.class
)

@Entity
@Table(name = "student_group")
public class StudentGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "student_group")
    private String studentGroup;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "economy_consultants_student_group",
            joinColumns = @JoinColumn(
                    name = "groupID", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "consultantID", referencedColumnName = "consultantID"))
    private Set<EconomyConsultants> consultants;

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

    public Set<EconomyConsultants> getConsultants() {
        return consultants;
    }

    public void setConsultants(Set<EconomyConsultants> consultants) {
        this.consultants = consultants;
    }
}
