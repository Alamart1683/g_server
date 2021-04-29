package g_server.g_server.application.service.documents.crud;

import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.repository.documents.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {
    private DocumentRepository documentRepository;

    @Autowired
    public void setDocumentRepository(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    public Optional<Document> findByID(int id) {
        return documentRepository.findById(id);
    }

    public void save(Document document) {
        documentRepository.save(document);
    }

    public void delete(int id) { documentRepository.deleteById(id); }
}