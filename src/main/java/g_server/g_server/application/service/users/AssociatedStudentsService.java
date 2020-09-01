package g_server.g_server.application.service.users;

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
            student_id = documentUploadService.getCreatorId(token);
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
                    associatedStudentsRepository.findByScientificAdvisorAndStudent(student_id, scientificAdvisorId);
            if (existController != null) {
                messageList.add("Одновременно позволено подавать только одну заявку для одного научного руководителя");
            }
            // Если не возникло ошибок, добавим заявку
            if (messageList.size() == 0) {
                // Сформируем заявку
                AssociatedStudents associatedStudent = new AssociatedStudents(student_id,
                        scientificAdvisorId, false);
                // Сохраним заявку
                associatedStudentsRepository.save(associatedStudent);

                // Проверим, активна ли у научного руководителя почтовая рассылка
                if (scientificAdvisor.isSendMailAccepted()) {
                    // Отправим ему письмо с уведомлением
                    mailService.sendRequestForScientificAdvisorMail(student, scientificAdvisor, theme);
                    messageList.add("Ваш потенциальный научный руководитель" +
                            " получил уведомление по почте о вашей заявке");
                }
                messageList.add("Заяка успешно оформлена");
            }
        }
        return messageList;
    }

    // TODO Показать список активных заявок
    // public List<>

    // TODO Принять заявку

    // TODO Отклонить заявку

    // TODO Показать список ассоциированных студентов
}