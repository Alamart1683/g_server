package g_server.g_server.application.service.users;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.view.AssociatedStudentView;
import g_server.g_server.application.entity.users.AssociatedStudents;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.repository.project.ProjectThemeRepository;
import g_server.g_server.application.repository.users.AssociatedStudentsRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import g_server.g_server.application.repository.users.UsersRolesRepository;
import g_server.g_server.application.service.documents.DocumentUploadService;
import g_server.g_server.application.service.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

// Сервис взаимодействия студентов и научных руководителей
@Service
public class AssociatedStudentsService {
    // TODO Нигде не забыть заменить на итоговый адрес сервера
    private static final String apiUrl = "http://localhost:8080/";

    @Autowired
    private DocumentUploadService documentUploadService;

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

    // Отправить заявку научному руководителю от имени студента на научное руководство
    public List<String> sendRequestForScientificAdvisor(String token,
        Integer scientificAdvisorId, String theme) {
        List<String> messageList = new ArrayList<>();
        Integer student_id = null;
        // Проверка токена
        if (token == null) {
            messageList.add("Ошибка аутентификации: токен равен null");
        }
        if (token.equals("")) {
            messageList.add("Ошибка аутентификации: токен пуст");
        }
        else {
            student_id = getUserId(token);
        }
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

    // Показать список активных заявок
    public List<AssociatedStudentView> getActiveRequests(String token) {
        List<String> messageList = new ArrayList<>();
        // Проверка токена
        if (token == null) {
            return null;
        }
        if (token.equals("")) {
            return null;
        }
        Integer scientificAdvisorId = getUserId(token);
        List<AssociatedStudentView> activeRequests = new ArrayList<>();
        Users scientificAdvisor = usersRepository.findById(scientificAdvisorId).get();
        if (scientificAdvisor != null) {
            List<AssociatedStudents> associatedStudents =
                    associatedStudentsRepository.findByScientificAdvisor(scientificAdvisorId);
            // TODO Это очевидный костыль из-за того, что я не понимаю, почему Hibernate
            // TODO не хочет брать как параметр выборки булевское поле. В будущем его надо
            // TODO пофиксить, ибо это жутко не оптимальное решение
            for (AssociatedStudents associatedStudentRaw: associatedStudents) {
                if (associatedStudentRaw.isAccepted()) {
                    associatedStudents.remove(associatedStudentRaw);
                }
            }
            for (AssociatedStudents associatedStudent: associatedStudents) {
                Users currentStudent = usersRepository.findById(associatedStudent.getStudent()).get();
                String currentTheme = associatedStudent.getProjectTheme().getTheme();
                AssociatedStudentView associatedStudentForm = new AssociatedStudentView(currentStudent,
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

    // TODO Показать список ассоциированных студентов

    // TODO Отозвать заявку от лица студента

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