package g_server.g_server.application.entity.messanger.components;

public class MessageSendForm {
    private String messageTheme;
    private String messageText;
    private String receivers;

    public MessageSendForm() {
    }

    public MessageSendForm(String messageTheme, String messageText, String receivers) {
        this.messageTheme = messageTheme;
        this.messageText = messageText;
        this.receivers = receivers;
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
}
