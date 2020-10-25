package g_server.g_server.application.controller.documents;

import g_server.g_server.application.entity.view.DocumentView;
import g_server.g_server.application.entity.view.TaskDocumentVersionView;
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
    public List<DocumentView> GetDocumentView(HttpServletRequest httpServletRequest) {
        return documentViewService.getUserDocumentView(getTokenFromRequest(httpServletRequest));
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

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}