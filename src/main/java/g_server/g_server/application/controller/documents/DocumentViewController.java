package g_server.g_server.application.controller.documents;

import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import static org.springframework.util.StringUtils.hasText;

@RestController
public class DocumentViewController {
    public static final String AUTHORIZATION = "Authorization";

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}