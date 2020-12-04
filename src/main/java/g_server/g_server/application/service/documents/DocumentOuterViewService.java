package g_server.g_server.application.service.documents;

import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.repository.documents.DocumentRepository;
import g_server.g_server.application.repository.documents.DocumentVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.List;

@Service
public class DocumentOuterViewService {
    @Value("${api.url}")
    private String apiUrl;

    public static List<UrlResource> urlResourcesList;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentVersionRepository documentVersionRepository;

    public String createDocumentUrlResource(Integer versionID) throws MalformedURLException {
        DocumentVersion documentVersion;
        if (documentVersionRepository.findById(versionID).isPresent()) {
            documentVersion = documentVersionRepository.findById(versionID).get();
            UrlResource urlResource = new UrlResource(apiUrl + documentVersion.getThis_version_document_path());
            urlResourcesList.add(urlResource);
            return "Успешно";
        }
        return "Ошибка";
    }
}
