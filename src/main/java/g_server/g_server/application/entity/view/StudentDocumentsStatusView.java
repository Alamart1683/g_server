package g_server.g_server.application.entity.view;

public class StudentDocumentsStatusView {
    private int nirTaskStatus;
    private int nirReportStatus;
    private int ppppuipdTaskStatus;
    private int ppppuipdReportStatus;
    private int ppTaskStatus;
    private int ppReportStatus;
    private int vkrTask;
    private int vkrReport;

    public StudentDocumentsStatusView() { }

    public StudentDocumentsStatusView(int nirTaskStatus, int nirReportStatus, int ppppuipdTaskStatus,
                                      int ppppuipdReportStatus, int ppTaskStatus, int ppReportStatus, int vkrTask, int vkrReport) {
        this.nirTaskStatus = nirTaskStatus;
        this.nirReportStatus = nirReportStatus;
        this.ppppuipdTaskStatus = ppppuipdTaskStatus;
        this.ppppuipdReportStatus = ppppuipdReportStatus;
        this.ppTaskStatus = ppTaskStatus;
        this.ppReportStatus = ppReportStatus;
        this.vkrTask = vkrTask;
        this.vkrReport = vkrReport;
    }

    public int getNirTaskStatus() {
        return nirTaskStatus;
    }

    public void setNirTaskStatus(int nirTaskStatus) {
        this.nirTaskStatus = nirTaskStatus;
    }

    public int getNirReportStatus() {
        return nirReportStatus;
    }

    public void setNirReportStatus(int nirReportStatus) {
        this.nirReportStatus = nirReportStatus;
    }

    public int getPpppuipdTaskStatus() {
        return ppppuipdTaskStatus;
    }

    public void setPpppuipdTaskStatus(int ppppuipdTaskStatus) {
        this.ppppuipdTaskStatus = ppppuipdTaskStatus;
    }

    public int getPpppuipdReportStatus() {
        return ppppuipdReportStatus;
    }

    public void setPpppuipdReportStatus(int ppppuipdReportStatus) {
        this.ppppuipdReportStatus = ppppuipdReportStatus;
    }

    public int getPpTaskStatus() {
        return ppTaskStatus;
    }

    public void setPpTaskStatus(int ppTaskStatus) {
        this.ppTaskStatus = ppTaskStatus;
    }

    public int getPpReportStatus() {
        return ppReportStatus;
    }

    public void setPpReportStatus(int ppReportStatus) {
        this.ppReportStatus = ppReportStatus;
    }

    public int getVkrTask() {
        return vkrTask;
    }

    public void setVkrTask(int vkrTask) {
        this.vkrTask = vkrTask;
    }

    public int getVkrReport() {
        return vkrReport;
    }

    public void setVkrReport(int vkrReport) {
        this.vkrReport = vkrReport;
    }
}
