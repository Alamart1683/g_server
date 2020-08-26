package g_server.g_server.application.controller.users;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.forms.AuthorizationForm;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.service.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizationController {
    @Autowired
    private UsersService usersService;

    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/authorization")
    public String AuthorizationResponse(
            @ModelAttribute("authorizationForm") @Validated AuthorizationForm authorizationForm,
            BindingResult bindingResult, Model model
    ) {
        Users user = usersService.loadUserByEmailAndPassword(authorizationForm.getEmail(), authorizationForm.getPassword());
        if (user != null) {
            String token = jwtProvider.generateToken(user.getEmail());
            return token;
        }
        else {
            return "Неверная комбинация логина и пароля";
        }
    }
}