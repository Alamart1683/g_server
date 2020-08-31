package g_server.g_server.application.controller.users;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.forms.AdminForm;
import g_server.g_server.application.entity.forms.ScientificAdvisorForm;
import g_server.g_server.application.entity.forms.StudentForm;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.repository.users.UsersRepository;
import g_server.g_server.application.service.mail.MailService;
import g_server.g_server.application.service.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RegistrationController {
    private Integer registrationCode;
    private String studentRegistrationEmail;
    private String confirmUrl;
    private static final String apiUrl = "http://localhost:8080/";

    @Autowired
    private UsersService usersService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MailService mailService;

    @Autowired
    private UsersRepository usersRepository;

    // Отправить форму регистрации на клиент, сгенерировать код подтверждения
    @GetMapping("/registration/student")
    public String StudentRegistrationPreparing(Model model) {
        registrationCode = (int) ((Math.random() * (1000000 - 100000)) + 100000);
        StudentForm studentForm = new StudentForm();
        model.addAttribute("studentForm", studentForm);
        return "studentForm";
    }

    @PostMapping("/registration/mail/check/valid")
    public String CheckEmail(@ModelAttribute("email") String email) {
        if (mailService.checkMail(email))
            return "email is valid";
        else
            return "email is not valid";
    }

    @PostMapping("/registration/mail/check/free")
    public String isFree(@ModelAttribute("email") String email) {
        if (!usersService.isEmailExist(email))
            return "email is free";
        else
            return "email is not free";
    }

    @PostMapping("/registration/student")
    public List<String> RegisterStudent(
            @ModelAttribute("studentForm") @Validated StudentForm studentForm,
            BindingResult bindingResult, Model model) {
        List<String> messageList = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            messageList.add("Непредвиденная ошибка");
        }
        if (!studentForm.getPassword().equals(studentForm.getPasswordConfirm())) {
            model.addAttribute("passwordError", "Пароли не совпадают");
            messageList.add("Пароли не совпадают");
        }
        if (!usersService.isCathedraExist(studentForm)) {
            messageList.add("Указана несуществующая кафедра");
        }
        if (!usersService.isGroupExist(studentForm)) {
            messageList.add("Указана несуществующая группа");
        }
        if (!usersService.isStudentTypeExist(studentForm)) {
            messageList.add("Указан несуществуюший тип");
        }
        if (studentForm.determineMailSendingAccepted(studentForm.getMailSendingAccepted()) == null) {
            messageList.add("Ошибка определения согласия на почтовую рассылку");
        }
        if (registrationCode == null) {
            messageList.add("Код подтверждения регистрации не был сгенерирован");
        }
        if (messageList.size() == 0) {
            if (!usersService.saveStudent(studentForm.StudentFormToUsers(), studentForm.getStudent_type(),
                    studentForm.getStudent_group(), studentForm.getCathedra())) {
                model.addAttribute("usernameError", "Пользователь с данным email уже зарегистрирован");
                messageList.add("Пользователь с таким email уже есть");
            } else {
                messageList.add("Студент успешно зарегистрирован!");
                // Сохраним email, с которого была совершена регистрация, в буфер
                studentRegistrationEmail = studentForm.getEmail();
                // Сгенерируем токен для подтверждения регистрации по ссылке
                String tokenConfirm = jwtProvider.generateConfirmToken(registrationCode);
                confirmUrl = apiUrl + "registration/student/confirm/" + tokenConfirm;
                mailService.sendStudentEmail(studentForm.getEmail(), Integer.toString(registrationCode), confirmUrl);
                messageList.add("Письмо с кодом подтверждения успешно отправлено");
            }
        }
        return messageList;
    }

    // Запрос на повторную отправку кода
    @PostMapping("/registration/student/confirm/{email}")
    public String repeatConformCodeSending(@PathVariable String email) {
        mailService.sendStudentEmail(email, Integer.toString(registrationCode), confirmUrl);
        return "Код подтверждения был успешно выслан повторно";
    }

    // Запрос подтверждения кода и аккаунта студента без ссылки
    @PostMapping("/registration/student/confirm/")
    public List<String> StudentConfirmCode(
            @RequestParam int sentRegistrationCode,
            @RequestParam String email
            ) {
        List<String> messageList = new ArrayList<>();
        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            messageList.add("Ошибка: зарегистрированный пользователь не найден");
        }
        if (registrationCode != null) {
            if (sentRegistrationCode != registrationCode) {
                messageList.add("Код подтверждения указан неверно");
            }
        } else {
            messageList.add("Код подверждения больше не существует");
        }
        if (!email.equals(studentRegistrationEmail)) {
            messageList.add("Ошибка синхронизации email-адресов");
        }
        if (messageList.size() == 0) {
            user.setConfirmed(true);
            usersRepository.save(user);
            registrationCode = null;
            studentRegistrationEmail = null;
            confirmUrl = null;
            messageList.add("Аккаунт был успешно подтвержден");
        }
        return messageList;
    }

    // Активация аккаунта по сгенерированной ссылке
    @GetMapping("/registration/student/confirm/{token}")
    public String StudentConfirmCodeFromUrl(@PathVariable String token) {
        Integer confirmationCode = Integer.parseInt(jwtProvider.getRegistrationCodeFromToken(token));
        if (confirmationCode == null) {
            return "Срок действия ссылки подтверждения истек или она указана неверно";
        }
        if (confirmationCode.equals(registrationCode)) {
            Users user = usersRepository.findByEmail(studentRegistrationEmail);
            user.setConfirmed(true);
            usersRepository.save(user);
            registrationCode = null;
            studentRegistrationEmail = null;
            confirmUrl = null;
            return "Аккаунт был успешно подтвержден";
        }
        else {
            return "Чексумма не совпадает";
        }
    }

    @GetMapping("/admin/registration/scientific_advisor")
    public String ScientificAdvisorRegistrationPreparing(Model model) {
        ScientificAdvisorForm scientificAdvisorForm = new ScientificAdvisorForm();
        model.addAttribute("scientificAdvisorForm", scientificAdvisorForm);
        return "scientificAdvisorForm";
    }

    @PostMapping("/admin/registration/scientific_advisor")
    public List<String> RegisterScientificAdvisor(
            @ModelAttribute("scientificAdvisorForm") @Validated ScientificAdvisorForm scientificAdvisorForm,
            BindingResult bindingResult, Model model
    ) {
        List<String> messageList = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            messageList.add("Непредвиденная ошибка");
        }
        if (!scientificAdvisorForm.getPassword().equals(scientificAdvisorForm.getPasswordConfirm())) {
            model.addAttribute("passwordError", "Пароли не совпадают");
            messageList.add("Пароли не совпадают");
        }
        if (!usersService.isCathedraExist(scientificAdvisorForm)) {
            messageList.add("Указана несуществующая кафедра");
        }
        if (messageList.size() == 0) {
            if (!usersService.saveScientificAdvisor(scientificAdvisorForm.ScientificAdvisorFormToUsers(),
                    scientificAdvisorForm.getCathedra())) {
                model.addAttribute("usernameError", "Пользовател с данным email уже зарегистрирован");
                messageList.add("Пользователь с таким email уже есть");
            } else {
                // Отправка письма науч. руководителю
                mailService.sendLoginEmailAndPassword(scientificAdvisorForm.getEmail(), scientificAdvisorForm.getPassword(), "научного руководителя");
                messageList.add("Научный руководитель зарегистрирован!");
            }
        }
        return messageList;
    }

    @GetMapping("/admin/registration/head_of_cathedra")
    public String HeadOfCathedraRegistrationPreparing(Model model) {
        model.addAttribute("scientificAdvisorForm", new ScientificAdvisorForm());
        return "scientificAdvisorForm";
    }

    @PostMapping("/admin/registration/head_of_cathedra")
    public List<String> RegisterHeadOfCathedra(
            @ModelAttribute("scientificAdvisorForm") @Validated ScientificAdvisorForm scientificAdvisorForm,
            BindingResult bindingResult, Model model
    ) {
        List<String> messageList = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            messageList.add("Непредвиденная ошибка");
        }
        if (!scientificAdvisorForm.getPassword().equals(scientificAdvisorForm.getPasswordConfirm())) {
            model.addAttribute("passwordError", "Пароли не совпадают");
            messageList.add("Пароли не совпадают");
        }
        if (!usersService.isCathedraExist(scientificAdvisorForm)) {
            messageList.add("Указана несуществующая кафедра");
        }
        if (scientificAdvisorForm.determineMailSendingAccepted(scientificAdvisorForm.getMailSendingAccepted()) == null) {
            messageList.add("Ошибка определения согласия на почтовую рассылку");
        }
        if (messageList.size() == 0) {
            if (!usersService.saveHeadOfCathedra(scientificAdvisorForm.ScientificAdvisorFormToUsers(),
                    scientificAdvisorForm.getCathedra())) {
                model.addAttribute("usernameError", "Пользователь с данным email уже зарегистрирован");
                messageList.add("Пользователь с таким email уже есть");
            } else {
                // Отправка письма зав. кафедры
                mailService.sendLoginEmailAndPassword(scientificAdvisorForm.getEmail(), scientificAdvisorForm.getPassword(), "заведующего кафедрой");
                messageList.add("Заведующий кафедрой успшено зарегистрирован!");
            }
        }
        return messageList;
    }

    @GetMapping("/root/registration/admin")
    public String AdminRegistrationPreparing(Model model) {
        model.addAttribute("adminForm", new AdminForm());
        return "adminForm";
    }

    @PostMapping("/root/registration/admin")
    public List<String> RegisterAdmin(
            @ModelAttribute("adminForm") @Validated AdminForm adminForm,
            BindingResult bindingResult, Model model
    ) {
        List<String> messageList = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            messageList.add("registration");
        }
        if (!adminForm.getPassword().equals(adminForm.getPasswordConfirm())) {
            model.addAttribute("passwordError", "Пароли не совпадают");
            messageList.add("Пароли не совпадают");
        }
        if (adminForm.determineMailSendingAccepted(adminForm.getMailSendingAccepted()) == null) {
            messageList.add("Ошибка определения согласия на почтовую рассылку");
        }
        if (messageList.size() == 0) {
            if (!usersService.saveAdmin(adminForm.AdminFormToUsers())) {
                model.addAttribute("usernameError", "Пользовател с данным email уже зарегистрирован");
                messageList.add("Пользователь с таким email уже есть");
            } else {
                // Отправка письма администратору
                mailService.sendLoginEmailAndPassword(adminForm.getEmail(), adminForm.getPassword(), "администратора");
                messageList.add("Администратор успешно зарегистрирован!");
            }
        }
        return messageList;
    }
}