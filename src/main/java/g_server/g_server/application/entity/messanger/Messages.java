package g_server.g_server.application.entity.messanger;

import g_server.g_server.application.entity.users.Users;
import javax.persistence.*;

@Entity
@Table(name = "messages")
public class Messages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sender")
    private String sender;

    @Column(name = "send_date")
    private String sendDate;

    @Column(name = "message_theme")
    private String messageTheme;

    @Column(name = "message_text")
    private String messageText;

    @Column(name = "receivers")
    private String receivers;

    @Column(name = "is_receiver_red")
    private String isRedString;

    @Column(name = "is_delete")
    private String isDelete;

    @ManyToOne
    @JoinColumn(name = "sender", insertable = false, updatable = false, referencedColumnName = "id")
    private Users users;

    public Messages() { }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getMessageTheme() {
        return messageTheme;
    }

    public void setMessageTheme(String messageTheme) {
        this.messageTheme = messageTheme;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getReceivers() {
        return receivers;
    }

    public void setReceivers(String receivers) {
        this.receivers = receivers;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public String getIsRedString() {
        return isRedString;
    }

    public void setIsRedString(String isRedString) {
        this.isRedString = isRedString;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }
}
