package g_server.g_server.application.controller.documents.crud;

import g_server.g_server.application.entity.documents.DocumentType;
import g_server.g_server.application.service.documents.crud.DocumentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

// Научный руководитель может выбирать тип документа при загрузке/изменении оного
// Админ может проводить полную модификацию таблицы типов документов
@RestController
public class DocumentTypeController {
    @Autowired
    private DocumentTypeService documentTypeService;

    @GetMapping("/document/type/all")
    public List<DocumentType> findAll() {
        return documentTypeService.findAll();
    }

    @GetMapping("/document/type/{id}")
    public Optional<DocumentType> findById(@PathVariable int id) {
        return documentTypeService.findByID(id);
    }

    @PostMapping("/admin/document/type/save/")
    public void save(@RequestParam String type) {
        documentTypeService.save(new DocumentType(type));
    }

    @PutMapping("/admin/document/type/update/")
    public void update(
            @RequestParam int id,
            @RequestParam String type
    ) {
        DocumentType documentType = documentTypeService.findByID(id).get();
        documentType.setType(type);
        documentTypeService.save(documentType);
    }

    @DeleteMapping("/admin/document/type/delete/{id}")
    public void delete(@PathVariable int id) {
        documentTypeService.delete(id);
    }
}