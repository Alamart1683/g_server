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
            @RequestParam String documentName,
            HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest
    ) {
        String token = getTokenFromRequest(httpServletRequest);
        // TODO Убрать костыли в скачивании документов
        File file = documentDownloadService.findDownloadDocument(documentName, token);
        String contentType = documentDownloadService.getContentType(file.getName());
        String mainName = documentDownloadService.getMainFileName(documentName, token);
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

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}