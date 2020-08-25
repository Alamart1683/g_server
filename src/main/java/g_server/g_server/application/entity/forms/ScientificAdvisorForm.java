package g_server.g_server.application.entity.forms;

import g_server.g_server.application.entity.users.Users;

public class ScientificAdvisorForm extends Users {
    private String cathedra;
    private String passwordConfirm;

    public ScientificAdvisorForm() { }

    public ScientificAdvisorForm(String cathedra, String email, String name, String surname,
            String second_name, String password, String confirm_password, String phone) {
        this.cathedra = cathedra;
        this.setEmail(email);
        this.setPassword(password);
        this.passwordConfirm = confirm_password;
        this.setPasswordConfirm(confirm_password);
        this.setName(name);
        this.setSurname(surname);
        this.setSecond_name(second_name);
        this.setPhone(phone);
    }

    public Users ScientificAdvisorFormToUsers() {
        Users user = new Users( this.getEmail(), this.getName(), this.getSurname(),
                this.getSecond_name(), this.getPassword(), this.getPhone()
        );
        return user;
    }

    public String getCathedra() {
        return cathedra;
    }

    public void setCathedra(String cathedra) {
        this.cathedra = cathedra;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
