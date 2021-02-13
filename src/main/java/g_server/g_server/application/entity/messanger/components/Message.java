package g_server.g_server.application.entity.messanger.components;

import java.util.List;

public class Message {
    private int messageId;
    private Sender sender;
    private String messageDate;
    private String messageTheme;
    private String messageText;
    private List<Receiver> receivers;

    public Message() { }

    public Message(int messageId, Sender sender, String messageDate, String messageTheme,
                   String messageText, List<Receiver> receivers) {
        this.messageId = messageId;
        this.sender = sender;
        this.messageDate = messageDate;
        this.messageTheme = messageTheme;
        this.messageText = messageText;
        this.receivers = receivers;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
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

    public List<Receiver> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<Receiver> receivers) {
        this.receivers = receivers;
    }
}
