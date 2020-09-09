package g_server.g_server.application.controller.project;

import g_server.g_server.application.entity.forms.ProjectForm;
import g_server.g_server.application.service.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@RestController
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    public static final String AUTHORIZATION = "Authorization";

    @PostMapping("/scientific_advisor/project/add")
    public List<String> AddNewProject(
            @ModelAttribute("projectForm") @Validated ProjectForm projectForm,
            HttpServletRequest httpServletRequest) {
        return projectService.addProject(getTokenFromRequest(httpServletRequest), projectForm);
    }

    @DeleteMapping("/scientific_advisor/project/delete/{projectID}")
    public List<String> DeleteProject(
            HttpServletRequest httpServletRequest,
            @PathVariable Integer projectID) {
        return projectService.deleteProject(getTokenFromRequest(httpServletRequest), projectID);
    }

    @PutMapping("/scientific_advisor/project/rename/")
    public List<String> RenameProject(
            @RequestParam Integer projectID,
            @RequestParam String newName,
            HttpServletRequest httpServletRequest
    ) {
        return projectService.renameProject(getTokenFromRequest(httpServletRequest), projectID, newName);
    }

    @PutMapping("/scientific_advisor/project/change/description/")
    public List<String> ChangeDescription(
            @RequestParam Integer projectID,
            @RequestParam String newDescription,
            HttpServletRequest httpServletRequest
    ) {
        return projectService.changeProjectDescription(getTokenFromRequest(httpServletRequest),
                projectID, newDescription);
    }

    @PutMapping("/scientific_advisor/project/change/theme/")
    public List<String> ChangeTheme(
            @RequestParam Integer projectID,
            @RequestParam String newTheme,
            HttpServletRequest httpServletRequest
    ) {
        return projectService.changeProjectTheme(getTokenFromRequest(httpServletRequest), projectID, newTheme);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
