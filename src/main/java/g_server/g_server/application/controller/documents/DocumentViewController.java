package g_server.g_server.application.controller.documents;

import g_server.g_server.application.entity.view.*;
import g_server.g_server.application.service.documents.DocumentViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static org.springframework.util.StringUtils.hasText;

@RestController
public class DocumentViewController {
    public static final String AUTHORIZATION = "Authorization";

    @Autowired
    private DocumentViewService documentViewService;

    @GetMapping("/document/view")
    public List<DocumentView> getDocumentView(HttpServletRequest httpServletRequest) {
        return documentViewService.getUserDocumentView(getTokenFromRequest(httpServletRequest));
    }

    @GetMapping("/document/view/orders")
    public List<DocumentViewOrder> getOrdersView(HttpServletRequest httpServletRequest) {
        return documentViewService.getOrders(getTokenFromRequest(httpServletRequest));
    }

    @GetMapping("/document/view/templates")
    public List<DocumentViewTemplate> getTemplatesView(HttpServletRequest httpServletRequest) {
        return documentViewService.getTemplates(getTokenFromRequest(httpServletRequest));
    }

    @GetMapping("/document/view/templates/advisor")
    public List<AdvisorsTemplateView> getAdvisorsDownloadedTemplatesView(HttpServletRequest httpServletRequest) {
        return documentViewService.getAdvisorsLoadedTaskAndReportsTemplates(getTokenFromRequest(httpServletRequest));
    }

    @GetMapping("/document/view/templates/student")
    public List<AdvisorsTemplateView> getAdvisorsDownloadedTemplatesViewForStudent(HttpServletRequest httpServletRequest) {
        return documentViewService.getAdvisorsLoadedTaskAndReportsTemplatesForStudent(getTokenFromRequest(httpServletRequest));
    }

    @GetMapping("/scientific_advisor/document/view/students")
    public List<AdvisorsStudentDocumentView> getStudentsDocumentView(HttpServletRequest httpServletRequest) {
        return documentViewService.getAdvisorStudentsDocuments(getTokenFromRequest(httpServletRequest));
    }

    @GetMapping("/student/document/task/view")
    private List<TaskDocumentVersionView> getStudentTaskVersions(
        HttpServletRequest httpServletRequest,
        @RequestParam String taskType
    ) {
        return documentViewService.getStudentTaskVersions(getTokenFromRequest(httpServletRequest), taskType);
    }

    @GetMapping("/scientific_advisor/document/task/view")
    private List<TaskDocumentVersionView> getAdvisorStudentTaskVersions(
            HttpServletRequest httpServletRequest,
            @RequestParam String taskType,
            @RequestParam Integer studentID
    ) {
        return documentViewService.getAdvisorStudentTaskVersions(
                getTokenFromRequest(httpServletRequest), taskType, studentID);
    }

    @GetMapping("/student/document/report/view")
    private List<ReportVersionDocumentView> getStudentReportVersions(
            HttpServletRequest httpServletRequest,
            @RequestParam String taskType
    ) {
        return documentViewService.getStudentReportVersions(getTokenFromRequest(httpServletRequest), taskType);
    }

    @GetMapping("/scientific_advisor/document/report/view")
    private List<ReportVersionDocumentView> getAdvisorStudentReportVersions(
            HttpServletRequest httpServletRequest,
            @RequestParam String taskType,
            @RequestParam Integer studentID
    ) {
        return documentViewService.getAdvisorStudentReportVersions(
                getTokenFromRequest(httpServletRequest), taskType, studentID);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}