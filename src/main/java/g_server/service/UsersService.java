package g_server.service;
import g_server.entity.Users;

// Интерфейс сервиса для таблицы пользователей
public interface UsersService {
    Users findByEmail(String email); // Поиск пользователя по логину
}
