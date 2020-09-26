package g_server.g_server.application.service.project;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.project.ProjectTheme;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.repository.project.ProjectRepository;
import g_server.g_server.application.repository.project.ProjectThemeRepository;
import g_server.g_server.application.repository.users.AssociatedStudentsRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectThemeService {
    @Autowired
    private ProjectThemeRepository projectThemeRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AssociatedStudentsRepository associatedStudentsRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // Добавить новую тему
    public List<String> addProjectTheme(String token, String theme) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("ID научного руководителя не найден");
        }
        if (messageList.size() == 0) {
            ProjectTheme isFree = projectThemeRepository.findByThemeAndAdvisor(theme, advisorID);
            if (isFree != null) {
                messageList.add("Тема с таким именем уже существует");
            }
            else {
                projectThemeRepository.save(new ProjectTheme(advisorID, theme));
                messageList.add("Тема успешно добавлена");
            }
        }
        return messageList;
    }

    // Добавить список тем препода во время его первой аутентификации
    public List<String> addProjectThemes(String token, List<String> themes) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("ID научного руководителя не найден");
        }
        if (messageList.size() == 0) {
            for (String theme: themes) {
                ProjectTheme newTheme = new ProjectTheme(advisorID, theme);
                projectThemeRepository.save(newTheme);
            }
            messageList.add("Темы были успешно добавлены");
        }
        return messageList;
    }

    // Переименовать тему
    public List<String> changeProjectTheme(String token, String oldTheme, String newTheme) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("ID научного руководителя не найден");
        }
        if (messageList.size() == 0) {
            ProjectTheme changingTheme = projectThemeRepository.findByThemeAndAdvisor(oldTheme, advisorID);
            if (changingTheme != null) {
                ProjectTheme isFree = projectThemeRepository.findByThemeAndAdvisor(newTheme, advisorID);
                if (isFree != null) {
                    messageList.add("Тема с таким именем уже существует");
                }
                else {
                    changingTheme.setTheme(newTheme);
                    projectThemeRepository.save(changingTheme);
                    messageList.add("Тема успешно переименована");
                }
            }
            else {
                messageList.add("Изменяемая тема не найдена");
            }
        }
        return messageList;
    }

    // Удалить тему
    public List<String> deleteProjectTheme(String token, String theme) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("ID научного руководителя не найден");
        }
        if (messageList.size() == 0) {
            ProjectTheme deletingTheme = projectThemeRepository.findByThemeAndAdvisor(theme, advisorID);
            if (deletingTheme != null) {
                if (projectRepository.existsByType(deletingTheme.getId()) ||
                        associatedStudentsRepository.existsByTheme(deletingTheme.getId())) {
                    messageList.add("Невозможно удалить данную тему, так как она уже задействована");
                }
                else {
                    projectThemeRepository.deleteById(deletingTheme.getId());
                    messageList.add("Тема успешно удалена");
                }
            }
            else {
                messageList.add("Удаляемая тема не найдена");
            }
        }
        return messageList;
    }

    // Получить список тем для научного руководителя
    public List<String> getAll(String token) {
        List<String> projectThemes = new ArrayList<>();
        List<ProjectTheme> projectThemesRaw = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID != null) {
            projectThemesRaw = projectThemeRepository.findByAdvisor(advisorID);
            for (ProjectTheme projectThemeRaw: projectThemesRaw) {
                projectThemes.add(projectThemeRaw.getTheme());
            }
        }
        return projectThemes;
    }

    // Получить айди из токена
    public Integer getUserId(String token) {
        // Проверка токена
        if (token == null) {
            return null;
        }
        if (token.equals("")) {
            return null;
        }
        String email = jwtProvider.getEmailFromToken(token);
        Users user = usersRepository.findByEmail(email);
        if (user != null) {
            return user.getId();
        }
        else {
            return null;
        }
    }
}