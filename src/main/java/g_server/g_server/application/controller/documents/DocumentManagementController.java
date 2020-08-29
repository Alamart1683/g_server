package g_server.g_server.application.controller.documents;

import g_server.g_server.application.service.documents.DocumentManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static org.springframework.util.StringUtils.hasText;

@RestController
public class DocumentManagementController {
    @Autowired
    private DocumentManagementService documentManagementService;

    public static final String AUTHORIZATION = "Authorization";

    @DeleteMapping("/scientific_advisor/document/delete/")
    public List<String> deleteDocument(
        @RequestParam String documentName,
        HttpServletRequest httpServletRequest
    ) {
        return documentManagementService.deleteDocument(documentName, getTokenFromRequest(httpServletRequest));
    }

    @DeleteMapping("/scientific_advisor/document/delete/version/")
    public List<String> deleteDocumentVersion(
            @RequestParam String documentName,
            @RequestParam String documentEditionDate,
            HttpServletRequest httpServletRequest
    ) {
        return documentManagementService.deleteDocumentVersion(documentName, documentEditionDate,
                getTokenFromRequest(httpServletRequest));
    }

    @PutMapping("/scientific_advisor/document/rename")
    public List<String> RenameDocument(
        @RequestParam String oldDocumentName,
        @RequestParam String newDocumentName,
        HttpServletRequest httpServletRequest
    ) {
        return documentManagementService.renameDocument(oldDocumentName, newDocumentName,
                getTokenFromRequest(httpServletRequest));
    }

    @PutMapping("/scientific_advisor/document/change/description/")
    public List<String> changeDocumentDescription(
            @RequestParam String documentName,
            @RequestParam String newDescription,
            HttpServletRequest httpServletRequest
    ) {
        return documentManagementService.editDescription(documentName, newDescription,
                getTokenFromRequest(httpServletRequest));
    }

    @PutMapping("/scientific_advisor/document/change/type/")
    public List<String> changeDocumentType(
            @RequestParam String documentName,
            @RequestParam String newType,
            HttpServletRequest httpServletRequest
    ) {
        return documentManagementService.editType(documentName, newType, getTokenFromRequest(httpServletRequest));
    }

    @PutMapping("/scientific_advisor/document/change/kind/")
    public List<String> changeDocumentKind(
            @RequestParam String documentName,
            @RequestParam String newKind,
            HttpServletRequest httpServletRequest
    ) {
        return documentManagementService.editKind(documentName, newKind, getTokenFromRequest(httpServletRequest));
    }

    @PutMapping("/scientific_advisor/document/change/view_rights/")
    public List<String> changeDocumentViewRights(
            @RequestParam String documentName,
            @RequestParam String newViewRights,
            HttpServletRequest httpServletRequest
    ) {
        return documentManagementService.editViewRights(documentName, newViewRights, getTokenFromRequest(httpServletRequest));
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}