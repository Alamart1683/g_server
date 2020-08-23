package g_server.g_server.application.entity;

import org.springframework.security.core.GrantedAuthority;
import javax.persistence.*;

@Entity
@Table(name = "user_role")
public class UserRole implements GrantedAuthority {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "is_student")
    private boolean is_student;

    @Column(name = "is_scientific_advisor")
    private boolean is_scientific_advisor;

    @Column(name = "is_admin")
    private boolean is_admin;

    @Column(name = "is_head_of_cathedra")
    private boolean is_head_of_cathedra;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id", referencedColumnName = "id")
    private Users users;

    public UserRole() { }

    public UserRole(int id, boolean is_student, boolean is_scientific_advisor,
                    boolean is_admin, boolean is_head_of_cathedra) {
        this.id = id;
        this.is_student = is_student;
        this.is_scientific_advisor = is_scientific_advisor;
        this.is_admin = is_admin;
        this.is_head_of_cathedra = is_head_of_cathedra;
    }

    @Override
    public String getAuthority() {
        if (Is_student())
            return "ROLE_STUDENT";
        else if (Is_scientific_advisor())
            return "ROLE_SCIENTIFIC_ADVISOR";
        else if (Is_head_of_cathedra())
            return "ROLE_HEAD_OF_CATHEDRA";
        else if (Is_admin())
            return "ROLE_ADMIN";
        else
            return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean Is_student() {
        return is_student;
    }

    public void setIs_student(boolean is_student) {
        this.is_student = is_student;
    }

    public boolean Is_scientific_advisor() {
        return is_scientific_advisor;
    }

    public void setIs_scientific_advisor(boolean is_scientific_advisor) {
        this.is_scientific_advisor = is_scientific_advisor;
    }

    public boolean Is_admin() {
        return is_admin;
    }

    public void setIs_admin(boolean is_admin) {
        this.is_admin = is_admin;
    }

    public boolean Is_head_of_cathedra() {
        return is_head_of_cathedra;
    }

    public void setIs_head_of_cathedra(boolean is_head_of_cathedra) {
        this.is_head_of_cathedra = is_head_of_cathedra;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }
}