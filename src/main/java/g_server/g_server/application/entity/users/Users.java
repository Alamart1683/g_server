package g_server.g_server.application.entity.users;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users")
public class Users implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "second_name")
    private String second_name;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "is_accepted_mail_sending")
    private boolean sendMailAccepted;

    @Column(name = "is_confirmed")
    private boolean isConfirmed;

    @Column(name = "password_change_code")
    private Integer passwordChangeCode;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Set<Roles> roles;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    private StudentData studentData;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    private ScientificAdvisorData scientificAdvisorData;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "userID", insertable = false, updatable = false)
    private RefreshToken refreshToken;

    public Users() { }

    public Users(String email, String name, String surname, String second_name,
                 String password, String phone, boolean sendMailAccepted, boolean isConfirmed) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.second_name = second_name;
        this.password = password;
        this.phone = phone;
        this.sendMailAccepted = sendMailAccepted;
        this.isConfirmed = isConfirmed;
    }

    public Boolean determineMailSendingAccepted(String mailSendingAccepted) {
        if (mailSendingAccepted != null) {
            switch (mailSendingAccepted) {
                case "true":
                    return true;
                case "false":
                    return false;
                default:
                    return null;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSecond_name() {
        return second_name;
    }

    public void setSecond_name(String second_name) {
        this.second_name = second_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<Roles> getRoles() {
        return roles;
    }

    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
    }

    public StudentData getStudentData() {
        return studentData;
    }

    public void setStudentData(StudentData studentData) {
        this.studentData = studentData;
    }

    public ScientificAdvisorData getScientificAdvisorData() {
        return scientificAdvisorData;
    }

    public void setScientificAdvisorData(ScientificAdvisorData scientificAdvisorData) {
        this.scientificAdvisorData = scientificAdvisorData;
    }

    public boolean isSendMailAccepted() {
        return sendMailAccepted;
    }

    public void setSendMailAccepted(boolean sendMailAccepted) {
        this.sendMailAccepted = sendMailAccepted;
    }

    public Integer getPasswordChangeCode() {
        return passwordChangeCode;
    }

    public void setPasswordChangeCode(Integer passwordChangeCode) {
        this.passwordChangeCode = passwordChangeCode;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }
}