package g_server.g_server.application.query.response;

public class StudentAdvisorView {
    private int systemAdvisorID;
    private String advsiorFio;
    private String advisorEmail;
    private String advisorPhone;

    public StudentAdvisorView(int systemAdvisorID, String advsiorFio, String advisorEmail, String advisorPhone) {
        this.systemAdvisorID = systemAdvisorID;
        this.advsiorFio = advsiorFio;
        this.advisorEmail = advisorEmail;
        this.advisorPhone = advisorPhone;
    }

    public StudentAdvisorView() { }

    public int getSystemAdvisorID() {
        return systemAdvisorID;
    }

    public void setSystemAdvisorID(int systemAdvisorID) {
        this.systemAdvisorID = systemAdvisorID;
    }

    public String getAdvsiorFio() {
        return advsiorFio;
    }

    public void setAdvsiorFio(String advsiorFio) {
        this.advsiorFio = advsiorFio;
    }

    public String getAdvisorEmail() {
        return advisorEmail;
    }

    public void setAdvisorEmail(String advisorEmail) {
        this.advisorEmail = advisorEmail;
    }

    public String getAdvisorPhone() {
        return advisorPhone;
    }

    public void setAdvisorPhone(String advisorPhone) {
        this.advisorPhone = advisorPhone;
    }
}
