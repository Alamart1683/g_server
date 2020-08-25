package g_server.g_server.application.entity.forms;

public class AuthorizationForm {
    private String email;
    private String password;

    AuthorizationForm() { }

    AuthorizationForm(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
