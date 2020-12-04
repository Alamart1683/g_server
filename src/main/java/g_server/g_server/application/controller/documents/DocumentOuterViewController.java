package g_server.g_server.application.controller.documents;

import g_server.g_server.application.service.documents.DocumentOuterViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.net.MalformedURLException;

import static org.springframework.util.StringUtils.hasText;

@RestController
public class DocumentOuterViewController {
    public static final String AUTHORIZATION = "Authorization";

    @Autowired
    private DocumentOuterViewService documentOuterViewService;

    @PostMapping("/document/create/outer/view")
    public String createDocumentUrlSource(
            @RequestParam Integer versionID
    ) throws MalformedURLException {
        return documentOuterViewService.createDocumentUrlResource(versionID);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
