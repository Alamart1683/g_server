package g_server.g_server.application.controller.documents;

import g_server.g_server.application.query.request.*;
import g_server.g_server.application.service.documents.DocumentUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static org.springframework.util.StringUtils.hasText;

@RestController
public class DocumentUploadController {
    @Autowired
    private DocumentUploadService documentUploadService;

    public static final String AUTHORIZATION = "Authorization";

    @PostMapping("/document/upload")
    public List<String> UploadDocument(
            @ModelAttribute("documentForm") @Validated DocumentForm documentForm,
            HttpServletRequest httpServletRequest ) {
        documentForm.setToken(getTokenFromRequest(httpServletRequest));
        return documentUploadService.uploadDocument(documentForm);
    }

    @PostMapping("/document/upload/version")
    public List<String> UploadDocumentVersion(
            @ModelAttribute("documentVersionForm") @Validated DocumentVersionForm documentVersionForm,
            HttpServletRequest httpServletRequest) {
        documentVersionForm.setToken(getTokenFromRequest(httpServletRequest));
        return documentUploadService.uploadDocumentVersion(documentVersionForm);
    }

    @PostMapping("/head_of_cathedra/document/order/upload")
    public List<String> UploadOrder (
            @ModelAttribute("documentOrderForm") @Validated DocumentOrderForm documentOrderForm,
            HttpServletRequest httpServletRequest
    ) {
        documentOrderForm.setToken(getTokenFromRequest(httpServletRequest));
        return documentUploadService.uploadDocumentOrder(documentOrderForm);
    }

    @PostMapping("/student/document/report/upload")
    public List<String> studentUploadReport (
            @ModelAttribute("documentForm") @Validated DocumentFormReport documentForm,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        documentForm.setToken(getTokenFromRequest(httpServletRequest));
        return documentUploadService.uploadStudentReport(documentForm);
    }

    @PostMapping("/scientific_advisor/document/report/upload/version")
    public List<String> advisorUploadReportVersion (
            @ModelAttribute("documentForm") @Validated AdvisorReportDocumentForm documentForm,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        documentForm.setToken(getTokenFromRequest(httpServletRequest));
        return documentUploadService.uploadAdvisorStudentReportVersion(documentForm);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}