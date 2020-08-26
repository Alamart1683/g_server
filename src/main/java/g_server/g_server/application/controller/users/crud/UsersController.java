package g_server.g_server.application.controller.users.crud;

import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.service.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

// Данный контроллер предназначен для администрирования пользователей админом
// Смена ролей пользователей даже здесь не допускается
// Пароли админ не видит и не меняет, это будет делать другой сервис
// В дальнейшем надо реализовать возможность корректироки данных студентов и научных руководителей здесь
@RestController
public class UsersController {
    @Autowired
    private UsersService usersService;

    @GetMapping("/admin/users/all")
    public List<Users> findAll() {
        return usersService.findAll();
    }

    @GetMapping("/admin/users/{id}")
    public Optional<Users> findById(@PathVariable int id) {
        return  usersService.findById(id);
    }

    @DeleteMapping("/admin/users/delete/{id}")
    public String delete(@PathVariable int id) {
        // Доделать чтобы админ не мог удалять себя и других админов
        // Например ввести надуровень root-admin
        usersService.nativeDelete(id);
        return "Пользователь удален успешно";
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
}