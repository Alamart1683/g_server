package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.documents.Document;
import java.util.List;

public class AdvisorsTemplateView extends DocumentView {
    private Integer systemAreaID;
    private String area;

    public AdvisorsTemplateView(Document document, List<DocumentVersionView>
            documentVersions, int systemAreaID, String area) {
        super(document, documentVersions);
        this.systemAreaID = systemAreaID;
        this.area = area;
    }

    public Integer getSystemAreaID() {
        return systemAreaID;
    }

    public void setSystemAreaID(int systemAreaID) {
        this.systemAreaID = systemAreaID;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
