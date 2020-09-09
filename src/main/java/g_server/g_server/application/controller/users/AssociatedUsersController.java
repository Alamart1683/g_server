package g_server.g_server.application.controller.users;

import g_server.g_server.application.entity.view.AssociatedRequestView;
import g_server.g_server.application.entity.view.ScientificAdvisorView;
import g_server.g_server.application.service.users.AssociatedStudentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
    public List<AssociatedRequestView> findAllActiveRequest(
            HttpServletRequest httpServletRequest
    ) {
        return associatedStudentsService.getActiveRequests(getTokenFromRequest(httpServletRequest));
    }

    // Обработка заявки с помощью интерфейса сайта
    @PostMapping("/scientific_advisor/request/handle/")
    public List<String> handleStudentRequest(
            @RequestParam int requestID,
            @RequestParam boolean accept,
            HttpServletRequest httpServletRequest
    ) {
        List<String> messageList = new ArrayList<>();
        Integer scientificAdvisorId = associatedStudentsService.getUserId(getTokenFromRequest(httpServletRequest));
        if (scientificAdvisorId == null) {
            messageList.add("Ошибка валидации токена");
            return messageList;
        }
        return associatedStudentsService.handleRequest(scientificAdvisorId, requestID, accept);
    }

    // Обработка заявки через ссылки в письме
    @GetMapping("/mail/request/handle/{token}")
    public String handleStudentRequestByURL(@PathVariable String token) {
        List<String> params = associatedStudentsService.decodeRequestToken(token);
        if (params == null) {
            return "Срок действия ссылки подтверждения истек или она указана неверно";
        }
        Integer advisorID = Integer.parseInt(params.get(0));
        Integer requestID = associatedStudentsService.getRequestId(params.get(0), params.get(1));
        Boolean accept = associatedStudentsService.getAccept(params.get(2));
        if (accept == null || requestID == null || advisorID == null) {
            return "Срок действия ссылки подтверждения истек или она указана неверно";
        }
        return associatedStudentsService.handleRequest(advisorID, requestID, accept).get(0);
    }

    // Получить представление научных руководителей для отправки заявки
    @GetMapping("/student/scientific_advisor/all")
    public List<ScientificAdvisorView> getScientificAdvisorView() {
        return associatedStudentsService.getScientificAdvisorViewList();
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}