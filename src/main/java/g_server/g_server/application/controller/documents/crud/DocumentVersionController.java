package g_server.g_server.application.controller.documents.crud;

import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.repository.documents.DocumentVersionRepository;
import g_server.g_server.application.service.documents.crud.DocumentVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;

// Админ может только ПРОСМАТРИВАТЬ и фильтровать все версии документов в таблице
// Реализовывать удаление нет смысла так как при удалении документа случится
// каскадное удаление и всех версий документа
// Для преподавателей необходимо сделать возможность удаления версии их документов
// которые им вдруг разонравились
@RestController
public class DocumentVersionController {
    private DocumentVersionService documentVersionService;
    private DocumentVersionRepository documentVersionRepository;

    @Autowired
    public void setDocumentVersionService(DocumentVersionService documentVersionService) {
        this.documentVersionService = documentVersionService;
    }

    @Autowired
    public void setDocumentVersionRepository(DocumentVersionRepository documentVersionRepository) {
        this.documentVersionRepository = documentVersionRepository;
    }

    @GetMapping("/admin/document/version/all")
    public List<DocumentVersion> findAll() {
        return documentVersionService.findAll();
    }

    @GetMapping("/admin/document/version/{id}")
    public Optional<DocumentVersion> findById(@PathVariable int id) {
        return documentVersionService.findById(id);
    }

    @GetMapping("/admin/document/version/by_document/{document}")
    public List<DocumentVersion> findByDocument(@PathVariable int document) {
        return documentVersionRepository.findByDocument(document);
    }

    @GetMapping("/admin/document/version/by_editor/{editor}")
    public List<DocumentVersion> findByEditor(@PathVariable int editor) {
        return documentVersionRepository.findByEditor(editor);
    }
}