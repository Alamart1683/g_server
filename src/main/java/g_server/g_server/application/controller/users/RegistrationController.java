package g_server.g_server.application.controller.users;

import g_server.g_server.application.entity.forms.AdminForm;
import g_server.g_server.application.entity.forms.ScientificAdvisorForm;
import g_server.g_server.application.entity.forms.StudentForm;
import g_server.g_server.application.service.mail.MailService;
import g_server.g_server.application.service.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RegistrationController {
    @Autowired
    private UsersService usersService;

    private Integer registrationCode;

    @Autowired
    private MailService mailService;

    @GetMapping("/registration/student")
    public String StudentRegistrationPreparing(Model model)  {
        registrationCode = (int)((Math.random() * (1000000 - 100000)) + 100000);
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

    @PostMapping("/registration/student/mail")
    public String StudentSentConfirmationCode(@ModelAttribute("email") String email) {
        mailService.sendStudentEmail(email, Integer.toString(registrationCode));
        return "Письмо с кодом подтверждения успешно отправлено";
    }

    @PostMapping("/registration/student")
    public List<String> RegisterStudent(
            @ModelAttribute("studentForm") @Validated StudentForm studentForm,
            BindingResult bindingResult, Model model) {
        List<String> messageList = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            messageList.add("Непредвиденная ошибка");
        }
        if (registrationCode != null) {
            if (studentForm.getRegistrationCode() != registrationCode) {
                model.addAttribute("codeConfirmationError", "Код подтверждения указан неверно");
                messageList.add("Код подтверждения указан неверно");
            }
        }
        else {
            messageList.add("Код подверждения больше не существует");
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
        if (studentForm.determineMaidSendingAccepted(studentForm.getMailSendingAccepted()) == null) {
            messageList.add("Ошибка определения согласия на почтовую рассылку");
        }
        if (messageList.size() == 0) {
            if (!usersService.saveStudent(studentForm.StudentFormToUsers(), studentForm.getStudent_type(),
                    studentForm.getStudent_group(), studentForm.getCathedra())) {
                model.addAttribute("usernameError", "Пользователь с данным email уже зарегистрирован");
                messageList.add("Пользователь с таким email уже есть");
            }
            else {
                messageList.add("Студент успешно зарегистрирован!");
                registrationCode = null;
            }
        }
        return messageList;
    }

    @GetMapping("/admin/registration/scientific_advisor")
    public String ScientificAdvisorRegistrationPreparing (Model model) {
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
            }
            else {
                // Отправка письма науч. руководителю
                mailService.sendLoginEmail(scientificAdvisorForm.getEmail(), scientificAdvisorForm.getPassword(), "научного руководителя");
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
        if (scientificAdvisorForm.determineMaidSendingAccepted(scientificAdvisorForm.getMailSendingAccepted()) == null) {
            messageList.add("Ошибка определения согласия на почтовую рассылку");
        }
        if (messageList.size() == 0) {
            if (!usersService.saveHeadOfCathedra(scientificAdvisorForm.ScientificAdvisorFormToUsers(),
                    scientificAdvisorForm.getCathedra())) {
                model.addAttribute("usernameError", "Пользователь с данным email уже зарегистрирован");
                messageList.add("Пользователь с таким email уже есть");
            }
            else {
                // Отправка письма зав. кафедры
                mailService.sendLoginEmail(scientificAdvisorForm.getEmail(), scientificAdvisorForm.getPassword(), "заведующего кафедрой");
                messageList.add("Заведующий кафедрой успшено зарегистрирован!");
            }
        }
        return messageList;
    }

    @GetMapping("/admin/registration/admin")
    public String AdminRegistrationPreparing(Model model) {
        model.addAttribute("adminForm", new AdminForm());
        return "adminForm";
    }

    @PostMapping("/admin/registration/admin")
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
        if (adminForm.determineMaidSendingAccepted(adminForm.getMailSendingAccepted()) == null) {
            messageList.add("Ошибка определения согласия на почтовую рассылку");
        }
        if (messageList.size() == 0) {
            if (!usersService.saveAdmin(adminForm.AdminFormToUsers())) {
                model.addAttribute("usernameError", "Пользовател с данным email уже зарегистрирован");
                messageList.add("Пользователь с таким email уже есть");
            }
            else {
                // Отправка письма администратору
                mailService.sendLoginEmail(adminForm.getEmail(), adminForm.getPassword(), "администратора");
                messageList.add("Администратор успешно зарегистрирован!");
            }
        }
        return messageList;
    }
}