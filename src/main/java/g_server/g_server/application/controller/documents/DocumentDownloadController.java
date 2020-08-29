package g_server.g_server.application.controller.documents;

import g_server.g_server.application.service.documents.DocumentDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DocumentDownloadController {
    @Autowired
    private DocumentDownloadService documentDownloadService;

    @GetMapping("/document/download/")
    public void documentDownload(
            @RequestParam String documentName,
            @RequestParam String token,
            HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest
    ) {
        // TODO Сделать присвоение файлу адекватного имени
        // TODO Разобраться как вытащить тоек из HTTP запроса напрямую
        // httpServletRequest.getAuthType();
        File file = documentDownloadService.findDownloadDocument(documentName, token);
        String contentType = documentDownloadService.getContentType(file.getName());
        String extension = documentDownloadService.getFileExtension(file.getName());
        String mainName = documentDownloadService.getMainFileName(documentName, token);
        Path path = Paths.get(file.getPath());
        httpServletResponse.setContentType(contentType);
        httpServletResponse.addHeader("Content-Disposition", "attachment; filename=" + mainName + '.' + extension);
        try {
            Files.copy(path, httpServletResponse.getOutputStream());
            httpServletResponse.getOutputStream().flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}