package g_server.g_server.application.controller.project;

import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.entity.documents.DocumentKind;
import g_server.g_server.application.entity.project.ProjectTheme;
import g_server.g_server.application.repository.project.ProjectThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ProjectThemeController {
    @Autowired
    private ProjectThemeRepository projectThemeRepository;

    @GetMapping("/admin/project/theme/all")
    public List<ProjectTheme> findAll() {
        return projectThemeRepository.findAll();
    }

    @GetMapping("/admin/project/theme/{id}")
    public Optional<ProjectTheme> findById(@PathVariable int id) {
        return projectThemeRepository.findById(id);
    }

    @PostMapping("/admin/project/theme/save/")
    public void save(@RequestParam String theme) {
        projectThemeRepository.save(new ProjectTheme(theme));
    }

    @PutMapping("/admin/project/theme/update/")
    public void update(
            @RequestParam int id,
            @RequestParam String theme
    ) {
        ProjectTheme projectTheme = projectThemeRepository.findById(id).get();
        projectTheme.setTheme(theme);
        projectThemeRepository.save(projectTheme);
    }

    @DeleteMapping("/admin/project/theme/{id}")
    public void delete(@PathVariable int id) {
        projectThemeRepository.deleteById(id);
    }
}
