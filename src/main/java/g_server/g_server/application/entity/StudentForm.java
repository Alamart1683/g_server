package g_server.g_server.application.entity;

public class StudentForm extends Users {
    private String student_group;
    private String cathedra;
    private String student_type;
    private String passwordConfirm;
    private int registrationCode;
    private int registrationCodeConfirm;

    public StudentForm() { }

    public StudentForm(String student_group, String cathedra, String student_type,
                String email, String name, String surname, String second_name,
                String password, String confirm_password, String phone,
                int registrationCode, int registrationCodeConfirm) {
        this.cathedra = cathedra;
        this.student_group = student_group;
        this.student_type = student_type;
        this.setEmail(email);
        this.setPassword(password);
        this.passwordConfirm = confirm_password;
        this.setPasswordConfirm(confirm_password);
        this.setName(name);
        this.setSurname(surname);
        this.setSecond_name(second_name);
        this.setPhone(phone);
        this.registrationCode = registrationCode;
        this.registrationCodeConfirm = registrationCodeConfirm;
    }

    public Users StudentFormToUsers() {
        Users user = new Users( this.getEmail(), this.getName(), this.getSurname(),
                this.getSecond_name(), this.getPassword(), this.getPhone()
        );
        return user;
    }

    public String getStudent_group() {
        return student_group;
    }

    public void setStudent_group(String student_group) {
        this.student_group = student_group;
    }

    public String getCathedra() {
        return cathedra;
    }

    public void setCathedra(String cathedra) {
        this.cathedra = cathedra;
    }

    public String getStudent_type() {
        return student_type;
    }

    public void setStudent_type(String student_type) {
        this.student_type = student_type;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public int getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(int registrationCode) {
        this.registrationCode = registrationCode;
    }

    public int getRegistrationCodeConfirm() {
        return registrationCodeConfirm;
    }

    public void setRegistrationCodeConfirm(int registrationCodeConfirm) {
        this.registrationCodeConfirm = registrationCodeConfirm;
    }
}
