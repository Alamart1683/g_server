package g_server.g_server.application.service.users;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.project.OccupiedStudents;
import g_server.g_server.application.entity.project.Project;
import g_server.g_server.application.entity.project.ProjectTheme;
import g_server.g_server.application.entity.users.ScientificAdvisorData;
import g_server.g_server.application.entity.users.StudentData;
import g_server.g_server.application.entity.view.AssociatedRequestView;
import g_server.g_server.application.entity.users.AssociatedStudents;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.entity.view.AssociatedStudentView;
import g_server.g_server.application.entity.view.AssociatedStudentViewWithoutProject;
import g_server.g_server.application.entity.view.ScientificAdvisorView;
import g_server.g_server.application.repository.project.OccupiedStudentsRepository;
import g_server.g_server.application.repository.project.ProjectRepository;
import g_server.g_server.application.repository.project.ProjectThemeRepository;
import g_server.g_server.application.repository.users.AssociatedStudentsRepository;
import g_server.g_server.application.repository.users.StudentDataRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import g_server.g_server.application.repository.users.UsersRolesRepository;
import g_server.g_server.application.service.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

// Сервис взаимодействия студентов и научных руководителей
@Service
public class AssociatedStudentsService {
    @Value("$(api.url)")
    private String apiUrl;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UsersRolesRepository usersRolesRepository;

    @Autowired
    private AssociatedStudentsRepository associatedStudentsRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private ProjectThemeRepository projectThemeRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private ScientificAdvisorDataService scientificAdvisorDataService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StudentDataRepository studentDataRepository;

    @Autowired
    private OccupiedStudentsRepository occupiedStudentsRepository;

    // Отправить заявку научному руководителю от имени студента на научное руководство
    public List<String> sendRequestForScientificAdvisor(String token,
        Integer scientificAdvisorId, String theme) {
        List<String> messageList = new ArrayList<>();
        Integer student_id = getUserId(token);
        // Проверка существования айди студента и научного рукводителя
        if (student_id == null) {
            messageList.add("Студент не найден, отправить заявку невозможно");
        }
        if (scientificAdvisorId == null) {
            messageList.add("Ошибка: получен null вместо int в качестве параметра");
        }
        // Проверка, корректно ли указана тема ВКР
        if (projectThemeRepository.findByTheme(theme) == null) {
            messageList.add("Ошибка: желаемая тема ВКР указана некорректно");
        }
        // Формирование и отправка заявки
        if (messageList.size() == 0) {
            Users scientificAdvisor = usersRepository.findById(scientificAdvisorId).get();
            Users student = usersRepository.findById(student_id).get();
            // Проверка существования студента и научного рукводителя
            if (scientificAdvisor == null) {
                messageList.add("Не удается найти научного руководителя");
            }
            if (student == null) {
                messageList.add("Не удается найти ваш аккаунт");
            }
            // Проверка существования их ролей
            Integer studentRole = usersRolesRepository.findUsersRolesByUserId(student_id).getRoleId();
            Integer scientificAdvisorRole = usersRolesRepository.findUsersRolesByUserId(scientificAdvisorId).getRoleId();
            if (studentRole == null || scientificAdvisorRole == null) {
                messageList.add("Ошибка определения ролей пользователей");
            }
            if (studentRole != 1 || scientificAdvisorRole != 2) {
                messageList.add("Ошибка соответствия ролей пользователей");
            }
            // Проверка на то, одну ли заявку отправляет студент
            AssociatedStudents existController =
                    associatedStudentsRepository.findByScientificAdvisorAndStudent(scientificAdvisorId, student_id);
            if (existController != null) {
                messageList.add("Одновременно позволено подавать только одну заявку для одного научного руководителя");
            }
            // Проверка на то, что у научного руководтеля еще есть свободные места
            if (associatedStudentsRepository.findByScientificAdvisor(scientificAdvisor.getId()).size()
                    >= scientificAdvisor.getScientificAdvisorData().getPlaces()) {
                messageList.add("У данного научного руководителя не осталось свободных мест");
            }
            // Если не возникло ошибок, добавим заявку
            if (messageList.size() == 0) {
                // Сформируем заявку
                AssociatedStudents associatedStudent = new AssociatedStudents(scientificAdvisorId, student_id,
                        projectThemeRepository.findByTheme(theme).getId(), false);
                // Сохраним заявку
                associatedStudentsRepository.save(associatedStudent);
                // Сгенерируем её уникальный идентификатор
                String acceptRequestIdentifier = scientificAdvisorId.toString() + "."
                        + student_id.toString() + ".true";
                String declineRequestIdentifier = scientificAdvisorId.toString() + "."
                        + student_id.toString() + ".false";
                String acceptToken = jwtProvider.getStudentRequestHandleToken(acceptRequestIdentifier);
                String declineToken = jwtProvider.getStudentRequestHandleToken(declineRequestIdentifier);
                String acceptURL = apiUrl + "mail/request/handle/" +  acceptToken;
                String declineURL = apiUrl + "mail/request/handle/" + declineToken;
                // Проверим, активна ли у научного руководителя почтовая рассылка
                if (scientificAdvisor.isSendMailAccepted()) {
                    // Отправим ему письмо с уведомлением
                    mailService.sendRequestForScientificAdvisorMail(student, scientificAdvisor, theme,
                            acceptURL, declineURL);
                    messageList.add("Ваш потенциальный научный руководитель" +
                            " получил уведомление по почте о вашей заявке");
                }
                // Проверим, активна ли у студента почтовая рассылка
                if (student.isSendMailAccepted()) {
                    mailService.sendMailStudentAboutHisRequestSending(student);
                }
                messageList.add("Заяка успешно оформлена");
            }
        }
        return messageList;
    }

