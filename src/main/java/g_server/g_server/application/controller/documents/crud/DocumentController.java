package g_server.g_server.application.controller.documents.crud;

import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.repository.documents.DocumentRepository;
import g_server.g_server.application.service.documents.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;

// Админ должен иметь возможность видеть все документы,
// сортировать и удалять их по своему усмотрению
@RestController
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentRepository documentRepository;

    @GetMapping("/admin/document/all")
    public List<Document> findAll() {
        return documentService.findAll();
    }

    @GetMapping("/admin/document/{id}")
    public Optional<Document> findById(@PathVariable int id) {
        return documentService.findByID(id);
    }

    @GetMapping("/admin/document/{creator_id}")
    public List<Document> findByCreatorId(@PathVariable int creator_id) {
        return documentRepository.findByCreator(creator_id);
    }

    @DeleteMapping("/admin/document/delete/{id}")
    public void delete(@PathVariable int id) {
        // Позже надо включить сюда удаление документов и из файловой системы
        // Удаление документа повлечет каскадное удаление всех его версий
        // Это тоже необходимо учесть, чтобы из ФС удалились и все версии документа
        documentService.delete(id);
    }
}
