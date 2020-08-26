package g_server.g_server.application.service.documents;

import g_server.g_server.application.entity.documents.DocumentType;
import g_server.g_server.application.repository.documents.DocumentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentTypeService {
    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    public List<DocumentType> findAll() {
        return documentTypeRepository.findAll();
    }

    public Optional<DocumentType> findByID(int id) {
        return documentTypeRepository.findById(id);
    }

    public void save(DocumentType documentType) {
        documentTypeRepository.save(documentType);
    }

    public void delete(int id) {
        documentTypeRepository.deleteById(id);
    }
}