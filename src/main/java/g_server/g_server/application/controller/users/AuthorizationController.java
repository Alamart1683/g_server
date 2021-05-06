package g_server.g_server.application.controller.users;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.query.request.AuthorizationForm;
import g_server.g_server.application.query.response.AuthorizationResponseForm;
import g_server.g_server.application.entity.users.RefreshToken;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.repository.users.RefreshTokenRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import g_server.g_server.application.service.users.UsersService;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import static org.springframework.util.StringUtils.hasText;

@RestController
public class AuthorizationController {
    private UsersService usersService;
    private JwtProvider jwtProvider;
    private RefreshTokenRepository refreshTokenRepository;
    private UsersRepository usersRepository;

    @Autowired
    public void setUsersService(UsersService usersService) {
        this.usersService = usersService;
    }

    @Autowired
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Autowired
    public void setRefreshTokenRepository(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Autowired
    public void setUsersRepository(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

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
                        authorizationForm.getEmail(), authorizationForm.getPassword(), refreshIssue, refreshExpire
                );
                // Сгенерируем access-токен
                long accessIssue = java.time.Instant.now().getEpochSecond();
                long accessExpire = java.time.Instant.now().getEpochSecond() + 172800; // 48 часов
                String accessToken = jwtProvider.generateAccessToken(
                        authorizationForm.getEmail(), authorizationForm.getPassword(), accessIssue, accessExpire
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
                            user.getEmail(),
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
                user = usersRepository.findById(refreshToken.getUserID()).get();
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
                String userRole = usersService.getUserRoleByRoleID(user.getId()).substring(5).toLowerCase();
                String fio = user.getSurname() + " " + user.getName() + " " + user.getSecond_name();
                AuthorizationResponseForm authorizationResponseForm = new AuthorizationResponseForm(
                        accessToken,
                        accessIssue,
                        accessExpire,
                        newRefreshToken,
                        refreshIssue,
                        refreshExpire,
                        userRole,
                        fio,
                        user.getEmail(),
                        "Пролонгация пройдена успешно"
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

    @GetMapping("/authorization/get/password/code")
    public String getPasswordChangeCode(
            HttpServletRequest httpServletRequest
    ) {
        return usersService.getChangeUserPasswordCode(getTokenFromRequest(httpServletRequest));
    }

    @PostMapping("/authorization/check/password/code/")
    public Boolean checkPasswordCode(
            @RequestParam Integer code
    ) {
        return usersService.isCodeEquals(code);
    }

    @PostMapping("/authorization/change/password/")
    public String changePassword(
            HttpServletRequest httpServletRequest,
            @RequestParam Integer code,
            @RequestParam String newPassword
    ) {
        return usersService.changeUserPassword(getTokenFromRequest(httpServletRequest), code, newPassword);
    }

    @GetMapping("/authorization/get/password/code/byemail")
    public String getPasswordChangeCodeByEmail(
            @RequestParam String email
    ) {
        return usersService.getChangeUserPasswordCodeByEmail(email);
    }

    @PostMapping("/authorization/change/password/byemail")
    public String changePasswordByEmail(
            @RequestParam Integer code,
            @RequestParam String newPassword,
            @RequestParam String email
    ) {
        return usersService.changeUserPasswordByEmail(email, code, newPassword);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}