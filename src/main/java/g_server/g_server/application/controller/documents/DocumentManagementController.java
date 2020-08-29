package g_server.g_server.application.controller.documents;

import g_server.g_server.application.service.documents.DocumentManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @DeleteMapping("/scientific_advisor/document/delete/version/")
    public List<String> deleteDocumentVersion(
            @RequestParam String documentName,
            @RequestParam String documentEditionDate,
            @RequestParam String token
    ) {
        return documentManagementService.deleteDocumentVersion(documentName, documentEditionDate, token);
    }

    @PutMapping("/scientific_advisor/document/rename")
    public List<String> RenameDocument(
        @RequestParam String oldDocumentName,
        @RequestParam String newDocumentName,
        @RequestParam String token
    ) {
        return documentManagementService.renameDocument(oldDocumentName, newDocumentName, token);
    }

    @PutMapping("/scientific_advisor/document/change/description/")
    public List<String> changeDocumentDescription(
            @RequestParam String documentName,
            @RequestParam String newDescription,
            @RequestParam String token
    ) {
        return documentManagementService.editDescription(documentName, newDescription, token);
    }

    @PutMapping("/scientific_advisor/document/change/type/")
    public List<String> changeDocumentType(
            @RequestParam String documentName,
            @RequestParam String newType,
            @RequestParam String token
    ) {
        return documentManagementService.editType(documentName, newType, token);
    }

    @PutMapping("/scientific_advisor/document/change/kind/")
    public List<String> changeDocumentKind(
            @RequestParam String documentName,
            @RequestParam String newKind,
            @RequestParam String token
    ) {
        return documentManagementService.editKind(documentName, newKind, token);
    }

    @PutMapping("/scientific_advisor/document/change/view_rights/")
    public List<String> changeDocumentViewRights(
            @RequestParam String documentName,
            @RequestParam String newViewRights,
            @RequestParam String token
    ) {
        return documentManagementService.editViewRights(documentName, newViewRights, token);
    }
}