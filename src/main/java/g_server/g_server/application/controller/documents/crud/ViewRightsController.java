package g_server.g_server.application.controller.documents.crud;

import g_server.g_server.application.entity.documents.ViewRights;
import g_server.g_server.application.service.documents.crud.ViewRightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

// Админ имеет право просматривать и модифицировать таблицу прав доступа
// Преподавателю будет доступен выбор установления прав доступа при загрузке
// или изменении документа
@RestController
public class ViewRightsController {
    @Autowired
    private ViewRightsService viewRightsService;

    @GetMapping("/document/view_rights/all")
    public List<ViewRights> findAll() {
        return viewRightsService.findAll();
    }

    @GetMapping("/document/view_rights/{id}")
    public Optional<ViewRights> findById(@PathVariable int id) {
        return viewRightsService.findByID(id);
    }

    @PostMapping("/admin/document/view_rights/save/")
    public void save(
            @RequestParam boolean iofm,
            @RequestParam boolean iofsa,
            @RequestParam boolean iofms,
            @RequestParam boolean ifas,
            @RequestParam boolean ifa
    ) {
        ViewRights viewRights = new ViewRights(iofm, iofsa, iofms, ifas, ifa);
        viewRightsService.save(viewRights);
    }

    @PutMapping("/admin/document/view_rights/update/")
    public void update(
            @RequestParam int id,
            @RequestParam boolean iofm,
            @RequestParam boolean iofsa,
            @RequestParam boolean iofms,
            @RequestParam boolean ifas,
            @RequestParam boolean ifa
    ) {
        ViewRights viewRights = viewRightsService.findByID(id).get();
        viewRightsService.save(viewRights);
    }

    @DeleteMapping("/admin/document/view_rights/delete/{id}")
    public void delete(@PathVariable int id) {
        viewRightsService.delete(id);
    }
}