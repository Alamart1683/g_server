package g_server.g_server.application.entity;

import javax.persistence.*;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id", referencedColumnName = "id")
    private UserRole userRole;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "student_group", referencedColumnName = "id", insertable = false, updatable = false)
    private StudentGroup studentGroup;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cathedra", referencedColumnName = "id", insertable = false, updatable = false)
    private Cathedras cathedras;

    @ManyToOne(cascade = CascadeType.ALL)
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

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
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
