package g_server.g_server.application.controller.users;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.forms.AuthorizationForm;
import g_server.g_server.application.entity.forms.AuthorizationResponseForm;
import g_server.g_server.application.entity.users.RefreshToken;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.repository.users.RefreshTokenRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import g_server.g_server.application.service.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import static org.springframework.util.StringUtils.hasText;

@RestController
public class AuthorizationController {
    @Autowired
    private UsersService usersService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UsersRepository usersRepository;

    public static final String AUTHORIZATION = "Authorization";

    @PostMapping("/authorization")
    public AuthorizationResponseForm AuthorizationResponse(
            @ModelAttribute("authorizationForm") @Validated AuthorizationForm authorizationForm
    ) {
        Users user = usersService.loadUserByEmailAndPassword(authorizationForm.getEmail(), authorizationForm.getPassword());
        if (user != null) {
            if (user.isConfirmed()) {
                // Сгенерируем refresh-токен
                long refreshIssue = java.time.Instant.now().getEpochSecond();
                long refreshExpire = java.time.Instant.now().getEpochSecond() + 5184000; // 60 суток
                String refreshToken = jwtProvider.generateRefreshToken(
                        user.getEmail(), user.getPassword(), refreshIssue, refreshExpire
                );
                // Сгенерируем access-токен
                long accessIssue = java.time.Instant.now().getEpochSecond();
                long accessExpire = java.time.Instant.now().getEpochSecond() + 172800; // 48 часов
                String accessToken = jwtProvider.generateAccessToken(
                        user.getEmail(), user.getPassword(), accessIssue, accessExpire
                );
                String userRole = usersService.getUserRoleByRoleID(user.getId()).substring(5).toLowerCase();
                String fio = user.getSurname() + " " + user.getName() + " " + user.getSecond_name();
                if (userRole.equals("")) {
                    return new AuthorizationResponseForm("Не удается установить роль пользователя");
                } else {
                    return new AuthorizationResponseForm(
                            accessToken,
                            accessIssue,
                            accessExpire,
                            refreshToken,
                            refreshIssue,
                            refreshExpire,
                            userRole,
                            fio,
                            "Все в порядке"
                    );
                }
            }
            else {
                return new AuthorizationResponseForm("Аккаунт не подтвержден");
            }
        }
        else {
            return new AuthorizationResponseForm("Неверный логин или пароль");
        }
    }

    @PostMapping("/authorization/prolongation")
    public AuthorizationResponseForm AuthorizationProlongation(HttpServletRequest httpServletRequest) {
        if (jwtProvider.validateRefreshToken(getTokenFromRequest(httpServletRequest))) {
            RefreshToken refreshToken =
                    refreshTokenRepository.findByRefreshToken(getTokenFromRequest(httpServletRequest));
            Users user;
            try {
                user = usersRepository.findById(refreshToken.getId()).get();
            } catch (NoSuchElementException noSuchElementException) {
                user = null;
            }
            if (user != null ) {
                // Удалим старый refresh-токен
                refreshTokenRepository.deleteById(refreshToken.getId());
                // Сгенерируем новый refresh-токен
                long refreshIssue = java.time.Instant.now().getEpochSecond();
                long refreshExpire = java.time.Instant.now().getEpochSecond() + 5184000; // 60 суток
                String newRefreshToken = jwtProvider.generateRefreshToken(
                        user.getEmail(), user.getPassword(), refreshIssue, refreshExpire
                );
                // Сгенерируем новый access-токен
                long accessIssue = java.time.Instant.now().getEpochSecond();
                long accessExpire = java.time.Instant.now().getEpochSecond() + 172800; // 48 часов
                String accessToken = jwtProvider.generateAccessToken(
                        user.getEmail(), user.getPassword(), accessIssue, accessExpire
                );
                AuthorizationResponseForm authorizationResponseForm = new AuthorizationResponseForm(
                        accessToken,
                        accessIssue,
                        accessExpire,
                        refreshToken.getRefreshToken(),
                        refreshIssue,
                        refreshExpire
                );
                return authorizationResponseForm;
            } else {
                return new AuthorizationResponseForm("Нет связи с сервером");
            }
        }
        else {
            return new AuthorizationResponseForm("Неверная комбинация логина и пароля, необходима авторизация");
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}