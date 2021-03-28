package g_server.g_server.application.query.response;

import g_server.g_server.application.entity.documents.Document;

import java.util.List;

public class AdvisorsStudentDocumentView {
    private int systemCreatorID;
    private String documentName;
    private String documentDownloader;
    private String documentDownloadDate;
    private String documentType;
    private String documentKind;
    private String documentDescription;
    private List<TaskDocumentVersionView> taskVersions;
    private List<ReportVersionDocumentView> reportVersions;
    private List<DocumentVersionView> documentVersionViews;
    private List<VkrStuffVersionView> vkrStuffVersionViews;

    public AdvisorsStudentDocumentView(Document document, List<TaskDocumentVersionView> taskVersions,
             List<ReportVersionDocumentView> reportVersions, List<DocumentVersionView> documentVersionViews,
             List<VkrStuffVersionView> vkrStuffVersionViews) {
        this.systemCreatorID = document.getCreator();
        this.documentName = document.getName();
        this.documentDownloader = document.getUser().getSurname() + " " + document.getUser().getName() + " " +
                document.getUser().getSecond_name();
        this.documentDownloadDate = getRussianDate(document.getCreation_date());
        this.documentType = document.getDocumentType().getType();
        this.documentKind = document.getDocumentKind().getKind();
        this.documentDescription = document.getDescription();
        this.taskVersions = taskVersions;
        this.reportVersions = reportVersions;
        this.documentVersionViews = documentVersionViews;
        this.vkrStuffVersionViews = vkrStuffVersionViews;
    }

    public String getRussianDate(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        return day + "." + month + "." + year;
    }

    public List<VkrStuffVersionView> getVkrStuffVersionViews() {
        return vkrStuffVersionViews;
    }

    public void setVkrStuffVersionViews(List<VkrStuffVersionView> vkrStuffVersionViews) {
        this.vkrStuffVersionViews = vkrStuffVersionViews;
    }

    public int getSystemCreatorID() {
        return systemCreatorID;
    }

    public void setSystemCreatorID(int systemCreatorID) {
        this.systemCreatorID = systemCreatorID;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentDownloader() {
        return documentDownloader;
    }

    public void setDocumentDownloader(String documentDownloader) {
        this.documentDownloader = documentDownloader;
    }

    public String getDocumentDownloadDate() {
        return documentDownloadDate;
    }

    public void setDocumentDownloadDate(String documentDownloadDate) {
        this.documentDownloadDate = documentDownloadDate;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentKind() {
        return documentKind;
    }

    public void setDocumentKind(String documentKind) {
        this.documentKind = documentKind;
    }

    public String getDocumentDescription() {
        return documentDescription;
    }

    public void setDocumentDescription(String documentDescription) {
        this.documentDescription = documentDescription;
    }

    public List<TaskDocumentVersionView> getTaskVersions() {
        return taskVersions;
    }

    public void setTaskVersions(List<TaskDocumentVersionView> taskVersions) {
        this.taskVersions = taskVersions;
    }

    public List<ReportVersionDocumentView> getReportVersions() {
        return reportVersions;
    }

    public void setReportVersions(List<ReportVersionDocumentView> reportVersions) {
        this.reportVersions = reportVersions;
    }

    public List<DocumentVersionView> getDocumentVersionViews() {
        return documentVersionViews;
    }

    public void setDocumentVersionViews(List<DocumentVersionView> documentVersionViews) {
        this.documentVersionViews = documentVersionViews;
    }
}
