package g_server.g_server.application.query.request;

import g_server.g_server.application.entity.users.Users;

public class AdminForm extends Users {
    private String mailSendingAccepted;

    public AdminForm() { }

    public AdminForm(String email, String name, String surname, String second_name, String phone, String mailSendingAccepted) {
        this.setEmail(email);
        this.setName(name);
        this.setSurname(surname);
        this.setSecond_name(second_name);
        this.setPhone(phone);
        this.mailSendingAccepted = mailSendingAccepted;
    }

    public Users AdminFormToUsers() {
        Users user = new Users(this.getEmail(), this.getName(), this.getSurname(),
                this.getSecond_name(), this.getPassword(), this.getPhone(),
                determineMailSendingAccepted(mailSendingAccepted), true
        );
        return user;
    }

    public String getMailSendingAccepted() {
        return mailSendingAccepted;
    }

    public void setMailSendingAccepted(String mailSendingAccepted) {
        this.mailSendingAccepted = mailSendingAccepted;
    }
}