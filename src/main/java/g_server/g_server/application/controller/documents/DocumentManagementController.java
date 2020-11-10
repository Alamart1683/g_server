package g_server.g_server.application.controller.documents;

import g_server.g_server.application.entity.forms.NewRightViewForm;
import g_server.g_server.application.entity.view.AdvisorShortTaskDataView;
import g_server.g_server.application.entity.view.ShortTaskDataView;
import g_server.g_server.application.service.documents.DocumentManagementService;
import g_server.g_server.application.service.documents.DocumentProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public String nirTaskCreateOrCangeByStudent(
            @ModelAttribute("shortTaskDataView") @Validated ShortTaskDataView shortTaskDataView,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        String response = documentProcessorService.studentTaskGeneration(
                getTokenFromRequest(httpServletRequest), shortTaskDataView);
        return response;
    }

    @PostMapping("/scientific_advisor/document/management/task/nir/update")
    public String nirTaskChangeByAdvisor(
            @ModelAttribute("advisorShortTaskDataView") @Validated AdvisorShortTaskDataView advisorShortTaskDataView,
            HttpServletRequest httpServletRequest
            ) throws Exception {
        String response = documentProcessorService.advisorTaskVersionAdd(
                getTokenFromRequest(httpServletRequest), advisorShortTaskDataView);
        return response;
    }

    @PostMapping("/student/document/management/task/nir/send")
    public String sentTaskToAdvisorByStudent(
        HttpServletRequest httpServletRequest,
        @RequestParam String newStatus,
        @RequestParam Integer versionID
    ) {
        return documentManagementService.studentSendingTask(
                getTokenFromRequest(httpServletRequest), newStatus, versionID);
    }

    @PostMapping("/student/document/management/report/nir/send")
    public String sentReportToAdvisorByStudent(
            HttpServletRequest httpServletRequest,
            @RequestParam String newStatus,
            @RequestParam Integer versionID
    ) {
        return documentManagementService.studentSendingReport(
                getTokenFromRequest(httpServletRequest), newStatus, versionID);
    }

    @PostMapping("/scientific_advisor/document/management/task/nir/check")
    public String checkTaskByAdvisor(
            HttpServletRequest httpServletRequest,
            @RequestParam String newStatus,
            @RequestParam Integer versionID
    ) {
        return documentManagementService.advisorCheckTask(
                getTokenFromRequest(httpServletRequest), newStatus, versionID);
    }

    @PostMapping("/scientific_advisor/document/management/report/nir/check")
    public String checkReportByAdvisor(
            HttpServletRequest httpServletRequest,
            @RequestParam String newStatus,
            @RequestParam Integer versionID
    ) {
        return documentManagementService.advisorCheckReport(
                getTokenFromRequest(httpServletRequest), newStatus, versionID);
    }

    @DeleteMapping("/student/document/task/version/delete")
    public String deleteTaskVersionByStudent(
            HttpServletRequest httpServletRequest,
            @RequestParam Integer versionID
    ) {
        return documentManagementService.studentDeleteTaskVersion(
                getTokenFromRequest(httpServletRequest), versionID);
    }

    @DeleteMapping("/scientific_advisor/document/task/version/delete")
    public String deleteTaskVersionByAdvisor(
            HttpServletRequest httpServletRequest,
            @RequestParam Integer versionID,
            @RequestParam Integer studentID
    ) {
        return documentManagementService.advisorDeleteTaskVersion(
                getTokenFromRequest(httpServletRequest), versionID, studentID);
    }

    @DeleteMapping("/student/document/report/version/delete")
    public String deleteReportVersionByStudent(
            HttpServletRequest httpServletRequest,
            @RequestParam Integer versionID
    ) {
        return documentManagementService.studentDeleteReportVersion(
                getTokenFromRequest(httpServletRequest), versionID);
    }

    @DeleteMapping("/scientific_advisor/document/report/version/delete")
    public String deleteReportVersionByAdvisor(
            HttpServletRequest httpServletRequest,
            @RequestParam Integer versionID,
            @RequestParam Integer studentID
    ) {
        return documentManagementService.advisorDeleteReportVersion(
                getTokenFromRequest(httpServletRequest), versionID, studentID);
    }

    @PostMapping("/head_of_cathedra/only/approve/template")
    public String approveTemplate(
        HttpServletRequest httpServletRequest,
        @RequestParam Integer documentID
    ) {
        return documentManagementService.approveTemplate(getTokenFromRequest(httpServletRequest), documentID);
    }

    @PostMapping("/head_of_cathedra/only/approve/order")
    public String approveOrder(
            HttpServletRequest httpServletRequest,
            @RequestParam Integer documentID
    ) {
        return documentManagementService.approveOrder(getTokenFromRequest(httpServletRequest), documentID);
    }
}