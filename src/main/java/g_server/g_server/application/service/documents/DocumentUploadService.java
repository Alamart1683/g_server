package g_server.g_server.application.service.documents;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.entity.documents.DocumentKind;
import g_server.g_server.application.entity.documents.DocumentType;
import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.entity.forms.DocumentForm;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.repository.documents.DocumentKindRepository;
import g_server.g_server.application.repository.documents.DocumentRepository;
import g_server.g_server.application.repository.documents.DocumentTypeRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import g_server.g_server.application.service.documents.crud.DocumentService;
import g_server.g_server.application.service.documents.crud.DocumentVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentUploadService {
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private DocumentKindRepository documentKindRepository;

    @Autowired
    private DocumentVersionService documentVersionService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentService documentService;

    public List<String> UploadDocument(DocumentForm documentForm) {
        List<String> messagesList = new ArrayList<String>();
        // Определим айди научного руководителя
        Integer creator_id = null;
        if (documentForm.getToken() == null)
            messagesList.add("Ошибка аутентификации: токен равен null");
        if (messagesList.size() == 0)
            if (documentForm.getToken().equals(""))
                messagesList.add("Ошибка аутентификации: токен пуст");
            else
                creator_id = getCreatorId(documentForm.getToken());
        if (creator_id == null)
            messagesList.add("Пользователь не найден, загрузить файл невозможно");
        // Определим айди типа документа
        Integer type_id = getTypeId(documentForm.getDocumentFormType());
        if (type_id == null)
            messagesList.add("Указан несуществующий тип документа");
        // Определим айди вида документа
        Integer kind_id = getKindId(documentForm.getDocumentFormKind());
        if (kind_id == null)
            messagesList.add("Указан несуществующий вид докумета");
        // Проверим права доступа
        Integer viewRights = getViewRights(documentForm.getDocumentFormViewRights());
        if (viewRights == null) {
            messagesList.add("Указаны некорректные права доступа");
        }
        // Проверим что файл был загружен
        if (documentForm.getFile() == null)
            messagesList.add("Ошибка загрузки файла. Такого файла не существует");
        // Проверим корректное разрешение файла
        String fileExtension = getFileExtension(documentForm.getFile());
        if (fileExtension.length() == 0)
            messagesList.add("Попытка загрузить документ с некорректным разрешением");
        // После этого разместим файл на сервере
        if (messagesList.size() == 0) {
            // Создание директории документов научного руководителя
            String scientificAdvisorDocumentsPath = "src" + File.separator + "main" + File.separator + "resources" + File.separator +
                    "users_documents" + File.separator + creator_id;
            File scientificAdvisorDirectory = new File(scientificAdvisorDocumentsPath);
            if (!scientificAdvisorDirectory.exists()) {
                scientificAdvisorDirectory.mkdir();
            }
            // Создание директории версий файла
            String documentPath = scientificAdvisorDocumentsPath + File.separator + documentForm.getFile().getOriginalFilename();
            File documentDirectory = new File(documentPath);
            // Проверим что одноименный файл не был загружен пользователем
            if (!documentDirectory.exists()) {
                documentDirectory.mkdir();
            }
            else {
                messagesList.add("Файл с таким именем уже существует");
            }
            // Сохраним файл на сервере, создав необходимую директорию
            if (messagesList.size() == 0) {
                String currentDate = getCurrentDate();
                String versionPath = documentDirectory.getPath() + File.separator +
                        "version_" + currentDate + "." + fileExtension;
                Path uploadingFilePath = Paths.get(versionPath);
                try {
                    if (documentRepository.findByCreatorAndName(creator_id, documentForm.getFile().getOriginalFilename()) == null) {
                        if (multipartFileToFileWrite(documentForm.getFile(), uploadingFilePath)) {
                            // После этого занесем загруженный файл в таблицу документов
                            Document document = documentForm.DocumentFormToDocument(creator_id, documentPath, currentDate,
                                    type_id, kind_id, viewRights
                            );
                            documentService.save(document);
                            // Далее создадим запись о первой версии документа в таблице версий
                            int uploadingDocumentId = documentRepository.findByCreatorAndName(creator_id,
                                    documentForm.getFile().getOriginalFilename()).getId();
                            DocumentVersion documentVersion = new DocumentVersion(creator_id, uploadingDocumentId, currentDate,
                                    "Загрузка документа на сайт", versionPath);
                            documentVersionService.save(documentVersion);
                            messagesList.add("Документ был успешно загружен");
                        }
                        else {
                            messagesList.add("Непредвиденная ошибка загрузки файла");
                            if (documentDirectory.listFiles().length == 0) {
                                documentDirectory.delete();
                            }
                        }
                    }
                    else {
                        messagesList.add("Ошибка синхронизации файловой системы с базой данных");
                        if (documentDirectory.listFiles().length == 0) {
                            documentDirectory.delete();
                        }
                    }
                }
                catch (IOException ioException) {
                    messagesList.add("IOException");
                }

            }
            else {
                return messagesList;
            }
        }
        else {
            return messagesList;
        }
        return messagesList;
    }

    // Необходимо получить id пользователя-создателя документа из токена
    public Integer getCreatorId(String token) {
        String email = jwtProvider.getEmailFromToken(token);
        Users user = usersRepository.findByEmail(email);
        if (user != null) {
            return user.getId();
        }
        else {
            return null;
        }
    }

    // Необходимо получить id типа документа
    public Integer getTypeId(String type) {
        DocumentType documentType = documentTypeRepository.getDocumentTypeByType(type);
        if (documentType != null) {
            return documentType.getId();
        }
        else {
            return null;
        }
    }

    // Необходимо получить id вида документа
    public Integer getKindId(String kind) {
        DocumentKind documentKind = documentKindRepository.getDocumentKindByKind(kind);
        if (documentKind != null) {
            return documentKind.getId();
        }
        else {
            return null;
        }
    }

    // Необходимо опеределить корректность расширения файла
    public String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
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

    // Необходимо определить текущую дату
    public String getCurrentDate() {
        ZonedDateTime dateTime = ZonedDateTime.now();
        String completeDateTime = dateTime.getDayOfMonth() + "." + monthWordToMonthNumber(dateTime.getMonth().toString()) +
                "." + dateTime.getYear() + "." + dateTime.getHour() + "." + dateTime.getMinute() + "." + dateTime.getSecond() +
                "." + dateTime.getNano();
        completeDateTime = completeDateTime.substring(0, completeDateTime.lastIndexOf('.'));
        return completeDateTime;
    }

    // Необходимо конвертировать месяца в номера
    public String monthWordToMonthNumber(String monthWord) {
        switch (monthWord) {
            case "JANUARY":
                return "01";
            case "FEBRUARY":
                return "02";
            case "MARCH":
                return "03";
            case "APRIL":
                return "04";
            case "MAY":
                return "05";
            case "JUNE":
                return "06";
            case "JULY":
                return "07";
            case "AUGUST":
                return "08";
            case "SEPTEMBER":
                return "09";
            case "OCTOBER":
                return "10";
            case "NOVEMBER":
                return "11";
            case "DECEMBER":
                return "12";
            default:
                return "00";
        }
    }

    // Необходимо декодировать права просмотра
    public Integer getViewRights(String viewRights) {
        if (viewRights.equals("Только моим студентам")) {
            return 3;
        }
        return null;
    }

    // Необходимо конвертировать multipart file в file после чего записать его
    public boolean multipartFileToFileWrite(MultipartFile multipartFile, Path path) throws IOException {
        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(multipartFile.getBytes());
            return true;
        }
        catch (IOException ioException) {
            return false;
        }
    }
}