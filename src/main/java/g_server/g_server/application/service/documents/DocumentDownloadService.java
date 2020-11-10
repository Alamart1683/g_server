package g_server.g_server.application.service.documents;

import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.repository.documents.DocumentRepository;
import g_server.g_server.application.repository.documents.DocumentVersionRepository;
import g_server.g_server.application.service.documents.crud.DocumentService;
import g_server.g_server.application.service.users.AssociatedStudentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.util.StringUtils.hasText;

@Service
public class DocumentDownloadService {
    @Value("${storage.location}")
    private String storageLocation;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentVersionRepository documentVersionRepository;

    @Autowired
    private DocumentProcessorService documentProcessorService;

    @Autowired
    private AssociatedStudentsService associatedStudentsService;

    // Метод скачивания документа последней версии
    public File findDownloadDocument(Integer creator_id, String documentName) {
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

    // Метод скачивания документа выбранной версии
    public File findDownloadDocumentVersion(Integer versionID) {
        if (versionID == null)
            return null;
        DocumentVersion documentVersion;
        try {
            documentVersion = documentVersionRepository.findById(versionID).get();
        } catch (NoSuchElementException noSuchElementException) {
            documentVersion = null;
        }
        if (documentVersion != null) {
            File downloadFile = new File(documentVersion.getThis_version_document_path());
            return downloadFile;
        }
        else {
            return null;
        }
    }

    public File studentDownloadReportVersionWithApprovedTask(String token, String stringType, Integer reportVersion) {
        Integer studentID = associatedStudentsService.getUserId(token);
        if (studentID != null) {
            Document task;
            List<DocumentVersion> taskVersions;
            try {
                task = documentRepository.findByTypeAndKindAndCreator(documentProcessorService.determineType(stringType), 2, studentID).get(0);
                taskVersions = documentVersionRepository.findByDocument(task.getId());
                List<DocumentVersion> approvedTaskVersions = new ArrayList<>();
                for (DocumentVersion taskVersion: taskVersions) {
                    if (taskVersion.getNirTask().getDocumentStatus().getStatus().equals("Одобрено")) {
                        approvedTaskVersions.add(taskVersion);
                    }
                }
                DocumentVersion lastTaskVersion = approvedTaskVersions.get(approvedTaskVersions.size() - 1);
                DocumentVersion lastReportVersion = documentVersionRepository.findById(reportVersion).get();
                File lastTaskVersionFile = new File(lastTaskVersion.getThis_version_document_path());
                File lastReportVersionFile = new File(lastReportVersion.getThis_version_document_path());
                File destinationFile = new File(storageLocation + File.separator + studentID + File.separator + "temp.docx");
                InputStream taskStream = new FileInputStream(lastTaskVersionFile);
                InputStream reportStream = new FileInputStream(lastReportVersionFile);
                OutputStream outputStream = new FileOutputStream(destinationFile);
                documentProcessorService.makeUsWhole(taskStream, reportStream, outputStream);
                return destinationFile;
            } catch (NoSuchElementException noSuchElementException) {
                return null;
            } catch (FileNotFoundException fileNotFoundException) {
                return null;
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public File advisorDownloadReportVersionWithApprovedTask(String token, String stringType, Integer reportVersion, Integer studentID) {
        Integer advisorID = associatedStudentsService.getUserId(token);
        if (advisorID != null && studentID != null) {
            Document task;
            List<DocumentVersion> taskVersions;
            try {
                task = documentRepository.findByTypeAndKindAndCreator(documentProcessorService.determineType(stringType), 2, studentID).get(0);
                taskVersions = documentVersionRepository.findByDocument(task.getId());
                List<DocumentVersion> approvedTaskVersions = new ArrayList<>();
                for (DocumentVersion taskVersion: taskVersions) {
                    if (taskVersion.getNirTask().getDocumentStatus().getStatus().equals("Одобрено")) {
                        approvedTaskVersions.add(taskVersion);
                    }
                }
                DocumentVersion lastTaskVersion = approvedTaskVersions.get(approvedTaskVersions.size() - 1);
                DocumentVersion lastReportVersion = documentVersionRepository.findById(reportVersion).get();
                File lastTaskVersionFile = new File(lastTaskVersion.getThis_version_document_path());
                File lastReportVersionFile = new File(lastReportVersion.getThis_version_document_path());
                File advisorDir = new File(storageLocation + File.separator + advisorID);
                if (!advisorDir.exists()) {
                    advisorDir.mkdir();
                }
                File destinationFile = new File(storageLocation + File.separator + advisorID + File.separator + "temp.docx");
                InputStream taskStream = new FileInputStream(lastTaskVersionFile);
                InputStream reportStream = new FileInputStream(lastReportVersionFile);
                OutputStream outputStream = new FileOutputStream(destinationFile);
                documentProcessorService.makeUsWhole(taskStream, reportStream, outputStream);
                return destinationFile;
            } catch (NoSuchElementException noSuchElementException) {
                return null;
            } catch (FileNotFoundException fileNotFoundException) {
                return null;
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
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
    public String getMainFileName(Integer creator_id, String documentName) {
        if (creator_id == null)
            return "";
        Document document = documentRepository.findByCreatorAndName(creator_id, documentName);
        if (document == null)
            return "";
        return document.getName();
    }


}