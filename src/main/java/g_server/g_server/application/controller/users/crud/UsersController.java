package g_server.g_server.application.controller.users.crud;

import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.query.response.PersonalAdvisorView;
import g_server.g_server.application.query.response.PersonalStudentView;
import g_server.g_server.application.repository.users.UsersRepository;
import g_server.g_server.application.service.users.ScientificAdvisorDataService;
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
    private UsersService usersService;
    private UsersRepository usersRepository;
    private ScientificAdvisorDataService scientificAdvisorDataService;

    @Autowired
    public void setUsersService(UsersService usersService) {
        this.usersService = usersService;
    }

    @Autowired
    public void setUsersRepository(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Autowired
    public void setScientificAdvisorDataService(ScientificAdvisorDataService scientificAdvisorDataService) {
        this.scientificAdvisorDataService = scientificAdvisorDataService;
    }

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
        Integer adminId = usersService.getUserId(token);
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

    @PutMapping("/scientific_advisor/users/change_places/{places}")
    public List<String> changePlaces(@PathVariable Integer places, HttpServletRequest httpServletRequest) {
        return scientificAdvisorDataService.changePlaces(getTokenFromRequest(httpServletRequest), places);
    }

    @GetMapping("/student/personal")
    public PersonalStudentView getStudentPersonalView(HttpServletRequest httpServletRequest) {
        return usersService.getPersonalStudentView(getTokenFromRequest(httpServletRequest));
    }

    @GetMapping("/scientific_advisor/personal")
    public PersonalAdvisorView getAdvisorPersonalView(HttpServletRequest httpServletRequest) {
        return usersService.getPersonalAdvisorView(getTokenFromRequest(httpServletRequest));
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}