    // Показать список активных заявок данного научного руководителя
    public List<AssociatedRequestView> getActiveRequests(String token) {
        Integer scientificAdvisorId = getUserId(token);
        List<AssociatedRequestView> activeRequests = new ArrayList<>();
        Users scientificAdvisor = usersRepository.findById(scientificAdvisorId).get();
        if (scientificAdvisor != null) {
            List<AssociatedStudents> associatedStudentsRaw =
                    associatedStudentsRepository.findByScientificAdvisor(scientificAdvisorId);
            List<AssociatedStudents> associatedStudents = new ArrayList<>();
            for (AssociatedStudents associatedStudentRaw: associatedStudentsRaw) {
                if (!associatedStudentRaw.isAccepted()) {
                    associatedStudents.add(associatedStudentRaw);
                }
            }
            for (AssociatedStudents associatedStudent: associatedStudents) {
                Users currentStudent = usersRepository.findById(associatedStudent.getStudent()).get();
                String currentTheme = associatedStudent.getProjectTheme().getTheme();
                AssociatedRequestView associatedStudentForm = new AssociatedRequestView(currentStudent,
                        currentTheme, associatedStudent.getId());
                activeRequests.add(associatedStudentForm);
            }
            return activeRequests;
        }
        return null;
    }

    // Принять заявку или отклонить заявку
    public List<String> handleRequest(Integer scientificAdvisorId, Integer requestId, boolean accept) {
        List<String> messageList = new ArrayList<>();
        Users scientificAdvisor = usersRepository.findById(scientificAdvisorId).get();
        AssociatedStudents associatedStudent = associatedStudentsRepository.findById(requestId).get();
        Users student = usersRepository.findById(associatedStudent.getStudent()).get();
        if (associatedStudent == null) {
            messageList.add("Не удается найти заявку");
        }
        if (scientificAdvisor == null) {
            messageList.add("Ошибка авторизации");
        }
        if (student == null) {
            messageList.add("Не удается найти студента");
        }
        if (requestId == null) {
            messageList.add("Передан некорректный айди заявки");
        }
        if (associatedStudent.isAccepted()) {
            messageList.add("Срок действия ссылки подтверждения истек или она указана неверно");
        }
        if (associatedStudent.getScientificAdvisor() != scientificAdvisorId) {
            messageList.add("Вы не можете принять заявку студента другому научному руководителю");
        }
        // После проведения всех проверок примем заявку
        if (messageList.size() == 0) {
            if (accept) {
                associatedStudent.setAccepted(accept);
                associatedStudentsRepository.save(associatedStudent);
                // Сообщим студенту о том, что его заявка была принята
                if (student.isSendMailAccepted()) {
                    mailService.sendMailStudentAboutHandledRequest(student, scientificAdvisor, "принята");
                }
            }
            else {
                associatedStudentsRepository.deleteById(requestId);
                // Сообщим студенту о том, что его заявка была отклонена
                if (student.isSendMailAccepted()) {
                    mailService.sendMailStudentAboutHandledRequest(student, scientificAdvisor, "отклонена");
                }
            }
            messageList.add("Заявка успешно обработана");
        }
        return messageList;
    }

