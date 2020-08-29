package g_server.g_server.application.service.documents;

import org.springframework.stereotype.Service;

@Service
// Сервис ответственный за представление
// и разграничение документов пользователям
public class DocumentViewService {
    // Метод проверки может ли пользователь увидеть либо скачать файл
    public boolean checkView(String documentName, String token) {
        return true;
    }
}