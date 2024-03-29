package g_server.g_server.application.service.documents.crud;

import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.repository.documents.DocumentVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentVersionService {
    private DocumentVersionRepository documentVersionRepository;

    @Autowired
    public void setDocumentVersionRepository(DocumentVersionRepository documentVersionRepository) {
        this.documentVersionRepository = documentVersionRepository;
    }

    public List<DocumentVersion> findAll() {
        return documentVersionRepository.findAll();
    }

    public Optional<DocumentVersion> findById(int id) {
        return documentVersionRepository.findById(id);
    }

    public void save(DocumentVersion documentVersion) {
        documentVersionRepository.save(documentVersion);
    }

    public void delete(int id) {
        documentVersionRepository.deleteById(id);
    }
}