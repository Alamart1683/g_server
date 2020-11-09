package g_server.g_server.application.service.documents;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.documents.*;
import g_server.g_server.application.entity.forms.AdvisorReportDocumentForm;
import g_server.g_server.application.entity.forms.DocumentForm;
import g_server.g_server.application.entity.forms.DocumentOrderForm;
import g_server.g_server.application.entity.forms.DocumentVersionForm;
import g_server.g_server.application.entity.project.ProjectArea;
import g_server.g_server.application.entity.system_data.Speciality;
import g_server.g_server.application.entity.users.AssociatedStudents;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.repository.documents.*;
import g_server.g_server.application.repository.project.ProjectAreaRepository;
import g_server.g_server.application.repository.system_data.SpecialityRepository;
import g_server.g_server.application.repository.users.AssociatedStudentsRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import g_server.g_server.application.repository.users.UsersRolesRepository;
import g_server.g_server.application.service.documents.crud.DocumentService;
import g_server.g_server.application.service.documents.crud.DocumentVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
// TODO Сделать проверку размера загружаемого файла
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

    @Autowired
    private DocumentVersionRepository documentVersionRepository;

    @Autowired
    private DocumentDownloadService documentDownloadService;

    @Autowired
    private ViewRightsRepository viewRightsRepository;

    @Autowired
    private ProjectAreaRepository projectAreaRepository;

    @Autowired
    private ViewRightsAreaRepository viewRightsAreaRepository;

    @Autowired
    private OrderPropertiesRepository orderPropertiesRepository;

    @Autowired
    private UsersRolesRepository usersRolesRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    @Autowired
    private DocumentProcessorService documentProcessorService;

    @Autowired
    private NirReportRepository nirReportRepository;

    @Autowired
    private AssociatedStudentsRepository associatedStudentsRepository;

    public void createDocumentRootDirIfIsNotExist() {
        String rootDocDirPath = "src" + File.separator + "main" +
                File.separator + "resources" + File.separator + "users_documents";
        File rootDocDir = new File(rootDocDirPath);
        if (!rootDocDir.exists()) {
            rootDocDir.mkdir();
        }
    }

    // TODO Подумать над тем, чтобы шаблон задания мог существовать строго один для одной фазы
    // TODO Также подумать над тем, чтобы одновременно для одной фазы и направления мог быть забит только
    // TODO Только один приказ
    // Метод загрузки документа
    public List<String> uploadDocument(DocumentForm documentForm) {
        createDocumentRootDirIfIsNotExist();
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
        String projectAreaName = documentForm.getProjectArea();
        // Проверим корректное разрешение файла
        String fileExtension = getFileExtension(documentForm.getFile());
        if (fileExtension.length() == 0)
            messagesList.add("Попытка загрузить документ с некорректным разрешением");
        // Проверим что файл был загружен
        if (documentForm.getFile() == null)
            messagesList.add("Ошибка загрузки файла. Такого файла не существует");
        if (viewRights == null) {
            messagesList.add("Указаны некорректные права доступа");
        }
        // Если зоны видимости не нулевые, проверим их
        if (messagesList.size() == 0) {
            if (viewRights == 6 && projectAreaName == null) {
                messagesList.add("Не указано имя проекта для добавления ему документа");
            }
            Integer roleID = usersRolesRepository.findUsersRolesByUserId(creator_id).getRoleId();
            if (roleID != 3 && viewRights == 5) {
                messagesList.add("Попытка загрузить документ с" +
                        " областью видимости доступной только заведующему кафедрой");
            }
        }
        // После этого разместим файл на сервере
        if (messagesList.size() == 0) {
            // Создание директории документов научного руководителя
            String scientificAdvisorDocumentsPath = "src" + File.separator + "main" +
                    File.separator + "resources" + File.separator + "users_documents" + File.separator + creator_id;
            File scientificAdvisorDirectory = new File(scientificAdvisorDocumentsPath);
            if (!scientificAdvisorDirectory.exists())
                scientificAdvisorDirectory.mkdir();
            // TODO Здесь сокрыт функционал для реализации разрешения версий файлов разных разрешений
            // TODO Получим имя файла без разрешения
            //String withoutExtension = documentForm.getFile().getOriginalFilename().substring(0,
            //        documentForm.getFile().getOriginalFilename().lastIndexOf('.'));
            // TODO Его можно ввести снова при необходимости, заменив имя файла на его версию без расширения
            String fileName = documentForm.getFile().getOriginalFilename();
            // Создание директории версий файла
            String documentPath = scientificAdvisorDocumentsPath + File.separator + fileName;
            File documentDirectory = new File(documentPath);
            // Проверим что одноименный файл не был загружен пользователем
            if (!documentDirectory.exists())
                documentDirectory.mkdir();
            else
                messagesList.add("Файл с таким именем уже существует");
            // Сохраним файл на сервере, создав необходимую директорию
            if (messagesList.size() == 0) {
                String currentDate = getCurrentDate();
                String sqlDateTime = convertRussianDateToSqlDateTime(currentDate);
                String versionPath = documentDirectory.getPath() + File.separator +
                        "version_" + currentDate + "." + fileExtension;
                Path uploadingFilePath = Paths.get(versionPath);
                try {
                    if (documentRepository.findByCreatorAndName(creator_id, fileName) == null) {
                        if (multipartFileToFileWrite(documentForm.getFile(), uploadingFilePath)) {
                            // После этого занесем загруженный файл в таблицу документов
                            Document document = documentForm.DocumentFormToDocument(creator_id, documentPath, sqlDateTime,
                                    type_id, kind_id, viewRights
                            );
                            documentService.save(document);
                            // Теперь если документ находится в поле видимости проектой области, запишем его в таблицу
                            // согласования документов с проектом, если что-то пошло не так, изменим поле видимости
                            // документа на видимость только создателю
                            if (viewRights == 6) {
                                ProjectArea projectArea;
                                try {
                                    projectArea = projectAreaRepository.findByAreaAndAdvisor(projectAreaName, creator_id);
                                } catch (NullPointerException nullPointerException) {
                                    projectArea = null;
                                }
                                if (projectArea == null) {
                                    Document changingDocument = documentRepository.findByCreatorAndName(creator_id, document.getName());
                                    changingDocument.setView_rights(1);
                                    documentService.save(changingDocument);
                                }
                                else {
                                    ViewRightsArea viewRightsArea = new ViewRightsArea();
                                    viewRightsArea.setArea(projectArea.getId());
                                    viewRightsArea.setDocument(document.getId());
                                    viewRightsAreaRepository.save(viewRightsArea);
                                }
                            }
                            // Далее создадим запись о первой версии документа в таблице версий
                            int uploadingDocumentId = documentRepository.findByCreatorAndName(creator_id, fileName).getId();
                            DocumentVersion documentVersion = new DocumentVersion(creator_id, uploadingDocumentId,
                                    sqlDateTime, "Загрузка документа на сайт", versionPath);
                            documentVersionService.save(documentVersion);
                            messagesList.add("Документ был успешно загружен");
                        }
                        else {
                            messagesList.add("Непредвиденная ошибка загрузки файла");
                            if (documentDirectory != null) {
                                if (documentDirectory.listFiles().length == 0) {
                                    documentDirectory.delete();
                                }
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

    // Метод загрузки версии документа
    public List<String> uploadDocumentVersion(DocumentVersionForm documentVersionForm) {
        List<String> messagesList = new ArrayList<String>();
        // Определим айди научного руководителя
        Integer editor_id = null;
        if (documentVersionForm.getToken() == null)
            messagesList.add("Ошибка аутентификации: токен равен null");
        if (messagesList.size() == 0)
            if (documentVersionForm.getToken().equals(""))
                messagesList.add("Ошибка аутентификации: токен пуст");
            else
                editor_id = getCreatorId(documentVersionForm.getToken());
        if (editor_id == null)
            messagesList.add("Пользователь не найден, загрузить версию файла невозможно");
        String fileExtension = getFileExtension(documentVersionForm.getVersionFile());
        if (fileExtension.equals(""))
            messagesList.add("Вы загружаете версию файла с недопустимым расширением");
        if (messagesList.size() == 0) {
            Document document = documentRepository.findByCreatorAndName(editor_id, documentVersionForm.getDocumentName());
            String currentDate = getCurrentDate();
            String sqlDateTime = convertRussianDateToSqlDateTime(currentDate);
            if (document != null) {
                if (!fileExtension.equals(documentDownloadService.getFileExtension(document.getName())))
                    messagesList.add("Запрещено загружать версию документа с иным разрешением," +
                            " для этого следует загрузить новый документ");
                if (messagesList.size() == 0) {
                    // На случай, если пользователь умудрился секунда в секунду загрузить две версии
                    DocumentVersion integrityController = documentVersionRepository.findByEditorAndDocumentAndEditionDate(editor_id,
                            document.getId(), sqlDateTime);
                    if (integrityController != null) {
                        try { Thread.sleep(3000); } catch (InterruptedException interruptedException) { }
                        currentDate = getCurrentDate(true);
                        sqlDateTime = convertRussianDateToSqlDateTime(currentDate);
                    }
                    String versionUploadPath = document.getDocument_path() + File.separator +
                            "version_" + currentDate + "." + fileExtension;
                    try {
                        if (multipartFileToFileWrite(documentVersionForm.getVersionFile(),Paths.get(versionUploadPath))) {
                            DocumentVersion documentVersion = new DocumentVersion(editor_id, document.getId(), sqlDateTime,
                                    documentVersionForm.getEditionDescription(), versionUploadPath
                            );
                            documentVersionService.save(documentVersion);
                            messagesList.add("Версия файла " + documentVersionForm.getDocumentName() + " была успешно загружена");
                        }
                        else {
                            messagesList.add("Произошла непредвиденная ошибка загрузки версии файла");
                        }
                    }
                    catch (IOException ioException) {
                        messagesList.add("IOException");
                    }
                }
            }
            else {
                messagesList.add("Загрузить новую версию документа невозможно, так как документ не был найден");
            }
        }
        return messagesList;
    }

    // Метод загрузки приказа с указанием его данных для заведующего кафедрой
    public List<String> uploadDocumentOrder(DocumentOrderForm documentOrderForm) {
        createDocumentRootDirIfIsNotExist();
        List<String> messagesList = new ArrayList<String>();
        Integer creator_id = null;
        if (documentOrderForm.getToken() == null)
            messagesList.add("Ошибка аутентификации: токен равен null");
        if (messagesList.size() == 0)
            if (documentOrderForm.getToken().equals(""))
                messagesList.add("Ошибка аутентификации: токен пуст");
            else
                creator_id = getCreatorId(documentOrderForm.getToken());
        if (creator_id == null)
            messagesList.add("Пользователь не найден, загрузить файл невозможно");
        // Определим айди типа документа
        Integer type_id = getTypeId(documentOrderForm.getDocumentFormType());
        if (type_id == null)
            messagesList.add("Указан несуществующий тип документа");
        // Определим айди вида документа
        Integer kind_id = getKindId(documentOrderForm.getDocumentFormKind());
        if (kind_id == null)
            messagesList.add("Указан несуществующий вид докумета");
        // Проверим права доступа
        Integer viewRights = getViewRights(documentOrderForm.getDocumentFormViewRights());
        String projectName = documentOrderForm.getProjectArea();
        if (viewRights == null) {
            messagesList.add("Указаны некорректные права доступа");
        }
        else if (viewRights == 6 && projectName == null) {
            messagesList.add("Не указано имя проекта для добавления ему документа");
        }
        // Проверим что файл был загружен
        if (documentOrderForm.getFile() == null)
            messagesList.add("Ошибка загрузки файла. Такого файла не существует");
        // Проверим корректное разрешение файла
        String fileExtension = getFileExtension(documentOrderForm.getFile());
        if (fileExtension.length() == 0)
            messagesList.add("Попытка загрузить документ с некорректным разрешением");
        // После этого разместим файл на сервере
        if (messagesList.size() == 0) {
            // Создание директории документов научного руководителя
            String scientificAdvisorDocumentsPath = "src" + File.separator + "main" + File.separator + "resources"
                    + File.separator + "users_documents" + File.separator + creator_id;
            File scientificAdvisorDirectory = new File(scientificAdvisorDocumentsPath);
            if (!scientificAdvisorDirectory.exists())
                scientificAdvisorDirectory.mkdir();
            // TODO Здесь сокрыт функционал для реализации разрешения версий файлов разных разрешений
            // TODO Получим имя файла без разрешения
            //String withoutExtension = documentForm.getFile().getOriginalFilename().substring(0,
            //        documentForm.getFile().getOriginalFilename().lastIndexOf('.'));
            // TODO Его можно ввести снова при необходимости, заменив имя файла на его версию без расширения
            String fileName = documentOrderForm.getFile().getOriginalFilename();
            // Создание директории версий файла
            String documentPath = scientificAdvisorDocumentsPath + File.separator + fileName;
            File documentDirectory = new File(documentPath);
            // Проверим что одноименный файл не был загружен пользователем
            if (!documentDirectory.exists())
                documentDirectory.mkdir();
            else
                messagesList.add("Файл с таким именем уже существует");
            // Проверим что код специальности декодируется
            Speciality speciality = specialityRepository.findByCode(documentOrderForm.getSpeciality());
            if (speciality == null)
                messagesList.add("Указан некорректный код специальности");
            // Сохраним файл на сервере, создав необходимую директорию
            if (messagesList.size() == 0) {
                String currentDate = getCurrentDate();
                String sqlDateTime = convertRussianDateToSqlDateTime(currentDate);
                String versionPath = documentDirectory.getPath() + File.separator +
                        "version_" + currentDate + "." + fileExtension;
                Path uploadingFilePath = Paths.get(versionPath);
                try {
                    if (documentRepository.findByCreatorAndName(creator_id, fileName) == null) {
                        if (multipartFileToFileWrite(documentOrderForm.getFile(), uploadingFilePath)) {
                            // После этого занесем загруженный файл в таблицу документов
                            Document document = documentOrderForm.DocumentFormToDocument(creator_id, documentPath, sqlDateTime,
                                    type_id, kind_id, viewRights
                            );
                            documentService.save(document);
                            // Сохраним данные, специфические именно для приказа
                            OrderProperties orderProperties = new OrderProperties(
                                    document.getId(),
                                    documentOrderForm.getNumber(),
                                    convertRussianDateToSqlDate(documentOrderForm.getOrderDate()),
                                    convertRussianDateToSqlDate(documentOrderForm.getStartDate()),
                                    convertRussianDateToSqlDate(documentOrderForm.getEndDate()),
                                    speciality.getId()
                            );
                            orderPropertiesRepository.save(orderProperties);
                            // Далее создадим запись о первой версии документа в таблице версий
                            int uploadingDocumentId = documentRepository.findByCreatorAndName(creator_id, fileName).getId();
                            DocumentVersion documentVersion = new DocumentVersion(creator_id, uploadingDocumentId,
                                    sqlDateTime, "Загрузка документа на сайт", versionPath);
                            documentVersionService.save(documentVersion);
                            messagesList.add("Документ был успешно загружен");
                        } else {
                            messagesList.add("Непредвиденная ошибка загрузки файла");
                            if (documentDirectory.listFiles().length == 0) {
                                documentDirectory.delete();
                            }
                        }
                    } else {
                        messagesList.add("Ошибка синхронизации файловой системы с базой данных");
                        if (documentDirectory.listFiles().length == 0) {
                            documentDirectory.delete();
                        }
                    }
                } catch (IOException ioException) {
                    messagesList.add("IOException");
                }
            } else {
                return messagesList;
            }
        } else {
            return messagesList;
        }
        return messagesList;
    }

    // Метод загрузки студентом отчёта по работе:
    public List<String> uploadStudentReport(DocumentForm documentForm) throws Exception {
        List<String> messagesList = new ArrayList<>();
        Integer creator_id = null;
        if (documentForm.getToken() == null) {
            messagesList.add("Ошибка аутентификации: токен равен null");
        }
        if (messagesList.size() == 0) {

        }
        if (documentForm.getToken().equals("")) {
            messagesList.add("Ошибка аутентификации: токен пуст");
        }
        else {
            creator_id = getCreatorId(documentForm.getToken());
        }
        if (creator_id == null) {
            messagesList.add("Пользователь не найден, загрузить файл невозможно");
        }
        Integer type_id = getTypeId(documentForm.getDocumentFormType());
        if (type_id == null) {
            messagesList.add("Указан несуществующий тип документа");
        }
        Integer kind_id = getKindId(documentForm.getDocumentFormKind());
        if (kind_id == null) {
            messagesList.add("Указан несуществующий вид докумета");
        }
        Integer viewRights = getViewRights(documentForm.getDocumentFormViewRights());
        if (viewRights == null) {
            messagesList.add("Указаны некорректные права доступа");
        }
        if (documentForm.getFile() == null) {
            messagesList.add("Ошибка загрузки файла. Такого файла не существует");
        }
        String fileExtension = getFileExtension(documentForm.getFile());
        if (fileExtension.length() == 0 || !fileExtension.equals("docx")) {
            messagesList.add("Попытка загрузить документ с некорректным разрешением");
        }
        Integer roleID = usersRolesRepository.findUsersRolesByUserId(creator_id).getRoleId();
        if (roleID != 1 && viewRights != 7) {
            messagesList.add("Попытка применить запрос на загрузку отчёта студентом не по назначению");
        }
        Users student;
        try {
            student = usersRepository.findById(creator_id).get();
        } catch (NoSuchElementException noSuchElementException) {
            student = null;
            messagesList.add("Пользователь не найден");
        }
        // После этого разместим файл на сервере
        if (messagesList.size() == 0) {
            String studentDocumentsPath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "users_documents" + File.separator + creator_id;
            File scientificAdvisorDirectory = new File(studentDocumentsPath);

            if (!scientificAdvisorDirectory.exists()) {
                scientificAdvisorDirectory.mkdir();
            }
            String fileName =
                    documentProcessorService.getShortFio(student.getSurname() + " " + student.getName() + " " + student.getSecond_name())
                            + " " + student.getStudentData().getStudentGroup().getStudentGroup() + " отчёт по " + documentForm.getDocumentFormType() + ".docx";
            String documentPath = studentDocumentsPath + File.separator + fileName;
            File documentDirectory = new File(documentPath);
            // Проверим что одноименный файл не был загружен пользователем
            if (!documentDirectory.exists()) {
                documentDirectory.mkdir();
            }
            // Сохраним файл на сервере, создав необходимую директорию
            if (messagesList.size() == 0) {
                String currentDate = getCurrentDate();
                String sqlDateTime = convertRussianDateToSqlDateTime(currentDate);
                String versionPath = documentDirectory.getPath() + File.separator +
                        "temp_version_" + currentDate + "." + fileExtension;
                Path uploadingFilePath = Paths.get(versionPath);
                File lastTaskVersionFile = null;
                try {
                    Integer reportType = documentProcessorService.determineType(documentForm.getDocumentFormType());
                    // Если тип отчтёта - НИР
                    if (reportType == 1) {
                        // Найдем последнюю версию задания, необходимую для загрузки отчёта
                        Document task;
                        List<DocumentVersion> taskVersions;
                        try {
                            task = documentRepository.findByTypeAndKindAndCreator(documentProcessorService.determineType(documentForm.getDocumentFormType()), 2, creator_id).get(0);
                            taskVersions = documentVersionRepository.findByDocument(task.getId());
                            List<DocumentVersion> approvedTaskVersions = new ArrayList<>();
                            for (DocumentVersion taskVersion: taskVersions) {
                                if (taskVersion.getNirTask().getDocumentStatus().getStatus().equals("Одобрено")) {
                                    approvedTaskVersions.add(taskVersion);
                                }
                            }
                            DocumentVersion lastTaskVersion = approvedTaskVersions.get(approvedTaskVersions.size() - 1);
                            lastTaskVersionFile = new File(lastTaskVersion.getThis_version_document_path());
                        } catch (NoSuchElementException noSuchElementException) {
                            messagesList.add("Не найдено одобренное задание");
                        } catch (Exception e) {
                            messagesList.add("При поиске последней версии задания произошло что-то необъяснимое");
                        }
                        // Если найдено задание последней версии, то присоединим к нему файл с содержанием отчёта и запишем как отчёт
                        if (documentRepository.findByCreatorAndName(creator_id, fileName) == null && messagesList.size() == 0) {
                            if (multipartFileToFileWrite(documentForm.getFile(), uploadingFilePath)) {
                                // После этого занесем загруженный файл в таблицу документов
                                Document document = documentForm.DocumentFormToDocument(creator_id, documentPath, sqlDateTime,
                                        type_id, kind_id, viewRights
                                );
                                document.setName(fileName);
                                documentService.save(document);
                                // Далее создадим запись о первой версии документа в таблице версий
                                int uploadingDocumentId = documentRepository.findByCreatorAndName(creator_id, fileName).getId();
                                DocumentVersion documentVersion = new DocumentVersion(creator_id, uploadingDocumentId,
                                        sqlDateTime, "Загрузка отчёта по " + documentForm.getDocumentFormType() + " на сайт", versionPath.replaceAll("temp_", ""));
                                documentVersionService.save(documentVersion);
                                NirReport nirReport = new NirReport(documentVersion.getId(), 1);
                                nirReportRepository.save(nirReport);
                                InputStream taskStream = new FileInputStream(lastTaskVersionFile);
                                File uploadedTempReportVersion = new File(versionPath);
                                InputStream reportStream = new FileInputStream(uploadedTempReportVersion);
                                File finalReportVersion = new File(versionPath.replaceAll("temp_", ""));
                                OutputStream destinationStream = new FileOutputStream(finalReportVersion);
                                documentProcessorService.makeUsWhole(taskStream, reportStream, destinationStream);
                                taskStream.close();
                                reportStream.close();
                                destinationStream.close();
                                uploadedTempReportVersion.delete();
                                messagesList.add("Отчёт по " + documentForm.getDocumentFormType() + " был успешно загружен");
                            } else {
                                messagesList.add("Непредвиденная ошибка загрузки файла");
                                if (documentDirectory != null) {
                                    if (documentDirectory.listFiles().length == 0) {
                                        documentDirectory.delete();
                                    }
                                }
                            }
                        // Если отчет уже был загружен в прошлый раз, добавим его новую версию
                        } else if (documentRepository.findByCreatorAndName(creator_id, fileName) != null) {
                            if (multipartFileToFileWrite(documentForm.getFile(), uploadingFilePath)) {
                                // Далее создадим запись о новой версии отчёта в таблице версий
                                int uploadingDocumentId = documentRepository.findByCreatorAndName(creator_id, fileName).getId();
                                DocumentVersion documentVersion = new DocumentVersion(creator_id, uploadingDocumentId,
                                        sqlDateTime, "Загрузка версии отчёта по " +
                                        documentForm.getDocumentFormType() + " на сайт",
                                        versionPath.replaceAll("temp_", ""));
                                documentVersionService.save(documentVersion);
                                messagesList.add("Версия отчёта по " + documentForm.getDocumentFormType() + " была успешно загружена");
                                NirReport nirReport = new NirReport(documentVersion.getId(), 1);
                                nirReportRepository.save(nirReport);
                            } else {
                                messagesList.add("Непредвиденная ошибка загрузки версии файла");
                                if (documentDirectory != null) {
                                    if (documentDirectory.listFiles().length == 0) {
                                        documentDirectory.delete();
                                    }
                                }
                            }
                        } else {
                            messagesList.add("Ошибка синхронизации файловой системы с базой данных");
                            if (documentDirectory.listFiles().length == 0) {
                                documentDirectory.delete();
                            }
                        }
                    }
                    // Если тип отчёта - Знания и умения
                    else if (reportType == 2) {
                        // TODO сделать загрузку отчёта
                    }
                    // Если тип отчёта - Преддиплом
                    else if (reportType == 3) {
                        // TODO сделать загрузку отчёта
                    }
                    // Если тип отчёта - ВКР
                    else if (reportType == 4) {
                        // TODO сделать загрузку отчёта
                    } else {
                        messagesList.add("Некорректный тип отчёта!");
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

    // Метод загрузки версии отчёта научным руководителем студенту
    public List<String> uploadAdvisorStudentReportVersion(AdvisorReportDocumentForm documentForm) {
        List<String> messagesList = new ArrayList<String>();
        Integer advisorID = null;
        if (documentForm.getToken() == null) {
            messagesList.add("Ошибка аутентификации: токен равен null");
        }
        if (messagesList.size() == 0) {
            if (documentForm.getToken().equals("")) {
                messagesList.add("Ошибка аутентификации: токен пуст");
            }
            else {
                advisorID = getCreatorId(documentForm.getToken());
            }
        }
        if (advisorID == null) {
            messagesList.add("Пользователь не найден, загрузить файл невозможно");
        }
        Integer type_id = getTypeId(documentForm.getDocumentFormType());
        if (type_id == null) {
            messagesList.add("Указан несуществующий тип документа");
        }
        Integer kind_id = getKindId(documentForm.getDocumentFormKind());
        if (kind_id == null) {
            messagesList.add("Указан несуществующий вид докумета");
        }
        Integer viewRights = getViewRights(documentForm.getDocumentFormViewRights());
        if (viewRights == null) {
            messagesList.add("Указаны некорректные права доступа");
        }
        if (documentForm.getFile() == null) {
            messagesList.add("Ошибка загрузки файла. Такого файла не существует");
        }
        String fileExtension = getFileExtension(documentForm.getFile());
        if (fileExtension.length() == 0) {
            messagesList.add("Попытка загрузить документ с некорректным разрешением");
        }
        Integer roleID = usersRolesRepository.findUsersRolesByUserId(advisorID).getRoleId();
        if ((roleID != 2 || roleID != 3) && viewRights != 7) {
            messagesList.add("Попытка применить запрос на загрузку версии отчёта" +
                    " руководителем студенту не по назначению");
        }
        Users advisor;
        Users student;
        try {
            advisor = usersRepository.findById(advisorID).get();
            student = usersRepository.findById(documentForm.getStudentID()).get();
        } catch (NoSuchElementException noSuchElementException) {
            advisor = null;
            student = null;
            messagesList.add("Пользователь не найден");
        }
        // После этого разместим файл на сервере
        if (messagesList.size() == 0) {
            String studentDocumentsPath = "src" + File.separator + "main" + File.separator + "resources" +
                    File.separator + "users_documents" + File.separator + student.getId();
            File scientificAdvisorDirectory = new File(studentDocumentsPath);
            if (!scientificAdvisorDirectory.exists()) {
                scientificAdvisorDirectory.mkdir();
            }
            String fileName =
                    documentProcessorService.getShortFio(student.getSurname() + " " +
                            student.getName() + " " + student.getSecond_name()) + " " +
                            student.getStudentData().getStudentGroup().getStudentGroup() +
                            " содержание отчёта по " + documentForm.getDocumentFormType() + ".docx";
            String documentPath = studentDocumentsPath + File.separator + fileName;
            File documentDirectory = new File(documentPath);
            // Проверим что одноименный файл не был загружен пользователем
            if (!documentDirectory.exists()) {
                messagesList.add("Вы не можете загрузить версию отчёта студента пока он его не загрузит");
                return messagesList;
            }
            // Сохраним файл на сервере, создав необходимую директорию
            if (messagesList.size() == 0) {
                String currentDate = getCurrentDate();
                String sqlDateTime = convertRussianDateToSqlDateTime(currentDate);
                String versionPath = documentDirectory.getPath() + File.separator +
                        "version_" + currentDate + "." + fileExtension;
                Path uploadingFilePath = Paths.get(versionPath);
                try {
                    Integer reportType = documentProcessorService.determineType(documentForm.getDocumentFormType());
                    // Если тип отчтёта - НИР
                    if (reportType == 1) {
                        if (documentRepository.findByCreatorAndName(student.getId(), fileName) == null) {
                            messagesList.add("Вы не можете загрузить версию отчёта студента пока он его не загрузит");
                            return messagesList;
                            // Если отчет уже был загружен в прошлый раз, добавим его новую версию
                        } else if (documentRepository.findByCreatorAndName(student.getId(), fileName) != null) {
                            if (multipartFileToFileWrite(documentForm.getFile(), uploadingFilePath)) {
                                AssociatedStudents associatedStudent = associatedStudentsRepository.findByScientificAdvisorAndStudent(advisorID, student.getId());
                                if (associatedStudent != null) {
                                    // Далее создадим запись о новой версии отчёта в таблице версий
                                    int uploadingDocumentId = documentRepository.findByCreatorAndName(student.getId(), fileName).getId();
                                    DocumentVersion documentVersion = new DocumentVersion(advisorID, uploadingDocumentId,
                                            sqlDateTime, "Загрузка версии отчёта по " +
                                            documentForm.getDocumentFormType() + " на сайт научным руководителем", versionPath);
                                    documentVersionService.save(documentVersion);
                                    messagesList.add("Версия отчёта по " + documentForm.getDocumentFormType() + " была успешно загружена");
                                    NirReport nirReport = new NirReport(documentVersion.getId(), 1);
                                    nirReportRepository.save(nirReport);
                                } else {
                                    messagesList.add("Разрешено загружать версии отчетов только своим студентам");
                                }
                            } else {
                                messagesList.add("Непредвиденная ошибка загрузки версии файла");
                                if (documentDirectory != null) {
                                    if (documentDirectory.listFiles().length == 0) {
                                        documentDirectory.delete();
                                    }
                                }
                            }
                        } else {
                            messagesList.add("Ошибка синхронизации файловой системы с базой данных");
                            if (documentDirectory.listFiles().length == 0) {
                                documentDirectory.delete();
                            }
                        }
                    }
                    // Если тип отчёта - Знания и умения
                    else if (reportType == 2) {
                        // TODO сделать загрузку отчёта
                    }
                    // Если тип отчёта - Преддиплом
                    else if (reportType == 3) {
                        // TODO сделать загрузку отчёта
                    }
                    // Если тип отчёта - ВКР
                    else if (reportType == 4) {
                        // TODO сделать загрузку отчёта
                    } else {
                        messagesList.add("Некорректный тип отчёта!");
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

    // TODO Сделать возможность студентам загружать документ в его проект

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
                extension.equals("xls") || extension.equals("xlsx") || extension.equals("xlsm") ||
                extension.equals("rar") || extension.equals("zip") || extension.equals("7z")) {
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
        String currentDay;
        if (dateTime.getDayOfMonth() < 10)
            currentDay = "0" + dateTime.getDayOfMonth();
        else
            currentDay = dateTime.getDayOfMonth() + "";
        String completeDateTime = currentDay + "." + monthWordToMonthNumber(dateTime.getMonth().toString()) +
                "." + dateTime.getYear() + ".";
        String currentHour;
        if (dateTime.getHour() < 10)
            currentHour = "0" + dateTime.getHour();
        else
            currentHour = dateTime.getHour() + "";
        String currentMinute;
        if (dateTime.getMinute() < 10)
            currentMinute = "0" + dateTime.getMinute();
        else
            currentMinute = dateTime.getMinute() + "";
        String currentSecond;
        if (dateTime.getSecond() < 10)
            currentSecond = "0" + dateTime.getSecond();
        else
            currentSecond = dateTime.getSecond() + "";
        completeDateTime = completeDateTime + currentHour + "." + currentMinute + "." + currentSecond;
        return completeDateTime;
    }

    // Защита целостности таблицы версий для одного документа
    public String getCurrentDate(boolean integrityFlag) {
        ZonedDateTime dateTime = ZonedDateTime.now().plusSeconds(1);
        String completeDateTime = dateTime.getDayOfMonth() + "." + monthWordToMonthNumber(dateTime.getMonth().toString()) +
                "." + dateTime.getYear() + "." + dateTime.getHour() + "." + dateTime.getMinute() + "." + dateTime.getSecond() +
                "." + dateTime.getNano();
        completeDateTime = completeDateTime.substring(0, completeDateTime.lastIndexOf('.'));
        return completeDateTime;
    }

    public String convertRussianDateToSqlDateTime(String russianDate) {
        String year = russianDate.substring(6, 10);
        String month = russianDate.substring(3, 5);
        String day = russianDate.substring(0, 2);
        List<Integer> dotIndexesList = new ArrayList<>();
        for (int i = 11; i < russianDate.length(); i++) {
            if (russianDate.charAt(i) == '.')
                dotIndexesList.add(i);
        }
        String hour = russianDate.substring(11, dotIndexesList.get(0));
        String minute = russianDate.substring(dotIndexesList.get(0) + 1, dotIndexesList.get(1));
        String second = russianDate.substring(dotIndexesList.get(1) + 1);
        if (hour.length() == 1)
            hour = '0' + hour;
        if (minute.length() == 1)
            minute = '0' + minute;
        if (second.length() == 1)
            second = '0' + second;
        return year + '-' + month + '-' + day + ' ' + hour + ':' + minute + ':' + second;
    }

    public String convertRussianDateToSqlDate(String russianDate) {
        String year = russianDate.substring(6, 10);
        String month = russianDate.substring(3, 5);
        String day = russianDate.substring(0, 2);
        return year + '-' + month + '-' + day;
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
        try {
            return viewRightsRepository.findByViewRight(viewRights).getId();
        } catch (NullPointerException nullPointerException) {
            return null;
        }
    }

    // Необходимо конвертировать multipart file в file после чего записать его
    private boolean multipartFileToFileWrite(MultipartFile multipartFile, Path path) throws IOException {
        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(multipartFile.getBytes());
            return true;
        }
        catch (IOException ioException) {
            return false;
        }
    }

    // Проверим что студент загрузил только один отчёт по НИРу
    // TODO Написать метод который при повторной загрузке существующего отчёта
    // TODO добавит версию к уже существующему документу
}