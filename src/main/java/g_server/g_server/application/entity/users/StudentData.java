package g_server.g_server.application.entity.users;

import g_server.g_server.application.entity.system_data.Cathedras;
import g_server.g_server.application.entity.system_data.StudentGroup;
import g_server.g_server.application.entity.system_data.StudentType;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "student_data")
public class StudentData {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "student_group")
    private int student_group;

    @Column(name = "cathedra")
    private int cathedra;

    @Column(name = "type")
    private int type;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    private Roles roles;

    @ManyToOne
    @JoinColumn(name = "student_group", referencedColumnName = "id", insertable = false, updatable = false)
    private StudentGroup studentGroup;

    @ManyToOne
    @JoinColumn(name = "cathedra", referencedColumnName = "id", insertable = false, updatable = false)
    private Cathedras cathedras;

    @ManyToOne
    @JoinColumn(name = "type", referencedColumnName = "id", insertable = false, updatable = false)
    private StudentType studentType;

    public StudentData() { }

    public StudentData(int id, int student_group, int cathedra, int type) {
        this.id = id;
        this.student_group = student_group;
        this.cathedra = cathedra;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudent_group() {
        return student_group;
    }

    public void setStudent_group(int student_group) {
        this.student_group = student_group;
    }

    public int getCathedra() {
        return cathedra;
    }

    public void setCathedra(int cathedra) {
        this.cathedra = cathedra;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public StudentGroup getStudentGroup() {
        return studentGroup;
    }

    public void setStudentGroup(StudentGroup studentGroup) {
        this.studentGroup = studentGroup;
    }

    public Cathedras getCathedras() {
        return cathedras;
    }

    public void setCathedras(Cathedras cathedras) {
        this.cathedras = cathedras;
    }

    public StudentType getStudentType() {
        return studentType;
    }

    public void setStudentType(StudentType studentType) {
        this.studentType = studentType;
    }
}