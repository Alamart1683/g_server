package g_server.g_server.application.controller.users;

import g_server.g_server.application.entity.forms.AssociatedStudentForm;
import g_server.g_server.application.service.users.AssociatedStudentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
            @RequestParam String theme,
            HttpServletRequest httpServletRequest) {
        return associatedStudentsService.sendRequestForScientificAdvisor(getTokenFromRequest(httpServletRequest),
                scientificAdvisorId, theme);
    }

    @GetMapping("/scientific_advisor/request/all/active")
    public List<AssociatedStudentForm> findAllActiveRequest(
            HttpServletRequest httpServletRequest
    ) {
        return associatedStudentsService.getActiveRequests(getTokenFromRequest(httpServletRequest));
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}