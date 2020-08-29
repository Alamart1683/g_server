package g_server.g_server.application.service.documents;

import org.apache.tomcat.jni.FileInfo;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.repository.documents.DocumentRepository;
import g_server.g_server.application.repository.documents.DocumentVersionRepository;
import g_server.g_server.application.service.documents.crud.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DocumentDownloadService {
    @Autowired
    private DocumentViewService documentViewService;

    @Autowired
    private DocumentDownloadService documentDownloadService;

    @Autowired
    private DocumentUploadService documentUploadService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentVersionRepository documentVersionRepository;

    // Метод поиска скачиваемого документа
    public File findDownloadDocument(String documentName, String token) {
        if (documentViewService.checkView(documentName, token)) {
            if (token == null)
                return null;
            if (token == "")
                return null;
            Integer creator_id = null;
            creator_id = documentUploadService.getCreatorId(token);
            if (creator_id == null)
                return null;
            Document document = documentRepository.findByCreatorAndName(creator_id, documentName);
            if (document != null) {
                List<DocumentVersion> documentVersions = documentVersionRepository.findByDocument(document.getId());
                DocumentVersion lastVersion = documentVersions.get(documentVersions.size() - 1);
                String path = lastVersion.getThis_version_document_path();
                File downloadFile = new File(path);
                return downloadFile;
            }
            else
                return null;
        }
        return null;
    }

    // Метод определения типа контента
    public String getContentType(String path) {
        String extension = getFileExtension(path);
        // TODO Сделать для всех типов контента аналогичные условия относительно типов их файлов
        if (extension.equals("docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        else {
            return "";
        }
    }

    // Необходимо опеределить корректность расширения файла
    public String getFileExtension(String path) {
        if (path.lastIndexOf(".") != -1 && path.lastIndexOf(".") != 0) {
            String extension = path.substring(path.lastIndexOf(".") + 1);
            if (extension.equals("docx") || extension.equals("pdf") || extension.equals("doc") ||
                    extension.equals("txt") || extension.equals("rtf") || extension.equals("ppt") ||
                    extension.equals("pptx") || extension.equals("csv") || extension.equals("jpg") ||
                    extension.equals("jpeg") || extension.equals("png") || extension.equals("webp") ||
                    extension.equals("xls") || extension.equals("xlsx") || extension.equals("xlsm")) {
                return extension;
            }
            else {
                return "";
            }
        }
        else {
            return "";
        }
    }

    // Метод получения основного имени файла
    public String getMainFileName(String documentName, String token) {
        if (token == null)
            return "";
        if (token == "")
            return "";
        Integer creator_id = null;
        creator_id = documentUploadService.getCreatorId(token);
        if (creator_id == null)
            return "";
        Document document = documentRepository.findByCreatorAndName(creator_id, documentName);
        if (document == null)
            return "";
        return document.getName();
    }
}