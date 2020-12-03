package g_server.g_server.application.service.users;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.entity.documents.OrderProperties;
import g_server.g_server.application.entity.forms.AutomaticRegistrationForm;
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
import g_server.g_server.application.repository.system_data.StudentGroupRepository;
import g_server.g_server.application.repository.users.AssociatedStudentsRepository;
import g_server.g_server.application.repository.users.StudentDataRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import g_server.g_server.application.repository.users.UsersRolesRepository;
import g_server.g_server.application.service.documents.DocumentManagementService;
import g_server.g_server.application.service.documents.DocumentProcessorService;
import g_server.g_server.application.service.documents.DocumentUploadService;
import g_server.g_server.application.service.mail.MailService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

// Сервис взаимодействия студентов и научных руководителей
@Service
public class AssociatedStudentsService {
    @Value("${api.url}")
    private String apiUrl;

    @Value("${storage.location}")
    private String storageLocation;

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

    @Autowired
    private DocumentProcessorService documentProcessorService;

    @Autowired
    private DocumentUploadService documentUploadService;

    @Autowired
    private StudentGroupRepository studentGroupRepository;

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
                String projectArea = "Нет комплексного проекта";
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

    // Студент получает информацию о себе и своих документах
    public AssociatedStudentView getInfoAboutStudent(String token) {
        Integer studentID = getUserId(token);
        AssociatedStudents associatedStudent = associatedStudentsRepository.findByStudent(studentID);
        Users student = usersRepository.findById(studentID).get();
        OccupiedStudents occupiedStudent = occupiedStudentsRepository.findByStudentID(studentID);
        String projectName = "Проект не назначен";
        String projectArea = "Нет комплексного проекта";
        Project project = null;
        if (occupiedStudent != null) {
            try { project = projectRepository.findById(occupiedStudent.getProjectID()).get(); }
            catch (NoSuchElementException noSuchElementException) { }
        }
        if (project != null) {
            projectName = project.getName();
            projectArea = project.getProjectArea().getArea();
        }
        return new AssociatedStudentView(student, associatedStudent.getId(), projectName, projectArea,
                associatedStudent.getStudentUser().getPhone(), associatedStudent.getStudentUser().getEmail(),
                documentManagementService.getStudentsDocumentStatus(studentID)
        );
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

    // Получить представление ассоциации всех студентов с их НР для завкафа
    public List<AssociatedStudentViewWithAdvisor> getAllAssociatedStudentsWithAdvisors() {
        List<Users> allUsersList = usersRepository.findAll();
        List<AssociatedStudentViewWithAdvisor> associatedStudentViewWithAdvisorList = new ArrayList<>();
        for (Users user: allUsersList) {
            UsersRoles userRole = usersRolesRepository.findUsersRolesByUserId(user.getId());
            if (userRole.getRoleId() == 1) {
                AssociatedStudents associatedStudent;
                AssociatedStudentViewWithAdvisor currentView;
                try {
                    associatedStudent = associatedStudentsRepository.findByStudent(user.getId());
                    Users advisor = usersRepository.findById(associatedStudent.getScientificAdvisor()).get();
                    Speciality speciality = specialityRepository.findByPrefix(
                            user.getStudentData().getStudentGroup().getStudentGroup().substring(0, 4));
                    currentView = new AssociatedStudentViewWithAdvisor(
                            associatedStudent.getId(),
                            advisor.getId(),
                            advisor.getSurname() + " " + advisor.getName() + " " + advisor.getSecond_name(),
                            user.getId(),
                            user.getSurname() + " " + user.getName() + " " + user.getSecond_name(),
                            user.getStudentData().getStudentGroup().getStudentGroup(),
                            speciality.getCode(),
                            user.isConfirmed()
                    );
                } catch (NullPointerException nullPointerException) {
                    Speciality speciality = specialityRepository.findByPrefix(
                            user.getStudentData().getStudentGroup().getStudentGroup().substring(0, 4));
                    currentView = new AssociatedStudentViewWithAdvisor(
                            null,
                            null,
                            "Не назначен",
                            user.getId(),
                            user.getSurname() + " " + user.getName() + " " + user.getSecond_name(),
                            user.getStudentData().getStudentGroup().getStudentGroup(),
                            speciality.getCode(),
                            user.isConfirmed()
                    );
                }
                if (currentView != null) {
                    associatedStudentViewWithAdvisorList.add(currentView);
                }
            }
        }
        return associatedStudentViewWithAdvisorList;
    }

    // Изменить студенту научного руководителя
    public String changeStudentsAdvisor(Integer studentId, Integer newAdvisorID) {
        try {
            AssociatedStudents associatedStudent = associatedStudentsRepository.findByStudent(studentId);
            if (associatedStudent != null) {
                associatedStudentsRepository.deleteById(associatedStudent.getId());
            }
            AssociatedStudents newAssociatedStudent = new AssociatedStudents(newAdvisorID, studentId, true);
            associatedStudentsRepository.save(newAssociatedStudent);
            Users student;
            if (usersRepository.findById(studentId).isPresent()) {
                student = usersRepository.findById(studentId).get();
                student.setConfirmed(false);
            } else {
                return "Студент не найден";
            }
            usersRepository.save(student);
            return "Научный руководитель успешно изменен";
        } catch (Exception e) {
            return "Переданы некорректные параметры";
        }
    }

    // Одобрить студенту научного руководителя
    public String confirmStudentsAdvisor(Integer studentId) {
        try {
            Users student;
            if (usersRepository.findById(studentId).isPresent()) {
                student = usersRepository.findById(studentId).get();
                student.setConfirmed(true);
            } else {
                return "Студент не найден";
            }
            usersRepository.save(student);
            return "Назанчение успешно одобрено";
        } catch (Exception e) {
            return "Переданы некорректные параметры";
        }
    }

    // Метод автоматической привязки студента к научруку
    public String studentAutomaticAssociation(AutomaticRegistrationForm automaticRegistrationForm) {
        documentUploadService.createDocumentRootDirIfIsNotExist();
        MultipartFile multipartFile = automaticRegistrationForm.getStudentData();
        String tempPath = storageLocation + File.separator + "temp";
        File temp = new File(tempPath);
        if (!temp.exists()) {
            temp.mkdir();
        }
        if (!documentUploadService.getFileExtension(multipartFile).equals("xlsx")) {
            return "Поддреживается только формат xlsx!";
        }
        // Загрузим xlsx-файл в систему
        try (OutputStream os = Files.newOutputStream(Paths.get(tempPath + File.separator +
                "studentAssocData.xlsx"))) {
            os.write(multipartFile.getBytes()); os.close();
            XSSFWorkbook excelStudentData = new XSSFWorkbook(
                    new FileInputStream(new File(tempPath + File.separator + "studentAssocData.xlsx")));
            File deleteFile = new File(tempPath + File.separator + "studentAssocData.xlsx");
            XSSFSheet studentSheet;
            try {
                studentSheet = excelStudentData.getSheetAt(1);
            } catch (IllegalArgumentException illegalArgumentException) {
                return "Некорректный формат для файла с сопоставлением научных руководителей с студентами";
            }

            excelStudentData.close();
            List<Users> usersList = usersRepository.findAll();
            try {
                Users currentAdvisor = null;
                Users currentStudent = null;
                Iterator rowIter = studentSheet.rowIterator();
                while (rowIter.hasNext()) {
                    XSSFRow xssfRow = (XSSFRow) rowIter.next();
                    // Проверим что это не первая строка с легендой
                    if (xssfRow.getRowNum() > 0) {
                        try {
                            currentAdvisor = findAdvisorByShortFio(
                                    xssfRow.getCell(2).getStringCellValue(), usersList);
                        } catch (NullPointerException nullPointerException) {
                                currentAdvisor = null;
                        }
                        // Если нашли научрука, то найдем студента по имеющимся данным
                        if (currentAdvisor != null) {
                            try {
                                currentStudent = findStudentByFioAndGroup(
                                        xssfRow.getCell(0).getStringCellValue(),
                                        xssfRow.getCell(3).getStringCellValue(),
                                        usersList
                                );
                            } catch (NullPointerException nullPointerException) {
                                currentStudent = null;
                            }
                        }
                        // Если удалось найти и студента, и научного руководителя. то ассоциируем их
                        if (currentAdvisor != null && currentStudent != null) {
                            // Если он уже проассоциирован, то перезапишем
                            AssociatedStudents associatedStudent = associatedStudentsRepository.findByStudent(currentStudent.getId());
                            if (associatedStudent != null) {
                                associatedStudent.setScientificAdvisor(currentAdvisor.getId());
                                associatedStudentsRepository.save(associatedStudent);
                            // Иначе создадим новую ассоциацию
                            } else if (associatedStudent == null) {
                                associatedStudent = new AssociatedStudents(
                                        currentAdvisor.getId(), currentStudent.getId(), true);
                                associatedStudentsRepository.save(associatedStudent);
                            }
                            currentStudent.setConfirmed(false);
                            usersRepository.save(currentStudent);
                        }
                    }
                }
                deleteFile.delete();
                return "Студенты были успешно ассоциированны!";
            } catch (Exception e) {
                deleteFile.delete();
                return "Ошибка чтения информации внутри файла, проверьте его содержимое";
            }
        } catch (IOException ie) {
            return "Ошибка чтения файла";
        }
    }

    // Студент получает информацию о своей теме вкр
    public VkrThemeView studentGetVkrTheme(String token) {
        Integer studentID = getUserId(token);
        UsersRoles usersRole = usersRolesRepository.findUsersRolesByUserId(studentID);
        if (usersRole.getRoleId() == 1) {
            Users student = usersRepository.findById(studentID).get();
            VkrThemeView vkrThemeView = new VkrThemeView(
                    student.getStudentData().getVkrTheme(),
                    student.getStudentData().isVkrThemeEditable()
            );
            return vkrThemeView;
        }
        return null;
    }

    // Студент редактирует свою тему вкр
    public String studentEditingVkrTheme(String token, String newTheme) {
        Integer studentID = getUserId(token);
        UsersRoles usersRole = usersRolesRepository.findUsersRolesByUserId(studentID);
        if (usersRole.getRoleId() == 1) {
            Users student = usersRepository.findById(studentID).get();
            if (student.getStudentData().isVkrThemeEditable()) {
                student.getStudentData().setVkrTheme(newTheme);
                studentDataRepository.save(student.getStudentData());
                return "Всё в порядке";
            } else {
                return "Тема уже утверждена. Редактирование невозможно";
            }
        }
        return "Пользователь не найден";
    }

    // Научный руководитель редактирует тему студента
    public String advisorEditingStudentTheme(String token, String newTheme, Integer studentID) {
        Integer advisorID = getUserId(token);
        AssociatedStudents associatedStudents = associatedStudentsRepository.findByScientificAdvisorAndStudent(advisorID, studentID);
        if (associatedStudents != null) {
            UsersRoles usersRole = usersRolesRepository.findUsersRolesByUserId(studentID);
            if (usersRole.getRoleId() == 1) {
                Users student = usersRepository.findById(studentID).get();
                if (student.getStudentData().isVkrThemeEditable()) {
                    student.getStudentData().setVkrTheme(newTheme);
                    studentDataRepository.save(student.getStudentData());
                    return "Всё в порядке";
                } else {
                    return "Тема уже утверждена. Редактирование невозможно";
                }
            } else {
                return "Ваш студент не студент";
            }
        } else {
            return "Вы не можете редактировать тему не вашего студента";
        }
    }

    // Научный руководитель утверждает или разутверждает тему вкр
    public String advisorApprovedStudentTheme(String token, Integer studentID, boolean approve) {
        Integer advisorID = getUserId(token);
        AssociatedStudents associatedStudents = associatedStudentsRepository.findByScientificAdvisorAndStudent(advisorID, studentID);
        if (associatedStudents != null) {
            UsersRoles usersRole = usersRolesRepository.findUsersRolesByUserId(studentID);
            if (usersRole.getRoleId() == 1) {
                Users student = usersRepository.findById(studentID).get();
                student.getStudentData().setVkrThemeEditable(approve);
                studentDataRepository.save(student.getStudentData());
                return "Всё в порядке";
            } else {
                return "Ваш студент не студент";
            }
        } else {
            return "Вы не можете утвердить тему не вашего студента";
        }
    }

    // Поиск научного руководителя в системе по укороченному имени
    private Users findAdvisorByShortFio(String shortFio, List<Users> usersList) {
        Users advisor;
        for (Users user: usersList) {
            UsersRoles userRole;
            try {
                userRole = usersRolesRepository.findUsersRolesByUserId(user.getId());
                if (userRole.getRoleId() == 2 || userRole.getRoleId() == 3) {
                    String currentShortFio = documentProcessorService.getShortFio(
                            user.getSurname() + " " + user.getName() + " " + user.getSecond_name());
                    if (currentShortFio.equals(shortFio)) {
                        advisor = user;
                        return advisor;
                    }
                }
            } catch (NullPointerException nullPointerException) {
                return null;
            }
        }
        return null;
    }

    // Поиск студента в системе по фио и группе
    private Users findStudentByFioAndGroup(String fio, String group, List<Users> usersList) {
        String[] names = fio.split(" ");
        group = group.trim();
        Users student;
        for (Users user: usersList) {
            UsersRoles userRole;
            try {
                userRole = usersRolesRepository.findUsersRolesByUserId(user.getId());
                if (userRole.getRoleId() == 1) {
                    StudentData studentData = studentDataRepository.findById(user.getId()).get();
                    String currentGroup = studentGroupRepository.findById(studentData.getStudent_group()).get().getStudentGroup();
                    String currentSurname = user.getSurname();
                    String currentName = user.getName();
                    String currentSecondName = user.getSecond_name();
                    if (currentGroup.equals(group) && currentSurname.equals(names[0]) &&
                            currentName.equals(names[1]) && currentSecondName.equals(names[2])) {
                        student = user;
                        return student;
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

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