package g_server.g_server.application.controller.users.crud;

import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.repository.users.UsersRepository;
import g_server.g_server.application.service.documents.DocumentUploadService;
import g_server.g_server.application.service.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;

// Данный контроллер предназначен для администрирования пользователей админом
// Смена ролей пользователей даже здесь не допускается
// Пароли админ не видит и не меняет, это будет делать другой сервис
// В дальнейшем надо реализовать возможность корректироки данных студентов и научных руководителей здесь
@RestController
public class UsersController {
    public static final String AUTHORIZATION = "Authorization";

    @Autowired
    private UsersService usersService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private DocumentUploadService documentUploadService;

    @GetMapping("/admin/users/all")
    public List<Users> findAll() {
        return usersService.findAll();
    }

    @GetMapping("/admin/users/{id}")
    public Optional<Users> findById(@PathVariable int id) {
        return  usersService.findById(id);
    }

    @DeleteMapping("/admin/users/delete/{id}")
    public String delete(
            @PathVariable int id,
            HttpServletRequest httpServletRequest) {
        String token = getTokenFromRequest(httpServletRequest);
        Integer adminId = documentUploadService.getCreatorId(token);
        if (usersService.checkUsersRoles(adminId, id)) {
            usersRepository.deleteById(id);
            return "Пользователь удален успешно";
        }
        else {
            return "Недостаточно прав для удаления данного пользователя " +
                    "или данный пользователь не существует";
        }
    }

    @PutMapping("/admin/users/change_email/")
    public String changeEmail(
            @RequestParam int id,
            @RequestParam String new_email
    ) {
        Users users = usersService.findById(id).get();
        if (users != null) {
            users.setEmail(new_email);
            return "Email успешно изменен";
        }
        else {
            return "Такого пользователя не существует";
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