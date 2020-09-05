package g_server.g_server.application.controller.documents;

import g_server.g_server.application.service.documents.DocumentDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class DocumentDownloadController {
    @Autowired
    private DocumentDownloadService documentDownloadService;

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
}