    // Показать список студентов данного научного руководителя
    public List<AssociatedStudentView> getActiveStudents(String token) {
        Integer scientificAdvisorId = getUserId(token);
        List<AssociatedStudentView> activeStudents = new ArrayList<>();
        Users scientificAdvisor = usersRepository.findById(scientificAdvisorId).get();
        List<AssociatedStudents> associatedStudents = new ArrayList<>();
        if (scientificAdvisor != null) {
            List<AssociatedStudents> associatedStudentsRaw =
                    associatedStudentsRepository.findByScientificAdvisor(scientificAdvisorId);
            for (AssociatedStudents associatedStudentRaw: associatedStudentsRaw) {
                if (associatedStudentRaw.isAccepted()) {
                    associatedStudents.add(associatedStudentRaw);
                }
            }
            for (AssociatedStudents associatedStudent: associatedStudents) {
                Users currentStudent = usersRepository.findById(associatedStudent.getStudent()).get();
                Integer studentID = associatedStudent.getStudent();
                String projectTheme = "Проект не назначен";
                OccupiedStudents occupiedStudent = occupiedStudentsRepository.findByStudentID(studentID);
                Project project = null;
                if (occupiedStudent != null) {
                    try { project = projectRepository.findById(occupiedStudent.getProjectID()).get(); }
                    catch (NoSuchElementException noSuchElementException) { }
                }
                if (project != null) {
                    projectTheme = project.getName();
                }
                String currentTheme = associatedStudent.getProjectTheme().getTheme();
                AssociatedStudentView activeStudentForm = new AssociatedStudentView(currentStudent,
                        currentTheme, associatedStudent.getId(), projectTheme);
                activeStudents.add(activeStudentForm);
            }
            return activeStudents;
        }
        return null;
    }

    // Показать список студентов данного научного рукводителя, которые не участвуют в проектах
    public List<AssociatedStudentViewWithoutProject> getStudentsWithoutProject(String token) {
        Integer scientificAdvisorId = getUserId(token);
        List<AssociatedStudentView> activeStudents = new ArrayList<>();
        Users scientificAdvisor = usersRepository.findById(scientificAdvisorId).get();
        List<AssociatedStudents> associatedStudents = new ArrayList<>();
        List<AssociatedStudentViewWithoutProject> associatedStudentViewWithoutProjects = new ArrayList<>();
        if (scientificAdvisor != null) {
            List<AssociatedStudents> associatedStudentsRaw =
                    associatedStudentsRepository.findByScientificAdvisor(scientificAdvisorId);
            for (AssociatedStudents associatedStudentRaw: associatedStudentsRaw) {
                if (associatedStudentRaw.isAccepted()) {
                    associatedStudents.add(associatedStudentRaw);
                }
            }
            for (AssociatedStudents associatedStudent: associatedStudents) {
                Users currentStudent = usersRepository.findById(associatedStudent.getStudent()).get();
                Integer studentID = associatedStudent.getStudent();
                OccupiedStudents occupiedStudent = occupiedStudentsRepository.findByStudentID(studentID);
                Project project = null;
                if (occupiedStudent != null) {
                    try { project = projectRepository.findById(occupiedStudent.getProjectID()).get(); }
                    catch (NoSuchElementException noSuchElementException) { }
                }
                if (project == null) {
                    String currentTheme = associatedStudent.getProjectTheme().getTheme();
                    AssociatedStudentViewWithoutProject associatedStudentViewWithoutProject
                            = new AssociatedStudentViewWithoutProject(currentStudent, currentTheme, associatedStudent.getId());
                    associatedStudentViewWithoutProjects.add(associatedStudentViewWithoutProject);
                }
            }
            return associatedStudentViewWithoutProjects;
        }
        return null;
    }

