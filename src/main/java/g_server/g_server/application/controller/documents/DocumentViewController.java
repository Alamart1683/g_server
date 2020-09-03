package g_server.g_server.application.controller.documents;

import g_server.g_server.application.entity.view.DocumentView;
import g_server.g_server.application.service.documents.DocumentViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}