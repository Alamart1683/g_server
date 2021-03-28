package g_server.g_server.application.query.request;

public class AuthorizationResponseForm {
    private String accessToken;
    private long accessIssueDate;
    private long accessExpireDate;
    private String refreshToken;
    private long refreshIssueDate;
    private long refreshExpireDate;
    private String userRole;
    private String fio;
    private String email;
    private String message;

    public AuthorizationResponseForm(
            String accessToken, long accessIssueDate, long accessExpireDate,
            String refreshToken, long refreshIssueDate, long refreshExpireDate,
            String userRole, String fio, String email, String message) {
        this.accessToken = accessToken;
        this.accessIssueDate = accessIssueDate;
        this.accessExpireDate = accessExpireDate;
        this.refreshToken = refreshToken;
        this.refreshIssueDate = refreshIssueDate;
        this.refreshExpireDate = refreshExpireDate;
        this.userRole = userRole;
        this.fio = fio;
        this.email = email;
        this.message = message;
    }

    public AuthorizationResponseForm(
            String accessToken, long accessIssueDate, long accessExpireDate,
            String refreshToken, long refreshIssueDate, long refreshExpireDate
    ) {
        this.accessToken = accessToken;
        this.accessIssueDate = accessIssueDate;
        this.accessExpireDate = accessExpireDate;
        this.refreshToken = refreshToken;
        this.refreshIssueDate = refreshIssueDate;
        this.refreshExpireDate = refreshExpireDate;
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

    public long getAccessIssueDate() {
        return accessIssueDate;
    }

    public void setAccessIssueDate(long accessIssueDate) {
        this.accessIssueDate = accessIssueDate;
    }

    public long getAccessExpireDate() {
        return accessExpireDate;
    }

    public void setAccessExpireDate(long accessExpireDate) {
        this.accessExpireDate = accessExpireDate;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getRefreshIssueDate() {
        return refreshIssueDate;
    }

    public void setRefreshIssueDate(long refreshIssueDate) {
        this.refreshIssueDate = refreshIssueDate;
    }

    public long getRefreshExpireDate() {
        return refreshExpireDate;
    }

    public void setRefreshExpireDate(long refreshExpireDate) {
        this.refreshExpireDate = refreshExpireDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
