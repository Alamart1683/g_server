package g_server.g_server.application.controller.users;

import g_server.g_server.application.service.users.AssociatedStudentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static org.springframework.util.StringUtils.hasText;

/* Контроллер студенческих заявок и взаимодействия научных
руководителей с ними */
@RestController
public class AssociatedUsersController {
    public static final String AUTHORIZATION = "Authorization";

    @Autowired
    private AssociatedStudentsService associatedStudentsService;

    @PostMapping("/student/request/for_scientific_advisor/")
    public List<String> sendRequestForScientificAdvisor(
            @RequestParam int scientificAdvisorId,
            HttpServletRequest httpServletRequest) {
        return associatedStudentsService.sendRequestForScientificAdvisor(getTokenFromRequest(httpServletRequest),
                scientificAdvisorId);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
