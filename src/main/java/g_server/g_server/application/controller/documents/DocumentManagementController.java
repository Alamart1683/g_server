package g_server.g_server.application.controller.documents;

import g_server.g_server.application.service.documents.DocumentManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class DocumentManagementController {
    @Autowired
    private DocumentManagementService documentManagementService;

    @DeleteMapping("/scientific_advisor/document/delete/")
    public List<String> deleteDocument(
        @RequestParam String documentName,
        @RequestParam String token
    ) {
        return documentManagementService.deleteDocument(documentName, token);
    }
}