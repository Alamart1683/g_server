package g_server.repository;
import g_server.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    Users findByEmail(String email); // Метод поиска по логину в таблице пользователей
}
