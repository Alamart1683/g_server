package g_server.g_server.application.service.project;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.forms.ProjectForm;
import g_server.g_server.application.entity.project.Project;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.repository.project.ProjectRepository;
import g_server.g_server.application.repository.project.ProjectThemeRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ProjectThemeRepository projectThemeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // Добавить новый проект
    public List<String> addProject(String token, ProjectForm projectForm) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("Научный руководитель не найден");
        }
        Integer themeID = projectThemeRepository.findByTheme(projectForm.getProjectTheme()).getId();
        if (themeID == null) {
            messageList.add("Тема не найдена");
        }
        List<Project> projects = projectRepository.findAllByScientificAdvisorID(advisorID);
        for (Project project: projects) {
            if (project.getName().equals(projectForm.getProjectName())) {
                messageList.add("Проект с таким именем уже существует");
                break;
            }
        }
        if (messageList.size() == 0) {
            projectRepository.save(
                    new Project(
                            themeID, projectForm.getProjectName(), advisorID,
                            projectForm.getProjectDescription()
                    )
            );
            messageList.add("Проект успешно добавлен");
        }
        return messageList;
    }

    // Удалить проект
    public List<String> deleteProject(String token, Integer projectID) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("Научный руководитель не найден");
        }
        if (projectID == null) {
            messageList.add("Проект не найден");
        }
        if (messageList.size() == 0) {
            Project project = projectRepository.findById(projectID).get();
            if (project.getScientificAdvisorID() != advisorID) {
                messageList.add("Вы не можете удалить проект другого научного рукводителя");
            }
            else {
                projectRepository.deleteById(projectID);
                messageList.add("Проект удален успешно");
            }
        }
        return messageList;
    }

    // Переименовать проект
    public List<String> renameProject(String token, Integer projectID, String newName) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("Научный руководитель не найден");
        }
        if (projectID == null) {
            messageList.add("Проект не найден");
        }
        Project renamingProject = projectRepository.findById(projectID).get();
        List<Project> projects = projectRepository.findAllByScientificAdvisorID(advisorID);
        for (Project project: projects) {
            if (project.getName().equals(newName)) {
                messageList.add("Данное имя проекта уже занято, переименование невозможно");
                break;
            }
        }
        if (renamingProject.getScientificAdvisorID() != advisorID) {
            messageList.add("Вы не можете переименовать проект другого научного рукводителя");
        }
        if (messageList.size() == 0) {
            renamingProject.setName(newName);
            projectRepository.save(renamingProject);
            messageList.add("Проект успешно переименован");
        }
        return messageList;
    }

    // Изменить описание проекта
    public List<String> changeProjectDescription(String token, Integer projectID, String newDescription) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("Научный руководитель не найден");
        }
        if (projectID == null) {
            messageList.add("Проект не найден");
        }
        Project project = projectRepository.findById(projectID).get();
        if (project.getScientificAdvisorID() != advisorID) {
            messageList.add("Вы не можете изменить описание проекта другого научного руководителя");
        }
        if (messageList.size() == 0) {
            project.setDescription(newDescription);
            projectRepository.save(project);
            messageList.add("Описание проекта успешно изменено");
        }
        return messageList;
    }

    // Изменить тему проекта
    public List<String> changeProjectTheme(String token, Integer projectID, String newTheme) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("Научный руководитель не найден");
        }
        if (projectID == null) {
            messageList.add("Проект не найден");
        }
        if (projectThemeRepository.findByTheme(newTheme) == null) {
            messageList.add("Тема не найдена");
        }
        Project project = projectRepository.findById(projectID).get();
        if (project.getScientificAdvisorID() != advisorID) {
            messageList.add("Вы не можете изменить тему проекта другого научного руководителя");
        }
        if (messageList.size() == 0) {
            project.setType(projectThemeRepository.findByTheme(newTheme).getId());
            projectRepository.save(project);
            messageList.add("Тема проекта успешно изменена");
        }
        return messageList;
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