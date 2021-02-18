package g_server.g_server.application.entity.messanger.components;

public class Receiver {
    private int receiverId;
    private String fio;
    private String email;
    private String status;

    public Receiver() { this.status = "receiver"; }

    public Receiver(int receiverId, String fio, String email) {
        this.receiverId = receiverId;
        this.fio = fio;
        this.email = email;
        this.status = "receiver";
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
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

    public String getStatus() {
        return status;
    }
}
