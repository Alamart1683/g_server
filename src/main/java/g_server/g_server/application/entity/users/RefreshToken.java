package g_server.g_server.application.entity.users;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    private int id;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "issue")
    private long issue;

    @Column(name = "expire")
    private long expire;

    public RefreshToken() { }

    public RefreshToken(int id, String refreshToken, long issue, long expire) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.issue = issue;
        this.expire = expire;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
