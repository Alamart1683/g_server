package g_server.g_server.application.controller.project;

import g_server.g_server.application.service.project.ProjectAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static org.springframework.util.StringUtils.hasText;

@RestController
public class ProjectThemeController {
    @Autowired
    private ProjectAreaService projectThemeService;

    @GetMapping("/scientific_advisor/project/theme/all")
    public List<String> getAll(HttpServletRequest httpServletRequest) {
        return projectThemeService.getAll(getTokenFromRequest(httpServletRequest));
    }

    @PostMapping("/scientific_advisor/project/theme/save/")
    public List<String> save(@RequestParam String theme, HttpServletRequest httpServletRequest) {
        return projectThemeService.addProjectTheme(getTokenFromRequest(httpServletRequest), theme);
    }

    @PostMapping("/scientific_advisor/project/theme/save/all")
    public List<String> saveAll(@RequestParam List<String> themes, HttpServletRequest httpServletRequest) {
        return projectThemeService.addProjectThemes(getTokenFromRequest(httpServletRequest), themes);
    }

    @PutMapping("/scientific_advisor/project/theme/update/")
    public List<String> update(
            @RequestParam String oldTheme,
            @RequestParam String newTheme,
            HttpServletRequest httpServletRequest) {
        return projectThemeService.changeProjectTheme(getTokenFromRequest(httpServletRequest), oldTheme, newTheme);
    }

    @DeleteMapping("/scientific_advisor/project/theme/delete/")
    public List<String> delete(@RequestParam String theme, HttpServletRequest httpServletRequest) {
        return projectThemeService.deleteProjectTheme(getTokenFromRequest(httpServletRequest), theme);
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