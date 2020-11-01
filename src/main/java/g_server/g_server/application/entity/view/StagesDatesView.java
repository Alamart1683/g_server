package g_server.g_server.application.entity.view;

// Класс представления дат этапов учебного процесса
public class StagesDatesView {
    private String nirStart;
    private String nirEnd;
    private String ppppuipdStart;
    private String ppppuipdEnd;
    private String ppStart;
    private String ppEnd;
    private String vkrStart;
    private String vkrEnd;
    private String currentDate;

    public StagesDatesView() { }

    public StagesDatesView(String nirStart, String nirEnd, String ppppuipdStart,
                           String ppppuipdEnd, String ppStart, String ppEnd, String vkrStart,
                           String vkrEnd, String currentDate) {
        this.nirStart = nirStart;
        this.nirEnd = nirEnd;
        this.ppppuipdStart = ppppuipdStart;
        this.ppppuipdEnd = ppppuipdEnd;
        this.ppStart = ppStart;
        this.ppEnd = ppEnd;
        this.vkrStart = vkrStart;
        this.vkrEnd = vkrEnd;
        this.currentDate = currentDate;
    }

    public String getNirStart() {
        return nirStart;
    }

    public void setNirStart(String nirStart) {
        this.nirStart = nirStart;
    }

    public String getNirEnd() {
        return nirEnd;
    }

    public void setNirEnd(String nirEnd) {
        this.nirEnd = nirEnd;
    }

    public String getPpppuipdStart() {
        return ppppuipdStart;
    }

    public void setPpppuipdStart(String ppppuipdStart) {
        this.ppppuipdStart = ppppuipdStart;
    }

    public String getPpppuipdEnd() {
        return ppppuipdEnd;
    }

    public void setPpppuipdEnd(String ppppuipdEnd) {
        this.ppppuipdEnd = ppppuipdEnd;
    }

    public String getPpStart() {
        return ppStart;
    }

    public void setPpStart(String ppStart) {
        this.ppStart = ppStart;
    }

    public String getPpEnd() {
        return ppEnd;
    }

    public void setPpEnd(String ppEnd) {
        this.ppEnd = ppEnd;
    }

    public String getVkrStart() {
        return vkrStart;
    }

    public void setVkrStart(String vkrStart) {
        this.vkrStart = vkrStart;
    }

    public String getVkrEnd() {
        return vkrEnd;
    }

    public void setVkrEnd(String vkrEnd) {
        this.vkrEnd = vkrEnd;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}