package g_server.g_server.application.controller.project;

import g_server.g_server.application.service.project.ProjectAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static org.springframework.util.StringUtils.hasText;

@RestController
public class ProjectAreaController {
    private ProjectAreaService projectThemeService;

    @Autowired
    public void setProjectThemeService(ProjectAreaService projectThemeService) {
        this.projectThemeService = projectThemeService;
    }

    @GetMapping("/scientific_advisor/project/area/all")
    public List<String> getAll(HttpServletRequest httpServletRequest) {
        return projectThemeService.getAll(getTokenFromRequest(httpServletRequest));
    }

    @PostMapping("/scientific_advisor/project/area/save/")
    public List<String> save(@RequestParam String area, HttpServletRequest httpServletRequest) {
        return projectThemeService.addProjectArea(getTokenFromRequest(httpServletRequest), area);
    }

    @PostMapping("/scientific_advisor/project/area/save/all")
    public List<String> saveAll(@RequestParam List<String> areas, HttpServletRequest httpServletRequest) {
        return projectThemeService.addProjectAreas(getTokenFromRequest(httpServletRequest), areas);
    }

    @PutMapping("/scientific_advisor/project/area/update/")
    public List<String> update(
            @RequestParam String oldArea,
            @RequestParam String newArea,
            HttpServletRequest httpServletRequest) {
        return projectThemeService.changeProjectArea(getTokenFromRequest(httpServletRequest), oldArea, newArea);
    }

    @DeleteMapping("/scientific_advisor/project/area/delete/")
    public List<String> delete(@RequestParam String area, HttpServletRequest httpServletRequest) {
        return projectThemeService.deleteProjectArea(getTokenFromRequest(httpServletRequest), area);
    }

    public static final String AUTHORIZATION = "Authorization";

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}