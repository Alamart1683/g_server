package g_server.g_server.application.service.documents;

import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.repository.documents.DocumentRepository;
import g_server.g_server.application.repository.documents.DocumentVersionRepository;
import g_server.g_server.application.service.documents.crud.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
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
            else {
                return null;
            }
        }
        return null;
    }

    // Метод определения типа контента
    public String getContentType(String path) {
        String extension = getFileExtension(path);
        switch(extension) {
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "doc":
                return "application/msword";
            case "pdf":
                return "application/pdf";
            case "rtf":
                return "application/rtf";
            case "txt":
                return "text/plain";
            case "ppt":
                return "application/mspowerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "csv":
                return "text/csv";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "xlsm":
                return "application/vnd.ms-excel.sheet.macroenabled.12";
            case "jpg":
                return "image/x-citrix-jpeg";
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "webp":
                return "image/webp";
            case "rar":
                return "application/x-rar-compressed";
            case "zip":
                return "application/zip";
            case "7z":
                return "application/x-7z-compressed";
            default:
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