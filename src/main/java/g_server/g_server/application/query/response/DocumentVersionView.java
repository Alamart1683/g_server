package g_server.g_server.application.query.response;

import g_server.g_server.application.entity.documents.DocumentVersion;

// Представление списка версий документов
public class DocumentVersionView {
    private int systemEditorID;
    private int systemDocumentID;
    private String systemRussianDateTime;
    private String editorName;
    private String versionEditionDate;
    private String versionDescription;

    public DocumentVersionView(DocumentVersion documentVersion) {
        this.systemEditorID = documentVersion.getEditor();
        this.systemDocumentID = documentVersion.getDocument();
        this.systemRussianDateTime = getSystemRussianDateTime(documentVersion.getEditionDate());
        this.editorName = documentVersion.getUser().getSurname() + " " + documentVersion.getUser().getName() + " " +
                documentVersion.getUser().getSecond_name();
        this.versionEditionDate = getRussianDateTime(documentVersion.getEditionDate());
        this.versionDescription = documentVersion.getEdition_description();
    }

    // Красиво отобразить дату загрузки новой верссии документа
    public String getRussianDateTime(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        String russianDate = day + "." + month + "." + year;
        String russianDateTime = russianDate + date.substring(10);
        return russianDateTime;
    }

    // Системный вариант даты для скачивания версии файла (чтобы не конвертить на фронте)
    public String getSystemRussianDateTime(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        String russianDate = day + "." + month + "." + year + ".";
        String systemRussianDateTime = russianDate + date.substring(11).replaceAll(":", ".");
        return systemRussianDateTime;
    }

    public int getSystemEditorID() {
        return systemEditorID;
    }

    public void setSystemEditorID(int systemEditorID) {
        this.systemEditorID = systemEditorID;
    }

    public int getSystemDocumentID() {
        return systemDocumentID;
    }

    public void setSystemDocumentID(int systemDocumentID) {
        this.systemDocumentID = systemDocumentID;
    }

    public String getEditorName() {
        return editorName;
    }

    public void setEditorName(String editorName) {
        this.editorName = editorName;
    }

    public String getVersionEditionDate() {
        return versionEditionDate;
    }

    public void setVersionEditionDate(String versionEditionDate) {
        this.versionEditionDate = versionEditionDate;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

    public String getSystemRussianDateTime() {
        return systemRussianDateTime;
    }

    public void setSystemRussianDateTime(String systemRussianDateTime) {
        this.systemRussianDateTime = systemRussianDateTime;
    }
}