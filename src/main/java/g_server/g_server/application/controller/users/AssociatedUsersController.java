package g_server.g_server.application.controller.users;

import g_server.g_server.application.query.request.AutomaticRegistrationForm;
import g_server.g_server.application.query.response.*;
import g_server.g_server.application.service.users.AssociatedStudentsService;
import g_server.g_server.application.service.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.springframework.util.StringUtils.hasText;

/* Контроллер студенческих заявок и взаимодействия научных
руководителей с ними */
@RestController
public class AssociatedUsersController {
    public static final String AUTHORIZATION = "Authorization";
    private AssociatedStudentsService associatedStudentsService;
    private UsersService usersService;

    @Autowired
    public void setAssociatedStudentsService(AssociatedStudentsService associatedStudentsService) {
        this.associatedStudentsService = associatedStudentsService;
    }

    @Autowired
    public void setUsersService(UsersService usersService) {
        this.usersService = usersService;
    }

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
    @GetMapping("/scientific_advisor/all")
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

    // Получить список всех активных студентов или по группам
    @GetMapping("/scientific_advisor/student/active/for/")
    public List<AssociatedStudentView> getActiveStudentsByKey(
            @RequestParam String key
    ) {
        return associatedStudentsService.getAllActiveStudents(key);
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

    // Получить студенту инфу о себе
    @GetMapping("/student/get/about/me")
    public AssociatedStudentView getInfoAboutStudent(HttpServletRequest httpServletRequest) {
        return associatedStudentsService.getInfoAboutStudent(getTokenFromRequest(httpServletRequest));
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

    // Получить даты этапов для всех пользователей
    @GetMapping("/date/all")
    public StagesDatesView getStagesDates() {
        return usersService.getStagesDates();
    }

    // Получить список всех ассоциированных студентов для завкафедры
    @GetMapping("/head_of_cathedra/get/associated_students")
    public List<AssociatedStudentViewWithAdvisor> getAssociatedStudentsWithAdvisor() {
        return associatedStudentsService.getAllAssociatedStudentsWithAdvisors();
    }

    // Изменить научного руководителя
    @PostMapping("/head_of_cathedra/change/advisor")
    public String changeAdvisor(
            @RequestParam Integer studentID,
            @RequestParam Integer advisorID
    ) {
        return associatedStudentsService.changeStudentsAdvisor(studentID, advisorID);
    }

    // Одобрить назначение студента
    @PostMapping("/head_of_cathedra/only/confirm/student")
    public String confirmStudent(
            @RequestParam Integer studentID
    ) {
        return associatedStudentsService.confirmStudentsAdvisor(studentID);
    }

    // Назначить студентам научных руководителей из файла
    @PostMapping("/admin/association/student/automatic")
    public String studentAutomaticAssociation(@ModelAttribute("automaticStudentForm")
            @Validated AutomaticRegistrationForm automaticRegistrationForm) throws IOException {
        return associatedStudentsService.studentAutomaticAssociation(automaticRegistrationForm);
    }

    // Студент получает тему вкр
    @GetMapping("/student/get/vkr_theme")
    public VkrThemeView studentGetVkrTheme(HttpServletRequest httpServletRequest) {
        return associatedStudentsService.studentGetVkrTheme(getTokenFromRequest(httpServletRequest));
    }

    // Студент меняет тему вкр
    @PostMapping("/student/set/vkr_theme")
    public String studentSetVkrTheme(
            HttpServletRequest httpServletRequest,
            @RequestParam String newTheme
    ) {
        return associatedStudentsService.studentEditingVkrTheme(getTokenFromRequest(httpServletRequest), newTheme);
    }

    // Научный преподаватель меняет тему вкр студенту
    @PostMapping("/scientific_advisor/edit/student/vkr_theme")
    public String advisorEditStudentVkrTheme(
            HttpServletRequest httpServletRequest,
            @RequestParam String newTheme,
            @RequestParam Integer studentID
    ) {
        return associatedStudentsService.advisorEditingStudentTheme(getTokenFromRequest(httpServletRequest), newTheme, studentID);
    }

    // Научный преподаватель утверждает тему вкр студенту
    @PostMapping("/scientific_advisor/approve/student/vkr_theme")
    public String advisorApproveStudentVkrTheme(
        HttpServletRequest httpServletRequest,
        @RequestParam Integer studentID,
        @RequestParam boolean approve
    ) {
        return associatedStudentsService.advisorApprovedStudentTheme(getTokenFromRequest(httpServletRequest), studentID, approve);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}