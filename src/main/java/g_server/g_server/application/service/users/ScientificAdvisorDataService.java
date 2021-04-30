package g_server.g_server.application.service.users;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.users.ScientificAdvisorData;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.repository.users.ScientificAdvisorDataRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScientificAdvisorDataService {
    private ScientificAdvisorDataRepository scientificAdvisorDataRepository;
    private UsersRepository usersRepository;
    private JwtProvider jwtProvider;

    @Autowired
    public void setScientificAdvisorDataRepository(ScientificAdvisorDataRepository scientificAdvisorDataRepository) {
        this.scientificAdvisorDataRepository = scientificAdvisorDataRepository;
    }

    @Autowired
    public void setUsersRepository(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Autowired
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    public List<ScientificAdvisorData> findAll() {
        return scientificAdvisorDataRepository.findAll();
    }

    public Optional<ScientificAdvisorData> findById(int id) {
        return scientificAdvisorDataRepository.findById(id);
    }

    public void save(ScientificAdvisorData scientificAdvisorData) {
        scientificAdvisorDataRepository.save(scientificAdvisorData);
    }

    public void delete(int id) {
        scientificAdvisorDataRepository.deleteById(id);
    }

    // Метод изменения количества свободных мест научному руководителю
    public List<String> changePlaces(String token, Integer places) {
        List<String> messageList = new ArrayList<>();
        if (token == null) {
            messageList.add("Передан пустой токен");
        }
        if (token.equals("")) {
            messageList.add("Передана пустая строка вместо токена");
        }
        Users advisor = usersRepository.findByEmail(jwtProvider.getEmailFromToken(token));
        if (advisor == null) {
            messageList.add("Пользователь не найден");
        }
        if (places <= 0) {
            messageList.add("Указано недопустимое число мест");
        }
        if (messageList.size() == 0) {
            ScientificAdvisorData scientificAdvisorData = advisor.getScientificAdvisorData();
            scientificAdvisorData.setPlaces(places);
            save(scientificAdvisorData);
            messageList.add("Количество мест успешно изменено");
        }
        return messageList;
    }
}