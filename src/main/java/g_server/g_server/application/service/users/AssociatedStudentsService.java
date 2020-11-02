package g_server.g_server.application.service.users;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.entity.documents.OrderProperties;
import g_server.g_server.application.entity.project.OccupiedStudents;
import g_server.g_server.application.entity.project.Project;
import g_server.g_server.application.entity.project.ProjectArea;
import g_server.g_server.application.entity.system_data.Speciality;
import g_server.g_server.application.entity.users.*;
import g_server.g_server.application.entity.view.*;
import g_server.g_server.application.repository.documents.DocumentRepository;
import g_server.g_server.application.repository.documents.OrderPropertiesRepository;
import g_server.g_server.application.repository.project.OccupiedStudentsRepository;
import g_server.g_server.application.repository.project.ProjectRepository;
import g_server.g_server.application.repository.project.ProjectAreaRepository;
import g_server.g_server.application.repository.system_data.SpecialityRepository;
import g_server.g_server.application.repository.users.AssociatedStudentsRepository;
import g_server.g_server.application.repository.users.StudentDataRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import g_server.g_server.application.repository.users.UsersRolesRepository;
import g_server.g_server.application.service.documents.DocumentManagementService;
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
    @Value("${api.url}")
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
    private ProjectAreaRepository projectAreaRepository;

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

    @Autowired
    private OrderPropertiesRepository orderPropertiesRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentManagementService documentManagementService;

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
                AssociatedStudents associatedStudent = new AssociatedStudents(scientificAdvisorId, student_id, false);
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
                AssociatedRequestView associatedStudentForm = new AssociatedRequestView(currentStudent,
                        associatedStudent.getId());
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
                String projectName = "Проект не назначен";
                String projectArea = "Нет проектной области";
                OccupiedStudents occupiedStudent = occupiedStudentsRepository.findByStudentID(studentID);
                Project project = null;
                if (occupiedStudent != null) {
                    try { project = projectRepository.findById(occupiedStudent.getProjectID()).get(); }
                    catch (NoSuchElementException noSuchElementException) { }
                }
                if (project != null) {
                    projectName = project.getName();
                    projectArea = project.getProjectArea().getArea();
                }
                AssociatedStudentView activeStudentForm = new AssociatedStudentView(currentStudent,
                        associatedStudent.getId(), projectName, projectArea,
                        associatedStudent.getStudentUser().getPhone(),
                        associatedStudent.getStudentUser().getEmail(),
                        documentManagementService.getStudentsDocumentStatus(studentID)
                        );
                activeStudents.add(activeStudentForm);
            }
            return activeStudents;
        }
        return null;
    }

    // Показать список студентов данного научного руководителя, которые не участвуют в проектах
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
                    AssociatedStudentViewWithoutProject associatedStudentViewWithoutProject = new AssociatedStudentViewWithoutProject(
                            currentStudent,
                            associatedStudent.getId(),
                            associatedStudent.getStudentUser().getPhone(),
                            associatedStudent.getStudentUser().getEmail(),
                            documentManagementService.getStudentsDocumentStatus(studentID)
                            );
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

    // Проверить, имеет ли студент научного руководителя
    public List<String> isStudentHasAdvisor(String token) {
        List<String> messageList = new ArrayList<>();
        Integer studentID = getUserId(token);
        if (studentID == null) {
            messageList.add("ID студента не найден");
        }
        if (messageList.size() == 0) {
            AssociatedStudents associatedStudent = associatedStudentsRepository.findByStudent(studentID);
            if (associatedStudent != null) {
                messageList.add("Данный студент имеет научного руководителя");
            }
            else {
                messageList.add("Данный студент не имеет научного руководителя");
            }
        }
        return messageList;
    }

    // Отказаться от научного руководства студента для научного руководителя
    public List<String> dismissStudentByAdvisor(String token, Integer systemID) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("Не удается найти ID научного руководителя");
        }
        if (systemID == null) {
            messageList.add("Некорректный системный ID");
        }
        AssociatedStudents associatedStudent = null;
        try { associatedStudent = associatedStudentsRepository.findById(systemID).get(); }
        catch (NoSuchElementException noSuchElementException) { messageList.add("Не удается найти запись о руководстве"); }
        if (advisorID == associatedStudent.getScientificAdvisor()) {
            messageList.add("Вы не можете отказаться от чужого студента");
        }
        if (messageList.size() == 0) {
            // TODO Возмжно стоит вместо уведомления об отказе от студента
            // TODO сделать требование подтвердения отказа по почте
            associatedStudentsRepository.deleteById(systemID);
            messageList.add("Вы успешно отказались от научного руководства для данного студента");
        }
        return messageList;
    }

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
                messageList.add("Вы не можете удалять студента не из своего проекта");
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
        for (ScientificAdvisorData currentAdvisorData: advisorList) {
            Users currentAdvisor = usersRepository.findById(currentAdvisorData.getId()).get();
            List<AssociatedStudents> associatedStudents = new ArrayList<>();
            List<AssociatedStudents> associatedStudentsRaw =
                    associatedStudentsRepository.findByScientificAdvisor(currentAdvisorData.getId());
            List<ProjectArea> projectThemesRaw = projectAreaRepository.findByAdvisor(currentAdvisorData.getId());
            List<String> projectThemes = new ArrayList<>();
            for (ProjectArea projectAreaRaw : projectThemesRaw) {
                projectThemes.add(projectAreaRaw.getArea());
            }
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

    // Получить представление проекта
    public List<ProjectView> getProjectView(String token) {
        List<ProjectView> projectViews = new ArrayList<>();
        Integer userID;
        try {
            userID = getUserId(token);
        } catch (Exception e) {
            return null;
        }
        Users user;
        try {
            user = usersRepository.findById(userID).get();
        } catch (NoSuchElementException noSuchElementException) {
            return null;
        }
        UsersRoles usersRoles = usersRolesRepository.findUsersRolesByUserId(userID);
        Integer userRole = usersRoles.getRoleId();
        Project project;
        List<OccupiedStudents> occupiedStudents;
        switch (userRole) {
            case 1:
                try {
                    List<AssociatedStudentViewWithoutProject> occupiedStudentViews = new ArrayList<>();
                    Integer projectID = occupiedStudentsRepository.findByStudentID(userID).getProjectID();
                    project = projectRepository.findById(projectID).get();
                    occupiedStudents = occupiedStudentsRepository.findAllByProjectID(projectID);
                    Users advisor = usersRepository.findById(project.getScientificAdvisorID()).get();
                    for (OccupiedStudents occupiedStudent: occupiedStudents) {
                        Users student = usersRepository.findById(occupiedStudent.getStudentID()).get();
                        // В данном случае системный айди будет не айди записи ассоциации, а сам айди студента
                        AssociatedStudentViewWithoutProject currentStudentView = new AssociatedStudentViewWithoutProject(
                                student,
                                student.getId(),
                                student.getPhone(),
                                student.getEmail(),
                                documentManagementService.getStudentsDocumentStatus(student.getId())
                        );
                        occupiedStudentViews.add(currentStudentView);
                    }
                    ProjectView projectView = new ProjectView(
                            projectID,
                            project.getProjectArea().getId(),
                            advisor.getId(),
                            project.getName(),
                            project.getProjectArea().getArea(),
                            advisor.getSurname() + " " + advisor.getName() + " " + advisor.getSecond_name(),
                            occupiedStudentViews
                    );
                    projectViews.add(projectView);
                    return projectViews;
                } catch (Exception e) {
                    return null;
                }
            case 2:
                List<Project> advisorProjects;
                try {
                    advisorProjects = projectRepository.findAllByScientificAdvisorID(userID);
                    for (Project advisorProject: advisorProjects) {
                        List<AssociatedStudentViewWithoutProject> occupiedStudentViews = new ArrayList<>();
                        occupiedStudents = occupiedStudentsRepository.findAllByProjectID(advisorProject.getId());
                        for (OccupiedStudents occupiedStudent: occupiedStudents) {
                            Users student = usersRepository.findById(occupiedStudent.getStudentID()).get();
                            // В данном случае системный айди будет не айди записи ассоциации, а сам айди студента
                            AssociatedStudentViewWithoutProject currentStudentView = new AssociatedStudentViewWithoutProject(
                                    student,
                                    student.getId(),
                                    student.getPhone(),
                                    student.getEmail(),
                                    documentManagementService.getStudentsDocumentStatus(student.getId())
                            );
                            occupiedStudentViews.add(currentStudentView);
                        }
                        ProjectView projectView = new ProjectView(
                                advisorProject.getId(),
                                advisorProject.getProjectArea().getId(),
                                userID,
                                advisorProject.getName(),
                                advisorProject.getProjectArea().getArea(),
                                user.getSurname() + " " + user.getName() + " " + user.getSecond_name(),
                                occupiedStudentViews
                        );
                        projectViews.add(projectView);
                    }
                    return projectViews;
                } catch (Exception e) {
                    return null;
                }
            default:
                return null;
        }
    }

    // Метод получения всех известных данных для заполнения задания для конкретного студента
    public TaskDataViewWithMessage getNirTaskDataView(String token) {
        TaskDataViewWithMessage taskDataViewWithMessage = new TaskDataViewWithMessage();
        Integer userID;
        try {
            userID = getUserId(token);
        } catch (Exception e) {
            return null;
        }
        Users student;
        try {
            student = usersRepository.findById(userID).get();
        } catch (NoSuchElementException noSuchElementException) {
            return null;
        }
        if (student != null) {
            AssociatedStudents associatedStudents;
            try {
                associatedStudents = associatedStudentsRepository.findByStudent(student.getId());
            } catch (NullPointerException nullPointerException) {
                associatedStudents = null;
            }
            if (associatedStudents != null) {
                Speciality speciality = specialityRepository.findByPrefix(student.getStudentData()
                        .getStudentGroup().getStudentGroup().substring(0, 4));
                OrderProperties orderProperty;
                try {
                    orderProperty = orderPropertiesRepository.findBySpeciality(speciality.getId());
                } catch (NullPointerException nullPointerException) {
                    orderProperty = null;
                }
                if (orderProperty != null) {
                    Document document = documentRepository.findById(orderProperty.getId()).get();
                    Users advisor = usersRepository.findById(associatedStudents.getScientificAdvisor()).get();
                    Users headOfCathedra = usersRepository.findById(
                            usersRolesRepository.findByRoleId(3).getUserId()).get();
                    List<Document> taskList = documentRepository.findByTypeAndKind(1, 2);
                    if (taskList.size() > 0) {
                        TaskDataView taskDataView = new TaskDataView();
                        taskDataView.setTaskType(document.getDocumentType().getType());
                        taskDataView.setStudentFio(student.getSurname() + " " + student.getName() +
                                " " + student.getSecond_name());
                        taskDataView.setStudentGroup(student.getStudentData().getStudentGroup().getStudentGroup());
                        taskDataView.setStudentTheme("Введите согласованную тему НИР");
                        taskDataView.setAdvisorFio(advisor.getSurname() + " " + advisor.getName() +
                                " " + advisor.getSecond_name());
                        taskDataView.setHeadFio(headOfCathedra.getSurname() + " " + headOfCathedra.getName() +
                                " " + headOfCathedra.getSecond_name());
                        taskDataView.setCathedra(student.getStudentData().getCathedras().getCathedraName());
                        taskDataView.setOrderNumber(orderProperty.getNumber());
                        taskDataView.setOrderDate(convertSQLDateToRussianFormat(orderProperty.getOrderDate()));
                        taskDataView.setOrderStartDate(convertSQLDateToRussianFormat(orderProperty.getStartDate()));
                        taskDataView.setOrderEndDate(convertSQLDateToRussianFormat(orderProperty.getEndDate()));
                        taskDataView.setOrderSpeciality(speciality.getCode());
                        taskDataView.setToCreate("Создать");
                        taskDataView.setToExplore("Изучить");
                        taskDataView.setToFamiliarize("Ознакомиться");
                        taskDataView.setAdditionalTask("Дополнительное задание");
                        taskDataViewWithMessage.setTaskDataView(taskDataView);
                        taskDataViewWithMessage.setMessage("Данные получены успешно");
                    } else {
                        taskDataViewWithMessage.setMessage("Образец задания еще не был загружен в систему");
                    }
                } else {
                    taskDataViewWithMessage.setMessage("Приказ еще не вышел");
                }
                return taskDataViewWithMessage;
            } else {
                taskDataViewWithMessage.setMessage("Студенту не назначен научный руководитель");
                return taskDataViewWithMessage;
            }
        } else {
            taskDataViewWithMessage.setMessage("Студент не найден");
            return taskDataViewWithMessage;
        }
    }

    // TODO Сделать назначение области проекта студенту

    // TODO Сделать создание области проекта преподавателем

    // Получение списка имен проектов конкретного НР
    public List<String> getAdvisorProjectNames(String token) {
        Integer advisorID;
        List<String> projectNames = new ArrayList<>();
        try {
            advisorID = getUserId(token);
        } catch (Exception e) {
            return null;
        }
        List<Project> projectList = projectRepository.findAllByScientificAdvisorID(advisorID);
        for (Project project: projectList) {
            projectNames.add(project.getName());
        }
        return projectNames;
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

    public String convertSQLDateToRussianFormat(String sqlDate) {
        String year = sqlDate.substring(0, 4);
        String month = sqlDate.substring(5, 7);
        String day = sqlDate.substring(8);
        return day + '.' + month + '.' + year;
    }
}