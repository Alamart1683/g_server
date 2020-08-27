package g_server.g_server.application.service.documents.crud;

import g_server.g_server.application.entity.documents.DocumentKind;
import g_server.g_server.application.repository.documents.DocumentKindRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentKindService {
    @Autowired
    private DocumentKindRepository documentKindRepository;

    public List<DocumentKind> findAll() {
        return documentKindRepository.findAll();
    }

    public Optional<DocumentKind> findByID(int id) {
        return documentKindRepository.findById(id);
    }

    public void save(DocumentKind documentKind) {
        documentKindRepository.save(documentKind);
    }

    public void delete(int id) {
        documentKindRepository.deleteById(id);
    }
}