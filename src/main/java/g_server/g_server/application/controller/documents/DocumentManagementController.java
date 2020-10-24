package g_server.g_server.application.controller.documents;

import g_server.g_server.application.entity.forms.NewRightViewForm;
import g_server.g_server.application.entity.view.ShortTaskDataView;
import g_server.g_server.application.service.documents.DocumentManagementService;
import g_server.g_server.application.service.documents.DocumentProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import static org.springframework.util.StringUtils.hasText;

@RestController
public class DocumentManagementController {
    @Autowired
    private DocumentManagementService documentManagementService;

    @Autowired
    private DocumentProcessorService documentProcessorService;

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
            @ModelAttribute("newThemeForm") @Validated NewRightViewForm newRightViewForm,
            HttpServletRequest httpServletRequest
    ) {
        return documentManagementService.editViewRights(
                newRightViewForm.getDocumentName(),
                newRightViewForm.getNewViewRights(),
                newRightViewForm.getProjectName(),
                getTokenFromRequest(httpServletRequest)
        );
    }

    @DeleteMapping("/student/document/delete/")
    public List<String> studentDeleteDocument(
            @RequestParam String documentName,
            HttpServletRequest httpServletRequest
    ) {
        return documentManagementService.deleteDocument(documentName, getTokenFromRequest(httpServletRequest));
    }

    @DeleteMapping("/student/document/delete/version/")
    public List<String> studentDeleteDocumentVersion(
            @RequestParam String documentName,
            @RequestParam String documentEditionDate,
            HttpServletRequest httpServletRequest
    ) {
        return documentManagementService.deleteDocumentVersion(documentName, documentEditionDate,
                getTokenFromRequest(httpServletRequest));
    }

    @PutMapping("/student/document/rename")
    public List<String> studentRenameDocument(
            @RequestParam String oldDocumentName,
            @RequestParam String newDocumentName,
            HttpServletRequest httpServletRequest
    ) {
        return documentManagementService.renameDocument(oldDocumentName, newDocumentName,
                getTokenFromRequest(httpServletRequest));
    }

    @PutMapping("/student/document/change/description/")
    public List<String> studentChangeDocumentDescription(
            @RequestParam String documentName,
            @RequestParam String newDescription,
            HttpServletRequest httpServletRequest
    ) {
        return documentManagementService.editDescription(documentName, newDescription,
                getTokenFromRequest(httpServletRequest));
    }

    @PutMapping("/student/document/change/type/")
    public List<String> studentChangeDocumentType(
            @RequestParam String documentName,
            @RequestParam String newType,
            HttpServletRequest httpServletRequest
    ) {
        return documentManagementService.editType(documentName, newType, getTokenFromRequest(httpServletRequest));
    }

    @PutMapping("/student/document/change/kind/")
    public List<String> studentChangeDocumentKind(
            @RequestParam String documentName,
            @RequestParam String newKind,
            HttpServletRequest httpServletRequest
    ) {
        return documentManagementService.editKind(documentName, newKind, getTokenFromRequest(httpServletRequest));
    }

    @PutMapping("/student/document/change/view_rights/")
    public List<String> studentChangeDocumentViewRights(
            @ModelAttribute("newThemeForm") @Validated NewRightViewForm newRightViewForm,
            HttpServletRequest httpServletRequest
    ) {
        return documentManagementService.editViewRights(
                newRightViewForm.getDocumentName(),
                newRightViewForm.getNewViewRights(),
                newRightViewForm.getProjectName(),
                getTokenFromRequest(httpServletRequest)
        );
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    @PostMapping("/student/document/management/task/nir/create")
    public String nirTaskCreate(
            @ModelAttribute("shortTaskDataView") @Validated ShortTaskDataView shortTaskDataView,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String response = documentProcessorService.studentShortTaskProcessing(
                getTokenFromRequest(httpServletRequest), shortTaskDataView);
        return response;
    }
}