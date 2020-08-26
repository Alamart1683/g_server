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

@RestController
public class RegistrationController {
    @Autowired
    private UsersService usersService;

    @Autowired
    private MailService mailService;

    @GetMapping("/registration/student")
    public String StudentRegistrationPreparing(@ModelAttribute("email") String email, Model model) {
        int registrationCode = (int)((Math.random() * (1000000 - 100000)) + 100000);
        StudentForm studentForm = new StudentForm();
        studentForm.setRegistrationCode(registrationCode);
        model.addAttribute("studentForm", studentForm);
        // Отправка письма студенту
        mailService.sendStudentEmail(email, Integer.toString(registrationCode));
        return "studentForm";
    }

    @PostMapping("/registration/student")
    public String RegisterStudent(
            @ModelAttribute("studentForm") @Validated StudentForm studentForm,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "Непредвиденная ошибка";
        }
        if (studentForm.getRegistrationCode() != studentForm.getRegistrationCodeConfirm()) {
            model.addAttribute("codeConfirmationError", "Код подтверждения указан неверно");
            return "Код подтверждения указан неверно";
        }
        if (!studentForm.getPassword().equals(studentForm.getPasswordConfirm())) {
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "Пароли не совпадают";
        }
        if (!usersService.isCathedraExist(studentForm)) {
            return "Указана несуществующая кафедра";
        }
        if (!usersService.isGroupExist(studentForm)) {
            return "Указана несуществующая группа";
        }
        if (!usersService.isStudentTypeExist(studentForm)) {
            return "Указан несуществуюший тип";
        }
        if (!usersService.saveStudent(studentForm.StudentFormToUsers(), studentForm.getStudent_type(),
                studentForm.getStudent_group(), studentForm.getCathedra())) {
            model.addAttribute("usernameError", "Пользователь с данным email уже зарегистрирован");
            return "Пользователь с таким email уже есть";
        }
        return "success!";
    }

    @GetMapping("/admin/registration/scientific_advisor")
    public String ScientificAdvisorRegistrationPreparing (Model model) {
        ScientificAdvisorForm scientificAdvisorForm = new ScientificAdvisorForm();
        model.addAttribute("scientificAdvisorForm", scientificAdvisorForm);
        return "scientificAdvisorForm";
    }

    @PostMapping("/admin/registration/scientific_advisor")
    public String RegisterScientificAdvisor(
            @ModelAttribute("scientificAdvisorForm") @Validated ScientificAdvisorForm scientificAdvisorForm,
            BindingResult bindingResult, Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "Непредвиденная ошибка";
        }
        if (!scientificAdvisorForm.getPassword().equals(scientificAdvisorForm.getPasswordConfirm())) {
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "Пароли не совпадают";
        }
        if (!usersService.isCathedraExist(scientificAdvisorForm)) {
            return "Указана несуществующая кафедра";
        }
        if (!usersService.saveScientificAdvisor(scientificAdvisorForm.ScientificAdvisorFormToUsers(),
                scientificAdvisorForm.getCathedra())) {
            model.addAttribute("usernameError", "Пользовател с данным email уже зарегистрирован");
            return "Пользователь с таким email уже есть";
        }
        // Отправка письма науч. рководителю
        mailService.sendLoginEmail(scientificAdvisorForm.getEmail(), scientificAdvisorForm.getPassword());
        return "success!";
    }

    @GetMapping("/admin/registration/head_of_cathedra")
    public String HeadOfCathedraRegistrationPreparing(Model model) {
        model.addAttribute("scientificAdvisorForm", new ScientificAdvisorForm());
        return "scientificAdvisorForm";
    }

    @PostMapping("/admin/registration/head_of_cathedra")
    public String RegisterHeadOfCathedra(
            @ModelAttribute("scientificAdvisorForm") @Validated ScientificAdvisorForm scientificAdvisorForm,
            BindingResult bindingResult, Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "Непредвиденная ошибка";
        }
        if (!scientificAdvisorForm.getPassword().equals(scientificAdvisorForm.getPasswordConfirm())) {
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "Пароли не совпадают";
        }
        if (!usersService.isCathedraExist(scientificAdvisorForm)) {
            return "Указана несуществующая кафедра";
        }
        if (!usersService.saveHeadOfCathedra(scientificAdvisorForm.ScientificAdvisorFormToUsers(),
                scientificAdvisorForm.getCathedra())) {
            model.addAttribute("usernameError", "Пользователь с данным email уже зарегистрирован");
            return "Пользователь с таким email уже есть";
        }
        // Отправка письма зав. кафедры
        mailService.sendLoginEmail(scientificAdvisorForm.getEmail(), scientificAdvisorForm.getPassword());
        return "success!";
    }

    @GetMapping("/admin/registration/admin")
    public String AdminRegistrationPreparing(Model model) {
        model.addAttribute("adminForm", new AdminForm());
        return "adminForm";
    }

    @PostMapping("/admin/registration/admin")
    public String RegisterAdmin(
          @ModelAttribute("adminForm") @Validated AdminForm adminForm,
          BindingResult bindingResult, Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        if (!adminForm.getPassword().equals(adminForm.getPasswordConfirm())) {
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "Пароли не совпадают";
        }
        if (!usersService.saveAdmin(adminForm.AdminFormToUsers())) {
            model.addAttribute("usernameError", "Пользовател с данным email уже зарегистрирован");
            return "Пользователь с таким email уже есть";
        }
        // Отправка письма администратору
        mailService.sendLoginEmail(adminForm.getEmail(), adminForm.getPassword());
        return "success!";
    }
}