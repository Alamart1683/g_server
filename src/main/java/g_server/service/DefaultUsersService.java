package g_server.service;
import g_server.entity.Users;
import g_server.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DefaultUsersService implements UsersService {
    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Найти пользователя по email
    public Users findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Сохранить пользователя закодировав пароль
    public Users saveUser(Users users) {
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        return userRepository.save(users);
    }

    // Найти по логину и паролю
    public Users findByEmailAndPassword(String email, String password) {
        Users users = findByEmail(email);
        if (users != null) {
            if (passwordEncoder.matches(password, users.getPassword())) {
                return users;
            }
        }
        return null;
    }
}
