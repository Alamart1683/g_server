package g_server.g_server.application.service.documents;

import org.springframework.stereotype.Service;

@Service
// Сервис ответственный за представление
// и разграничение документов пользователям
// TODO Здесь должен быть реализован сервис, который
// TODO позволит получать студентам и преподам те списки
// TODO документов, что им дозволено видеть
public class DocumentViewService {
    // Метод проверки может ли пользователь увидеть либо скачать файл
    public boolean checkView(String documentName, String token) {
        // TODO Сделать метод проверки видимости
        return true;
    }
}