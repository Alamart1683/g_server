package g_server.g_server.application.controller.documents;

import g_server.g_server.application.entity.forms.DocumentForm;
import g_server.g_server.application.entity.forms.DocumentOrderForm;
import g_server.g_server.application.entity.forms.DocumentVersionForm;
import g_server.g_server.application.service.documents.DocumentUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping("/scientific_advisor/document/upload")
    public List<String> UploadDocument(
            @ModelAttribute("documentForm") @Validated DocumentForm documentForm,
            HttpServletRequest httpServletRequest ) {
        documentForm.setToken(getTokenFromRequest(httpServletRequest));
        return documentUploadService.uploadDocument(documentForm);
    }

    @PostMapping("/scientific_advisor/document/upload/version")
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
    public List<String> UploadReport (
            @ModelAttribute("documentForm") @Validated DocumentForm documentForm,
            HttpServletRequest httpServletRequest
    ) {
        documentForm.setToken(getTokenFromRequest(httpServletRequest));
        return documentUploadService.uploadStudentReport(documentForm);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}