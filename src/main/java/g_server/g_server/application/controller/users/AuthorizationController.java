package g_server.g_server.application.controller.users;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.forms.AuthorizationForm;
import g_server.g_server.application.entity.forms.AuthorizationResponseForm;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.service.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.time.ZoneId;

@RestController
public class AuthorizationController {
    @Autowired
    private UsersService usersService;

    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/authorization")
    public AuthorizationResponseForm AuthorizationResponse(
            @ModelAttribute("authorizationForm") @Validated AuthorizationForm authorizationForm
    ) {
        Users user = usersService.loadUserByEmailAndPassword(authorizationForm.getEmail(), authorizationForm.getPassword());
        if (user != null) {
            if (user.isConfirmed()) {
                String token = jwtProvider.generateToken(user.getEmail());
                String date = usersService.getExpirationDate(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()));
                String userRole = usersService.getUserRoleByRoleID(user.getId()).substring(5).toLowerCase();
                String fio = user.getSurname() + " " + user.getName() + " " + user.getSecond_name();
                if (userRole.equals("")) {
                    return new AuthorizationResponseForm("Не удается установить роль пользователя");
                } else {
                    return new AuthorizationResponseForm(token, date, userRole, fio, "Все в порядке");
                }
            }
            else {
                return new AuthorizationResponseForm("Для авторизации необходимо подтвердить регистрацию аккаунта");
            }
        }
        else {
            return new AuthorizationResponseForm("Неверная комбинация логина и пароля");
        }
    }

    @PostMapping("/authorization/prolongation")
    public AuthorizationResponseForm AuthorizationProlongation(
            @ModelAttribute("authorizationForm")
            @Validated AuthorizationForm authorizationForm
            ) {
        Users user = usersService.loadUserByEmailAndPassword(authorizationForm.getEmail(),
                authorizationForm.getPassword());
        if (user != null) {
            String token = jwtProvider.generateToken(user.getEmail());
            String date = usersService.getExpirationDate(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()));
            return new AuthorizationResponseForm(token, date);
        }
        else {
            return new AuthorizationResponseForm("Неверная комбинация логина и пароля");
        }
    }
}