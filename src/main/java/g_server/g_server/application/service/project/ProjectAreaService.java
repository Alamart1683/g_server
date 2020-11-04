package g_server.g_server.application.service.project;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.documents.ViewRightsArea;
import g_server.g_server.application.entity.project.ProjectArea;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.repository.documents.ViewRightsAreaRepository;
import g_server.g_server.application.repository.project.ProjectRepository;
import g_server.g_server.application.repository.project.ProjectAreaRepository;
import g_server.g_server.application.repository.users.AssociatedStudentsRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectAreaService {
    @Autowired
    private ProjectAreaRepository projectAreaRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ViewRightsAreaRepository viewRightsAreaRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // Добавить новую тему
    public List<String> addProjectArea(String token, String area) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("ID научного руководителя не найден");
        }
        if (messageList.size() == 0) {
            ProjectArea isFree = projectAreaRepository.findByAreaAndAdvisor(area, advisorID);
            if (isFree != null) {
                messageList.add("Тема с таким именем уже существует");
            }
            else {
                projectAreaRepository.save(new ProjectArea(advisorID, area));
                messageList.add("Тема успешно добавлена");
            }
        }
        return messageList;
    }

    // Добавить список тем препода во время его первой аутентификации
    public List<String> addProjectAreas(String token, List<String> themes) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("ID научного руководителя не найден");
        }
        if (messageList.size() == 0) {
            for (String theme: themes) {
                ProjectArea newTheme = new ProjectArea(advisorID, theme);
                projectAreaRepository.save(newTheme);
            }
            messageList.add("Темы были успешно добавлены");
        }
        return messageList;
    }

    // Переименовать тему
    public List<String> changeProjectArea(String token, String oldTheme, String newTheme) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("ID научного руководителя не найден");
        }
        if (messageList.size() == 0) {
            ProjectArea changingArea = projectAreaRepository.findByAreaAndAdvisor(oldTheme, advisorID);
            if (changingArea != null) {
                ProjectArea isFree = projectAreaRepository.findByAreaAndAdvisor(newTheme, advisorID);
                if (isFree != null) {
                    messageList.add("Тема с таким именем уже существует");
                }
                else {
                    changingArea.setArea(newTheme);
                    projectAreaRepository.save(changingArea);
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
    public List<String> deleteProjectArea(String token, String area) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("ID научного руководителя не найден");
        }
        if (messageList.size() == 0) {
            ProjectArea deletingArea = projectAreaRepository.findByAreaAndAdvisor(area, advisorID);
            List<ViewRightsArea> viewRightsAreas = viewRightsAreaRepository.findAllByArea(deletingArea.getId());
            if (deletingArea != null && viewRightsAreas != null) {
                if (projectRepository.existsByArea(deletingArea.getId())) {
                    messageList.add("Невозможно удалить данную тему, так как она уже задействована проектом");
                }
                else if (viewRightsAreas.size() > 0) {
                    messageList.add("Невозможно удалить данную тему, так как она уже является областью видимости для документа");
                }
                else {
                    projectAreaRepository.deleteById(deletingArea.getId());
                    messageList.add("Тема успешно удалена");
                }
            }
            else {
                messageList.add("Удаляемая проектная область не найдена");
            }
        }
        return messageList;
    }

    // Получить список тем для научного руководителя
    public List<String> getAll(String token) {
        List<String> projectThemes = new ArrayList<>();
        List<ProjectArea> projectThemesRaw = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID != null) {
            projectThemesRaw = projectAreaRepository.findByAdvisor(advisorID);
            for (ProjectArea projectAreaRaw : projectThemesRaw) {
                projectThemes.add(projectAreaRaw.getArea());
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