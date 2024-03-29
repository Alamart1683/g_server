package g_server.g_server.application.query.request;

import g_server.g_server.application.entity.users.Users;

public class ScientificAdvisorForm extends Users {
    private String cathedra;
    private String mailSendingAccepted;
    private int places;
    private String position;

    public ScientificAdvisorForm() { }

    public ScientificAdvisorForm(String cathedra, String email, String name, String surname,
            String second_name, String phone, String mailSendingAccepted, int places, String position) {
        this.cathedra = cathedra;
        this.setEmail(email);
        this.setName(name);
        this.setSurname(surname);
        this.setSecond_name(second_name);
        this.setPhone(phone);
        this.mailSendingAccepted = mailSendingAccepted;
        this.places = places;
        this.position = position;
    }

    public Users ScientificAdvisorFormToUsers() {
        Users user = new Users( this.getEmail(), this.getName(), this.getSurname(),
                this.getSecond_name(), this.getPassword(), this.getPhone(),
                determineMailSendingAccepted(mailSendingAccepted), true
        );
        return user;
    }

    public String getCathedra() {
        return cathedra;
    }

    public void setCathedra(String cathedra) {
        this.cathedra = cathedra;
    }

    public String getMailSendingAccepted() {
        return mailSendingAccepted;
    }

    public void setMailSendingAccepted(String mailSendingAccepted) {
        this.mailSendingAccepted = mailSendingAccepted;
    }

    public int getPlaces() {
        return places;
    }

    public void setPlaces(int places) {
        this.places = places;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