    // Отозвать заявку от лица студента
    public List<String> revokeRequestByStudent(String token) {
        List<String> messageList = new ArrayList<>();
        Integer studentID = getUserId(token);
        if (studentID != null) {
            messageList.add("ID студента не найдено");
        }
        AssociatedStudents associatedStudent = associatedStudentsRepository.findByStudent(studentID);
        if (associatedStudent == null) {
            messageList.add("Не удается найти заявку");
        }
        if (associatedStudent.isAccepted()) {
            messageList.add("Вы можете отозвать только не принятую заявку");
        }
        if (messageList.size() == 0) {
            associatedStudentsRepository.deleteById(associatedStudent.getId());
            // TODO Возможно сделать уведомление по почте
            messageList.add("Заявка успешно отозвана");
        }
        return messageList;
    }

    // Откзаться от научного руководителя от лица студента
    public List<String> dismissAdvisorByStudent(String token) {
        List<String> messageList = new ArrayList<>();
        Integer studentID = getUserId(token);
        if (studentID != null) {
            messageList.add("ID студента не найдено");
        }
        AssociatedStudents associatedStudent = associatedStudentsRepository.findByStudent(studentID);
        if (associatedStudent == null) {
            messageList.add("Не удается найти заявку");
        }
        if (!associatedStudent.isAccepted()) {
            messageList.add("Ваша заявка до сих пор не рассмотрена");
        }
        if (messageList.size() == 0) {
            // TODO Послать по почте на подтверждение НР письмо о том, что студент хочет сменить руководителя
            // TODO Сообщить по почте студенту о том, что это произошло
            messageList.add("Уведомление о вашем стремлении отправлено научному руководителю");
        }
        return messageList;
    }

    // TODO Проверить, имеет ли студент научного руководителя

    // TODO Отказаться от студента для научного руководителя

