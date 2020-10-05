package g_server.g_server.application.entity.forms;

public class AuthorizationResponseForm {
    private String accessToken;
    private String expirationDate;
    private String userRole;
    private String fio;
    private String message;

    public AuthorizationResponseForm(
            String accessToken, String expirationDate, String userRole, String fio, String message) {
        this.accessToken = accessToken;
        this.expirationDate = expirationDate;
        this.userRole = userRole;
        this.fio = fio;
        this.message = message;
    }

    public AuthorizationResponseForm(String accessToken, String expirationDate) {
        this.accessToken = accessToken;
        this.expirationDate = expirationDate;
    }

    public AuthorizationResponseForm(String message) {
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }
}
