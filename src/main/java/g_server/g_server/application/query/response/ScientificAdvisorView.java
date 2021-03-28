package g_server.g_server.application.query.response;

import java.util.List;

// Представление научного руководителя для отправки заявки студенту
// TODO Данное представление сделано из рассчёта на то, что темы у всех научных руководителей общие
public class ScientificAdvisorView {
    private int systemID;
    private String advisorName;
    private String advisorCathedra;
    private String advisorEmail;
    private String advisorPhone;
    private int totalAdvisorPlaces;
    private int freeAdvisorPlaces;
    private int occupiedAdvisorPlaces;
    private List<String> advisorProjectAreas;
    private boolean isHasFreePlaces;

   public ScientificAdvisorView() { }

   public ScientificAdvisorView(int systemID, String advisorSurname, String advisorName,
          String advisorSecondName, String advisorCathedra, String advisorEmail, String advisorPhone,
          int totalAdvisorPlaces, int freeAdvisorPlaces, int occupiedAdvisorPlaces,
          List<String> advisorProjectAreas) {
       this.systemID = systemID;
       this.advisorName = advisorSurname + " " + advisorName + " " + advisorSecondName;
       this.advisorCathedra = advisorCathedra;
       this.advisorEmail = advisorEmail;
       this.advisorPhone = advisorPhone;
       this.totalAdvisorPlaces = totalAdvisorPlaces;
       this.freeAdvisorPlaces = freeAdvisorPlaces;
       this.occupiedAdvisorPlaces = occupiedAdvisorPlaces;
       this.advisorProjectAreas = advisorProjectAreas;
       this.isHasFreePlaces = setHasFreePlaces();
   }

    public int getSystemID() {
        return systemID;
    }

    public void setSystemID(int systemID) {
        this.systemID = systemID;
    }

    public String getAdvisorName() {
        return advisorName;
    }

    public void setAdvisorName(String advisorName) {
        this.advisorName = advisorName;
    }

    public String getAdvisorCathedra() {
        return advisorCathedra;
    }

    public void setAdvisorCathedra(String advisorCathedra) {
        this.advisorCathedra = advisorCathedra;
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

    public int getTotalAdvisorPlaces() {
        return totalAdvisorPlaces;
    }

    public void setTotalAdvisorPlaces(int totalAdvisorPlaces) {
        this.totalAdvisorPlaces = totalAdvisorPlaces;
    }

    public int getFreeAdvisorPlaces() {
        return freeAdvisorPlaces;
    }

    public void setFreeAdvisorPlaces(int freeAdvisorPlaces) {
        this.freeAdvisorPlaces = freeAdvisorPlaces;
    }

    public int getOccupiedAdvisorPlaces() {
        return occupiedAdvisorPlaces;
    }

    public void setOccupiedAdvisorPlaces(int occupiedAdvisorPlaces) {
        this.occupiedAdvisorPlaces = occupiedAdvisorPlaces;
    }

    public List<String> getAdvisorProjectAreas() {
        return advisorProjectAreas;
    }

    public void setAdvisorProjectAreas(List<String> advisorProjectAreas) {
        this.advisorProjectAreas = advisorProjectAreas;
    }

    public boolean setHasFreePlaces() {
       if (this.freeAdvisorPlaces > 0) {
           return true;
       }
       else {
           return false;
       }
    }

    public boolean isHasFreePlaces() {
        return isHasFreePlaces;
    }
}
