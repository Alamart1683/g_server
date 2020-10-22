package g_server.g_server.application.entity.users;

import javax.persistence.*;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "userID")
    private int userID;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "issue")
    private long issue;

    @Column(name = "expire")
    private long expire;

    public RefreshToken() { }

    public RefreshToken(int userID, String refreshToken, long issue, long expire) {
        this.userID = userID;
        this.refreshToken = refreshToken;
        this.issue = issue;
        this.expire = expire;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getIssue() {
        return issue;
    }

    public void setIssue(int issue) {
        this.issue = issue;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public int getUserID() { return userID; }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getId() {
        return id;
    }
}
