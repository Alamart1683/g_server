package g_server.g_server.application.controller.documents.crud;

import g_server.g_server.application.entity.documents.DocumentKind;
import g_server.g_server.application.service.documents.DocumentKindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
public class DocumentKindController {
    @Autowired
    private DocumentKindService documentKindService;

    @GetMapping("/document/kind/all")
    public List<DocumentKind> findAll() {
        return documentKindService.findAll();
    }

    @GetMapping("/document/kind/{id}")
    public Optional<DocumentKind> findById(@PathVariable int id) {
        return documentKindService.findByID(id);
    }

    @PostMapping("/admin/document/kind/save/")
    public void save(@RequestParam String kind) {
        documentKindService.save(new DocumentKind(kind));
    }

    @PutMapping("/admin/document/kind/update/")
    public void update(
            @RequestParam int id,
            @RequestParam String kind
    ) {
        DocumentKind documentKind = documentKindService.findByID(id).get();
        documentKind.setKind(kind);
        documentKindService.save(documentKind);
    }

    @DeleteMapping("/admin/document/kind/delete/{id}")
    public void delete(@PathVariable int id) {
        documentKindService.delete(id);
    }
}