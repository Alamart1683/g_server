package g_server.g_server.application.service.project;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.forms.ProjectForm;
import g_server.g_server.application.entity.project.Project;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.repository.project.ProjectRepository;
import g_server.g_server.application.repository.project.ProjectAreaRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProjectService {
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ProjectAreaRepository projectAreaRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // Добавить новый проект
    public List<String> addProject(String token, ProjectForm projectForm) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("Научный руководитель не найден");
        }
        Integer areaID;
        try {
            areaID = projectAreaRepository.findByAreaAndAdvisor(projectForm.getProjectTheme(), advisorID).getId();
        } catch (Exception e) {
            areaID = null;
        }
        if (areaID == null) {
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
                            areaID, projectForm.getProjectName(), advisorID,
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
            messageList.add("ID проекта не найдено");
        }
        Project project = null;
        try { project = projectRepository.findById(projectID).get(); }
        catch (NoSuchElementException noSuchElementException) { messageList.add("Проект не найден"); }
        if (messageList.size() == 0) {
            if (project.getScientificAdvisorID() != advisorID) {
                messageList.add("Вы не можете удалить проект другого научного руководителя");
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
            messageList.add("ID проекта не найдено");
        }
        List<Project> projects = projectRepository.findAllByScientificAdvisorID(advisorID);
        for (Project project: projects) {
            if (project.getName().equals(newName)) {
                messageList.add("Данное имя проекта уже занято, переименование невозможно");
                break;
            }
        }
        Project renamingProject = null;
        try { renamingProject = projectRepository.findById(projectID).get(); }
        catch (NoSuchElementException noSuchElementException) { messageList.add("Проект не найден"); }
        if (messageList.size() == 0) {
            if (renamingProject.getScientificAdvisorID() != advisorID) {
                messageList.add("Вы не можете переименовать проект другого научного руководителя");
            }
            else {
                renamingProject.setName(newName);
                projectRepository.save(renamingProject);
                messageList.add("Проект успешно переименован");
            }
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
            messageList.add("ID проекта не найдено");
        }
        Project project = null;
        try { project = projectRepository.findById(projectID).get(); }
        catch (NoSuchElementException noSuchElementException) { messageList.add("Проект не найден"); }
        if (messageList.size() == 0) {
            if (project.getScientificAdvisorID() != advisorID) {
                messageList.add("Вы не можете изменить описание проекта другого научного руководителя");
            }
            else {
                project.setDescription(newDescription);
                projectRepository.save(project);
                messageList.add("Описание проекта успешно изменено");
            }
        }
        return messageList;
    }

    // Изменить тему проекта
    public List<String> changeProjectTheme(String token, Integer projectID, String newArea) {
        List<String> messageList = new ArrayList<>();
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            messageList.add("Научный руководитель не найден");
        }
        if (projectID == null) {
            messageList.add("Проект не найден");
        }
        if (projectAreaRepository.findByAreaAndAdvisor(newArea, advisorID) == null) {
            messageList.add("Тема не найдена");
        }
        Project project = null;
        try { project = projectRepository.findById(projectID).get(); }
        catch (NoSuchElementException noSuchElementException) { messageList.add("Проект не найден"); }
        if (messageList.size() == 0) {
            if (project.getScientificAdvisorID() != advisorID) {
                messageList.add("Вы не можете изменить тему проекта другого научного руководителя");
            }
            else {
                project.setArea(projectAreaRepository.findByAreaAndAdvisor(newArea, advisorID).getId());
                projectRepository.save(project);
                messageList.add("Тема проекта успешно изменена");
            }
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