package g_server.g_server.application.controller;

import g_server.g_server.application.entity.StudentForm;
import g_server.g_server.application.service.UsersService;
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

    @GetMapping("/registration/student")
    public String Registration(Model model) {
        model.addAttribute("studentForm", new StudentForm());
        return "registration";
    }

    @PostMapping("/registration/student")
    public String RegisterUser(
            @ModelAttribute("studentForm") @Validated StudentForm studentForm,
            BindingResult bindingResult, Model model) {
        // int registrationCode = (int)((Math.random() * (1000000 - 100000)) + 100000);
        // studentForm.setRegistrationCode(registrationCode);
        // Сделать отправку кода подтверждения по почте
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        if (studentForm.getRegistrationCode() != studentForm.getRegistrationCodeConfirm()) {
            model.addAttribute("codeConfirmationError", "Код подтверждения указан неверно");
            return "registration";
        }
        if (!studentForm.getPassword().equals(studentForm.getPasswordConfirm())) {
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "registration";
        }
        if (!usersService.saveStudent(studentForm.StudentFormToUsers(), studentForm.getStudent_type(),
                studentForm.getStudent_group(), studentForm.getCathedra())) {
            model.addAttribute("usernameError", "Пользователь с данным email уже зарегестрирован");
        }
        return "redirect:/success!";
    }
}