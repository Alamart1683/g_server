package g_server.g_server.application.controller.users;

import g_server.g_server.application.entity.view.*;
import g_server.g_server.application.service.users.AssociatedStudentsService;
import g_server.g_server.application.service.users.UsersService;
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

    @Autowired
    private UsersService usersService;

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

    // Добавить студента в проект
    @PostMapping("/scientific_advisor/project/student/add/")
    public List<String> addStudentToProject(
            @RequestParam Integer studentID,
            @RequestParam Integer projectID,
            HttpServletRequest httpServletRequest
    ) {
        return associatedStudentsService.addStudentToProject(getTokenFromRequest(httpServletRequest),
                studentID, projectID);
    }

    // Удалить студента из проекта
    @DeleteMapping("/scientific_advisor/project/student/delete/")
    public List<String> deleteStudentFromProject(
            @RequestParam Integer studentID,
            @RequestParam Integer projectID,
            HttpServletRequest httpServletRequest
    ) {
        return associatedStudentsService.deleteStudentFromProject(getTokenFromRequest(httpServletRequest),
                studentID, projectID);
    }

    // Получить список активных студентов
    @GetMapping("/scientific_advisor/student/active")
    public List<AssociatedStudentView> getActiveStudents(HttpServletRequest httpServletRequest) {
        return associatedStudentsService.getActiveStudents(getTokenFromRequest(httpServletRequest));
    }

    // Получить список активных студентов без проекта
    @GetMapping("/scientific_advisor/student/active/without_project")
    public List<AssociatedStudentViewWithoutProject> getActiveStudentsWithoutProject(
            HttpServletRequest httpServletRequest) {
        return associatedStudentsService.getStudentsWithoutProject(getTokenFromRequest(httpServletRequest));
    }

    // Отозвать заявку научному руководителю от лица студента
    @DeleteMapping("/student/dismiss/request")
    public List<String> revokeStudentRequest(HttpServletRequest httpServletRequest) {
        return associatedStudentsService.revokeRequestByStudent(getTokenFromRequest(httpServletRequest));
    }

    // Отказаться от научного руководства студента от лица научного руководителя
    @DeleteMapping("/scientific_advisor/dismiss/student")
    public List<String> dismissStudentByAdvisor(@RequestParam Integer systemID, HttpServletRequest httpServletRequest) {
        return associatedStudentsService.dismissStudentByAdvisor(getTokenFromRequest(httpServletRequest), systemID);
    }

    // Послать научному руководителю о желании студента сменить его
    @PostMapping("/student/dismiss/advisor")
    public List<String> dismissAdvisorByStudent(HttpServletRequest httpServletRequest) {
        return associatedStudentsService.dismissAdvisorByStudent(getTokenFromRequest(httpServletRequest));
    }

    // Проверить имеет ли студент научного руководителя
    @GetMapping("/student/check/advisor")
    public List<String> isStudentHasAdvisor(HttpServletRequest httpServletRequest) {
        return associatedStudentsService.isStudentHasAdvisor(getTokenFromRequest(httpServletRequest));
    }

    @GetMapping("/student/project")
    public List<ProjectView> getProject(HttpServletRequest httpServletRequest) {
        return associatedStudentsService.getProjectView(getTokenFromRequest(httpServletRequest));
    }

    @GetMapping("/scientific_advisor/projects")
    public List<ProjectView> getProjects(HttpServletRequest httpServletRequest) {
        return associatedStudentsService.getProjectView(getTokenFromRequest(httpServletRequest));
    }

    @GetMapping("/scientific_advisor/projects/names")
    public List<String> getProjectNames(HttpServletRequest httpServletRequest) {
        return associatedStudentsService.getAdvisorProjectNames(getTokenFromRequest(httpServletRequest));
    }

    @GetMapping("/student/request/NIR/task/data")
    public TaskDataViewWithMessage getStudentTaskData(HttpServletRequest httpServletRequest) {
        return associatedStudentsService.getNirTaskDataView(getTokenFromRequest(httpServletRequest));
    }

    // Получить данные научного руководителя по айди студента
    @GetMapping("/student/advisor/data")
    public StudentAdvisorView getStudentAdvisorData(HttpServletRequest httpServletRequest) {
        return usersService.getAdvisorDataByStudentToken(getTokenFromRequest(httpServletRequest));
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}