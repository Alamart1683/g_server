package g_server.g_server.application.controller.documents;

import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.query.response.AcademicRecordDynamicForm;
import g_server.g_server.application.repository.documents.DocumentRepository;
import g_server.g_server.application.repository.documents.DocumentVersionRepository;
import g_server.g_server.application.service.documents.DocumentDownloadService;
import g_server.g_server.application.service.documents.DocumentProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.springframework.util.StringUtils.hasText;

@RestController
public class DocumentDownloadController {
    private DocumentDownloadService documentDownloadService;
    private DocumentProcessorService documentProcessorService;
    private DocumentVersionRepository documentVersionRepository;
    private DocumentRepository documentRepository;

    @Autowired
    public void setDocumentDownloadService(DocumentDownloadService documentDownloadService) {
        this.documentDownloadService = documentDownloadService;
    }

    @Autowired
    public void setDocumentProcessorService(DocumentProcessorService documentProcessorService) {
        this.documentProcessorService = documentProcessorService;
    }

    @Autowired
    public void setDocumentVersionRepository(DocumentVersionRepository documentVersionRepository) {
        this.documentVersionRepository = documentVersionRepository;
    }

    @Autowired
    public void setDocumentRepository(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public static final String AUTHORIZATION = "Authorization";

    @GetMapping("/document/download/")
    public void documentDownload(
            @RequestParam Integer creator_id,
            @RequestParam String documentName,
            HttpServletResponse httpServletResponse
    ) {
        File file = documentDownloadService.findDownloadDocument(creator_id, documentName);
        if (file != null) {
            String contentType = documentDownloadService.getContentType(file.getName());
            String mainName = documentDownloadService.getMainFileName(creator_id, documentName);
            Path path = Paths.get(file.getPath());
            httpServletResponse.setContentType(contentType);
            httpServletResponse.addHeader("Content-Disposition", "attachment; filename=" + mainName);
            try {
                Files.copy(path, httpServletResponse.getOutputStream());
                httpServletResponse.getOutputStream().flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @GetMapping("/document/download/version")
    public void documentVersionDownload(
            @RequestParam Integer versionID,
            HttpServletResponse httpServletResponse
    ) throws Exception {
        File file;
        if (checkIsTaskVersion(versionID)) {
            file = documentProcessorService.getThreePages(versionID);
        } else {
            file = documentDownloadService.findDownloadDocumentVersion(versionID);
        }
        if (file != null) {
            String contentType = documentDownloadService.getContentType(file.getName());
            String mainName = "Версия документа.docx";
            Path path = Paths.get(file.getPath());
            httpServletResponse.setContentType(contentType);
            httpServletResponse.addHeader("Content-Disposition", "attachment; filename=" + mainName);
            try {
                Files.copy(path, httpServletResponse.getOutputStream());
                httpServletResponse.getOutputStream().flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            if (file.getName().contains("temp")) {
                file.delete();
            }
        }
    }

    @GetMapping("/student/document/download/report")
    public void studentDownloadReport(
            @RequestParam String type,
            @RequestParam Integer reportVersion,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {
        // File reportFile = documentDownloadService.studentDownloadReportVersionWithApprovedTask(getTokenFromRequest(httpServletRequest), type, reportVersion);
        File reportFile = documentDownloadService.findDownloadDocumentVersion(reportVersion);
        if (reportFile != null) {
            String contentType = documentDownloadService.getContentType(reportFile.getName());
            String mainName = "Версия отчёта.docx";
            Path reportPath = Paths.get(reportFile.getPath());
            httpServletResponse.setContentType(contentType);
            httpServletResponse.addHeader("Content-Disposition", "attachment; filename=" + mainName);
            try {
                Files.copy(reportPath, httpServletResponse.getOutputStream());
                httpServletResponse.getOutputStream().flush();
                // reportFile.delete();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @GetMapping("/scientific_advisor/document/download/report")
    public void advisorDownloadReport(
            @RequestParam String type,
            @RequestParam Integer reportVersion,
            @RequestParam Integer studentID,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {
        // File reportFile = documentDownloadService.advisorDownloadReportVersionWithApprovedTask(getTokenFromRequest(httpServletRequest), type, reportVersion, studentID);
        File reportFile = documentDownloadService.findDownloadDocumentVersion(reportVersion);
        if (reportFile != null) {
            String contentType = documentDownloadService.getContentType(reportFile.getName());
            String mainName = "Версия отчёта.docx";
            Path reportPath = Paths.get(reportFile.getPath());
            httpServletResponse.setContentType(contentType);
            httpServletResponse.addHeader("Content-Disposition", "attachment; filename=" + mainName);
            try {
                Files.copy(reportPath, httpServletResponse.getOutputStream());
                httpServletResponse.getOutputStream().flush();
                // reportFile.delete();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @GetMapping("/head_of_cathedra/document/download/cathedta_report/")
    public void reportDownload(
            HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest,
            @RequestParam String studentKey,
            @RequestParam Integer stageKey
    ) throws Exception {
        File file = documentProcessorService.generateReportAboutAllActiveStudents(
                studentKey, getTokenFromRequest(httpServletRequest), stageKey);
        if (file != null) {
            String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            String mainName = "Отчёт об успеваемости студентов";
            Path path = Paths.get(file.getPath());
            httpServletResponse.setContentType(contentType);
            httpServletResponse.addHeader("Content-Disposition", "attachment; filename=" + mainName);
            try {
                Files.copy(path, httpServletResponse.getOutputStream());
                httpServletResponse.getOutputStream().flush();
                file.delete();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @PostMapping("/head_of_cathedra/document/download/dynamic_report/")
    public void dynamicReportDownload(
            //@ModelAttribute("dynamicForm") @Validated AcademicRecordDynamicForm dynamicForm,
            @RequestBody AcademicRecordDynamicForm dynamicForm,
            HttpServletResponse httpServletResponse
    ) throws Exception {
        File file = documentProcessorService.generateReportFromDynamicForm(dynamicForm);
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        String mainName = "Отчёт об успеваемости студентов";
        Path path = Paths.get(file.getPath());
        httpServletResponse.setContentType(contentType);
        httpServletResponse.addHeader("Content-Disposition", "attachment; filename=" + mainName);
        try {
            Files.copy(path, httpServletResponse.getOutputStream());
            httpServletResponse.getOutputStream().flush();
            file.delete();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private boolean checkIsTaskVersion(Integer versionID) {
        DocumentVersion documentVersion;
        if (documentVersionRepository.findById(versionID).isPresent()) {
            documentVersion = documentVersionRepository.findById(versionID).get();
            Document document;
            if (documentRepository.findById(documentVersion.getDocument()).isPresent()) {
                document = documentRepository.findById(documentVersion.getDocument()).get();
                if (document.getKind() == 2 && document.getType() != 4) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}