package g_server.g_server.application.controller.documents;

import g_server.g_server.application.entity.view.ShortTaskDataView;
import g_server.g_server.application.entity.view.TaskDataView;
import g_server.g_server.application.service.documents.DocumentDownloadService;
import g_server.g_server.application.service.documents.DocumentProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    @Autowired
    private DocumentDownloadService documentDownloadService;

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
    ) {
        File file = documentDownloadService.findDownloadDocumentVersion(versionID);
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
        }
    }

    @GetMapping("/student/document/download/report")
    public void studentDownloadReport(
            @RequestParam String type,
            @RequestParam Integer reportVersion,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {
        File reportFile = documentDownloadService.studentDownloadReportVersionWithApprovedTask(
                getTokenFromRequest(httpServletRequest), type, reportVersion);
        if (reportFile != null) {
            String contentType = documentDownloadService.getContentType(reportFile.getName());
            String mainName = "Версия отчёта.docx";
            Path reportPath = Paths.get(reportFile.getPath());
            httpServletResponse.setContentType(contentType);
            httpServletResponse.addHeader("Content-Disposition", "attachment; filename=" + mainName);
            try {
                Files.copy(reportPath, httpServletResponse.getOutputStream());
                httpServletResponse.getOutputStream().flush();
                reportFile.delete();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}