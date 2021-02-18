package g_server.g_server.application.entity.messanger.components;

public class Sender {
    private int senderId;
    private String fio;
    private String email;
    private String status;

    public Sender() { this.status = "sender"; }

    public Sender(int senderId, String fio, String email) {
        this.senderId = senderId;
        this.fio = fio;
        this.email = email;
        this.status = "sender";
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() { return status; }
}