    // Добавить студента в проект
    public List<String> addStudentToProject(String token, Integer studentID, Integer projectID) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("ID научного руководителя не найден");
        }
        if (studentID == null) {
            messageList.add("ID студента не найден");
        }
        if (projectID == null) {
            messageList.add("ID проекта не найдено");
        }
        Project project = null;
        try { project = projectRepository.findById(projectID).get(); }
        catch (NoSuchElementException noSuchElementException) { messageList.add("Проект не найден"); }
        StudentData student = null;
        try { student = studentDataRepository.findById(studentID).get(); }
        catch (NoSuchElementException noSuchElementException) { messageList.add("Студент не найден"); }
        if (messageList.size() == 0) {
            AssociatedStudents associatedStudent =
                    associatedStudentsRepository.findByScientificAdvisorAndStudent(advisorID, studentID);
            if (associatedStudent == null) {
                messageList.add("Данный студент не ассоциирован с данным научным руководителем");
            }
            if (advisorID != project.getScientificAdvisorID()) {
                messageList.add("Вы не можете добавлять студентов в чужой проект");
            }
            if (messageList.size() == 0) {
                if (associatedStudent.isAccepted()) {
                    List<OccupiedStudents> integrityController =
                            occupiedStudentsRepository.findAllByStudentID(studentID);
                    if (integrityController.size() == 0) {
                        OccupiedStudents occupiedStudents = new OccupiedStudents(studentID, projectID);
                        occupiedStudentsRepository.save(occupiedStudents);
                        messageList.add("Студент был успешно добавлен в проект");
                        // TODO Сделать почтовое уведомление об этом
                    }
                    else {
                        messageList.add("Данный студент уже стостоит в проекте");
                    }

                }
                else {
                    messageList.add("Вы не можете добавить данного студента в проект," +
                            " так как вы не приняли его заявку");
                }
            }
        }
        return messageList;
    }

    // Удалить студента из проекта
    public List<String> deleteStudentFromProject(String token, Integer studentID, Integer projectID) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("ID научного руководителя не найден");
        }
        if (studentID == null) {
            messageList.add("ID студента не найден");
        }
        if (projectID == null) {
            messageList.add("ID проекта не найдено");
        }
        Project project = null;
        try { project = projectRepository.findById(projectID).get(); }
        catch (NoSuchElementException noSuchElementException) { messageList.add("Проект не найден"); }
        StudentData student = null;
        try { student = studentDataRepository.findById(studentID).get(); }
        catch (NoSuchElementException noSuchElementException) { messageList.add("Студент не найден"); }
        if (messageList.size() == 0) {
            OccupiedStudents occupiedStudent =
                    occupiedStudentsRepository.findAllByStudentIDAndProjectID(studentID, projectID);
            if (occupiedStudent == null) {
                messageList.add("Не удается найти данного студента участником данного проекта");
            }
            else if (project.getScientificAdvisorID() != advisorID) {
                messageList.add("Вы не моежет удалять студента не из своего проекта");
            }
            else {
                occupiedStudentsRepository.deleteById(occupiedStudent.getId());
                messageList.add("Студент был успешно удален из данного проекта");
            }
        }
        return messageList;
    }

    // TODO Возможно стоит сделать триггер при принятии последней заявки НР чтобы те, что не были
    // TODO им приняты, автоматически отклонились и для этого заюзать письмо

    // Сформировать представление научных руководителей для подачи студентом заявки
    // TODO Не знаю, стоит ли отображать преподов с уже закончившимися местами
    // TODO Можно отображать их типа приглушенными, ввел для этого специальный флаг
    public List<ScientificAdvisorView> getScientificAdvisorViewList() {
        List<ScientificAdvisorData> advisorList = scientificAdvisorDataService.findAll();
        List<ScientificAdvisorView> advisorViewList = new ArrayList<>();
        // TODO Список обших на данный момент тем проектов
        List<ProjectTheme> projectThemes = projectThemeRepository.findAll();
        for (ScientificAdvisorData currentAdvisorData: advisorList) {
            Users currentAdvisor = usersRepository.findById(currentAdvisorData.getId()).get();
            List<AssociatedStudents> associatedStudents = new ArrayList<>();
            List<AssociatedStudents> associatedStudentsRaw =
                    associatedStudentsRepository.findByScientificAdvisor(currentAdvisorData.getId());
            for (AssociatedStudents associatedStudentRaw: associatedStudentsRaw) {
                if (associatedStudentRaw.isAccepted()) {
                    associatedStudents.add(associatedStudentRaw);
                }
            }
            Integer freePlaces = currentAdvisorData.getPlaces() - associatedStudents.size();
            Integer occupiedPlaces = associatedStudents.size();
            advisorViewList.add(
                    new ScientificAdvisorView(
                            currentAdvisorData.getId(),
                            currentAdvisor.getSurname(),
                            currentAdvisor.getName(),
                            currentAdvisor.getSecond_name(),
                            currentAdvisorData.getCathedras().getCathedraName(),
                            currentAdvisor.getEmail(),
                            currentAdvisor.getPhone(),
                            currentAdvisorData.getPlaces(),
                            freePlaces,
                            occupiedPlaces,
                            projectThemes
                    )
            );
        }
        return advisorViewList;
    }

    // Получить айди из токена
    public Integer getUserId(String token) {
        // Проверка токена
        if (token == null) {
            return null;
        }
        if (token.equals("")) {
            return null;
        }
        String email = jwtProvider.getEmailFromToken(token);
        Users user = usersRepository.findByEmail(email);
        if (user != null) {
            return user.getId();
        }
        else {
            return null;
        }
    }

    // Метод декодирования токена идентификатора студентческой заявки
    public List<String> decodeRequestToken(String token) {
        List<String> params = new ArrayList<>();
        String identifier = jwtProvider.getRequestIdentifierFromToken(token);
        if (identifier != null) {
            if (!identifier.equals("")) {
                String[] identifierArray = identifier.split("\\.");
                for (int i = 0; i < identifierArray.length; i++) {
                    params.add(identifierArray[i]);
                }
                return params;
            }
        }
        return null;
    }

    // Метод получения айди запроса по айди НР и студента
    public Integer getRequestId(String scientificAdvisor, String student) {
        Integer advisor_id = Integer.parseInt(scientificAdvisor);
        Integer student_id = Integer.parseInt(student);
        if (advisor_id == null || student_id == null) {
            return null;
        }
        AssociatedStudents associatedStudent =
                associatedStudentsRepository.findByScientificAdvisorAndStudent(advisor_id, student_id);
        if (associatedStudent == null) {
            return null;
        }
        return  associatedStudent.getId();
    }

    // Декодировать вердикт по заявке
    public Boolean getAccept(String accept) {
        if (accept.equals("true")) {
            return true;
        } else if (accept.equals("false")) {
            return false;
        } else {
            return null;
        }
    }
}