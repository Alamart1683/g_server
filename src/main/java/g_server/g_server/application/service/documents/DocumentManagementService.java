package g_server.g_server.application.service.documents;

import g_server.g_server.application.entity.documents.*;
import g_server.g_server.application.entity.project.Project;
import g_server.g_server.application.entity.project.ProjectArea;
import g_server.g_server.application.entity.users.AssociatedStudents;
import g_server.g_server.application.query.response.StudentDocumentsStatusView;
import g_server.g_server.application.repository.documents.*;
import g_server.g_server.application.repository.project.ProjectAreaRepository;
import g_server.g_server.application.repository.project.ProjectRepository;
import g_server.g_server.application.repository.users.AssociatedStudentsRepository;
import g_server.g_server.application.service.documents.crud.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DocumentManagementService {
    private DocumentRepository documentRepository;
    private DocumentUploadService documentUploadService;
    private DocumentVersionRepository documentVersionRepository;
    private DocumentService documentService;
    private DocumentTypeRepository documentTypeRepository;
    private DocumentKindRepository documentKindRepository;
    private DocumentDownloadService documentDownloadService;
    private ViewRightsAreaRepository viewRightsAreaRepository;
    private NirTaskRepository nirTaskRepository;
    private AssociatedStudentsRepository associatedStudentsRepository;
    private NirReportRepository nirReportRepository;
    private ProjectRepository projectRepository;
    private ProjectAreaRepository projectAreaRepository;
    private TemplatePropertiesRepository templatePropertiesRepository;
    private OrderPropertiesRepository orderPropertiesRepository;
    private PpppuiopdReportRepository ppppuiopdReportRepository;
    private PdReportRepository pdReportRepository;
    private PpppuiopdTaskRepository ppppuiopdTaskRepository;
    private PdTaskRepository pdTaskRepository;
    private ViewRightsProjectRepository viewRightsProjectRepository;
    private VkrTaskRepository vkrTaskRepository;
    private VkrReportRepository vkrReportRepository;
    private VkrAntiplagiatRepository vkrAntiplagiatRepository;
    private VkrPresentationRepository vkrPresentationRepository;
    private VkrConclusionRepository vkrConclusionRepository;
    private VkrAllowanceRepository vkrAllowanceRepository;
    private DocumentProcessorService documentProcessorService;

    @Autowired
    public void setDocumentRepository(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Autowired
    public void setDocumentUploadService(DocumentUploadService documentUploadService) {
        this.documentUploadService = documentUploadService;
    }

    @Autowired
    public void setDocumentVersionRepository(DocumentVersionRepository documentVersionRepository) {
        this.documentVersionRepository = documentVersionRepository;
    }

    @Autowired
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Autowired
    public void setDocumentTypeRepository(DocumentTypeRepository documentTypeRepository) {
        this.documentTypeRepository = documentTypeRepository;
    }

    @Autowired
    public void setDocumentKindRepository(DocumentKindRepository documentKindRepository) {
        this.documentKindRepository = documentKindRepository;
    }

    @Autowired
    public void setDocumentDownloadService(DocumentDownloadService documentDownloadService) {
        this.documentDownloadService = documentDownloadService;
    }

    @Autowired
    public void setViewRightsAreaRepository(ViewRightsAreaRepository viewRightsAreaRepository) {
        this.viewRightsAreaRepository = viewRightsAreaRepository;
    }

    @Autowired
    public void setNirTaskRepository(NirTaskRepository nirTaskRepository) {
        this.nirTaskRepository = nirTaskRepository;
    }

    @Autowired
    public void setAssociatedStudentsRepository(AssociatedStudentsRepository associatedStudentsRepository) {
        this.associatedStudentsRepository = associatedStudentsRepository;
    }

    @Autowired
    public void setNirReportRepository(NirReportRepository nirReportRepository) {
        this.nirReportRepository = nirReportRepository;
    }

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    public void setProjectAreaRepository(ProjectAreaRepository projectAreaRepository) {
        this.projectAreaRepository = projectAreaRepository;
    }

    @Autowired
    public void setTemplatePropertiesRepository(TemplatePropertiesRepository templatePropertiesRepository) {
        this.templatePropertiesRepository = templatePropertiesRepository;
    }

    @Autowired
    public void setOrderPropertiesRepository(OrderPropertiesRepository orderPropertiesRepository) {
        this.orderPropertiesRepository = orderPropertiesRepository;
    }

    @Autowired
    public void setPpppuiopdReportRepository(PpppuiopdReportRepository ppppuiopdReportRepository) {
        this.ppppuiopdReportRepository = ppppuiopdReportRepository;
    }

    @Autowired
    public void setPdReportRepository(PdReportRepository pdReportRepository) {
        this.pdReportRepository = pdReportRepository;
    }

    @Autowired
    public void setPpppuiopdTaskRepository(PpppuiopdTaskRepository ppppuiopdTaskRepository) {
        this.ppppuiopdTaskRepository = ppppuiopdTaskRepository;
    }

    @Autowired
    public void setPdTaskRepository(PdTaskRepository pdTaskRepository) {
        this.pdTaskRepository = pdTaskRepository;
    }

    @Autowired
    public void setViewRightsProjectRepository(ViewRightsProjectRepository viewRightsProjectRepository) {
        this.viewRightsProjectRepository = viewRightsProjectRepository;
    }

    @Autowired
    public void setVkrTaskRepository(VkrTaskRepository vkrTaskRepository) {
        this.vkrTaskRepository = vkrTaskRepository;
    }

    @Autowired
    public void setVkrReportRepository(VkrReportRepository vkrReportRepository) {
        this.vkrReportRepository = vkrReportRepository;
    }

    @Autowired
    public void setVkrAntiplagiatRepository(VkrAntiplagiatRepository vkrAntiplagiatRepository) {
        this.vkrAntiplagiatRepository = vkrAntiplagiatRepository;
    }

    @Autowired
    public void setVkrPresentationRepository(VkrPresentationRepository vkrPresentationRepository) {
        this.vkrPresentationRepository = vkrPresentationRepository;
    }

    @Autowired
    public void setVkrConclusionRepository(VkrConclusionRepository vkrConclusionRepository) {
        this.vkrConclusionRepository = vkrConclusionRepository;
    }

    @Autowired
    public void setVkrAllowanceRepository(VkrAllowanceRepository vkrAllowanceRepository) {
        this.vkrAllowanceRepository = vkrAllowanceRepository;
    }

    @Autowired
    public void setDocumentProcessorService(DocumentProcessorService documentProcessorService) {
        this.documentProcessorService = documentProcessorService;
    }

    // Метод удаления документа вместе со всеми версиями
    public List<String> deleteDocument(String documentName, String token) {
        List<String> messagesList = new ArrayList<String>();
        if (token == null)
            messagesList.add("Ошибка аутентификации: токен равен null");
        if (token.equals("Ошибка аутентификации: токен пуст"))
            messagesList.add("");
        Integer creator_id = null;
        if (messagesList.size() == 0)
            creator_id = getDocumentUploadService().getCreatorId(token);
        if (creator_id == null)
            messagesList.add("Пользователь, загрузивший документ, не найден - удаление документа невозможно");
        if (messagesList.size() == 0) {
            Document document = getDocumentRepository().findByCreatorAndName(creator_id, documentName);
            // Если директория удаляемого документа существует, удалим его версии и саму директорию
            if (document != null) {
                File fileDirectory = new File(document.getDocumentPath());
                if (fileDirectory.exists()) {
                    for (File file: fileDirectory.listFiles()) {
                        file.delete();
                    }
                    fileDirectory.delete();
                    getDocumentRepository().deleteById(document.getId());
                    messagesList.add("Документ удален успешно вместе со всеми версиями");
                }
                else {
                    messagesList.add("Директория удаляемого документа не найдена или вы пытаетесь удалить чужой документ" +
                            " - удаление документа невозможно");
                }
            }
            else {
                messagesList.add("Удаляемый документ не найден - удаление документа невозможно");
            }
        }
        return messagesList;
    }

    // Метод удаления документа вместе со всеми версиями по типу
    public List<String> deleteDocumentVkrStuffByKind(String documentKind, String token) {
        List<String> messagesList = new ArrayList<String>();
        if (token == null)
            messagesList.add("Ошибка аутентификации: токен равен null");
        if (token.equals("Ошибка аутентификации: токен пуст"))
            messagesList.add("");
        Integer creator_id = null;
        Integer kind = getDocumentProcessorService().determineKind(documentKind);
        if (messagesList.size() == 0)
            creator_id = getDocumentUploadService().getCreatorId(token);
        if (creator_id == null)
            messagesList.add("Пользователь, загрузивший документ, не найден - удаление документа невозможно");
        if (messagesList.size() == 0) {
            Document document = getDocumentRepository().findByTypeAndKindAndCreator(4, kind, creator_id).get(0);
            // Если директория удаляемого документа существует, удалим его версии и саму директорию
            if (document != null) {
                File fileDirectory = new File(document.getDocumentPath());
                if (fileDirectory.exists()) {
                    for (File file: fileDirectory.listFiles()) {
                        file.delete();
                    }
                    fileDirectory.delete();
                    getDocumentRepository().deleteById(document.getId());
                    messagesList.add("Документ удален успешно вместе со всеми версиями");
                }
                else {
                    messagesList.add("Директория удаляемого документа не найдена или вы пытаетесь удалить чужой документ" +
                            " - удаление документа невозможно");
                }
            }
            else {
                messagesList.add("Удаляемый документ не найден - удаление документа невозможно");
            }
        }
        return messagesList;
    }

    // Удалить конкретную версию документа
    // Формат даты и времени вида ДД.ММ.ГГГГ.ЧЧ.ММ.СС
    public List<String> deleteDocumentVersion(String documentName, String editionDateTime, String token) {
        List<String> messagesList = new ArrayList<String>();
        if (token == null)
            messagesList.add("Ошибка аутентификации: токен равен null");
        if (token.equals("Ошибка аутентификации: токен пуст"))
            messagesList.add("");
        Integer creator_id = null;
        if (messagesList.size() == 0)
            creator_id = getDocumentUploadService().getCreatorId(token);
        if (creator_id == null)
            messagesList.add("Пользователь, загрузивший документ, не найден - удаление версии документа невозможно");
        if (messagesList.size() == 0) {
            Document document = getDocumentRepository().findByCreatorAndName(creator_id, documentName);
            // Если директория удаляемого документа существует, удалим его версию и саму директорию
            if (document != null) {
                File fileDirectory = new File(document.getDocumentPath());
                if (fileDirectory.exists()) {
                    List<DocumentVersion> documentVersions = getDocumentVersionRepository().findByDocument(document.getId());
                    if (documentVersions.size() != 0) {
                        if (documentVersions.size() == 1) {
                            if (collateDateTimes(documentVersions.get(0).getEditionDate(), editionDateTime)) {
                                String versionPath = documentVersions.get(0).getThis_version_document_path();
                                File file = new File(versionPath);
                                file.delete();
                                if (fileDirectory.listFiles().length == 0) {
                                    fileDirectory.delete();
                                }
                                getDocumentRepository().deleteById(document.getId());
                                messagesList.add("Документ был успешно удален вместе с последней его версией");
                            }
                            else {
                                messagesList.add("Удаляемая версия документа не найдена");
                            }
                        }
                        else if (documentVersions.size() > 1) {
                            for (DocumentVersion documentVersion : documentVersions) {
                                if (collateDateTimes(documentVersion.getEditionDate(), editionDateTime)) {
                                    File fileVersion = new File(documentVersion.getThis_version_document_path());
                                    fileVersion.delete();
                                    getDocumentVersionRepository().delete(documentVersion);
                                    messagesList.add("Версия документа удалена успешно");
                                    break;
                                }
                            }
                            if (messagesList.size() == 0) {
                                messagesList.add("Удаляемая версия документа не найдена");
                            }
                        }
                    }
                    else {
                        messagesList.add("Ошибка: Данный документ не существует");
                    }
                }
                else {
                    messagesList.add("Директория удаляемого документа не найдена или вы пытаетесь удалить чужой документ" +
                            " - удаление документа невозможно");
                }
            }
            else {
                messagesList.add("Удаляемый документ не найден - удаление версии документа невозможно");
            }
        }
        return messagesList;
    }

    // Переименовать документ
    public List<String> renameDocument(String oldDocumentName, String newDocumentName, String token) {
        List<String> messagesList = new ArrayList<String>();
        if (token == null)
            messagesList.add("Ошибка аутентификации: токен равен null");
        if (token.equals("Ошибка аутентификации: токен пуст"))
            messagesList.add("");
        Integer creator_id = null;
        if (messagesList.size() == 0)
            creator_id = getDocumentUploadService().getCreatorId(token);
        if (creator_id == null)
            messagesList.add("Пользователь, загрузивший документ, не найден - переименование документа невозможно");
        if (!getDocumentDownloadService().getFileExtension(newDocumentName).equals(getDocumentDownloadService().getFileExtension(oldDocumentName)))
            messagesList.add("Запрещено изменять расширение переименовываемого документа");
        if (messagesList.size() == 0) {
            Document document = getDocumentRepository().findByCreatorAndName(creator_id, oldDocumentName);
            // Если переименовываемый документ существует в базе данных, то переименуем его
            if (document != null) {
                File documentDirectory = new File(document.getDocumentPath());
                String newDocumentPath = document.getDocumentPath().substring(0,
                        document.getDocumentPath().lastIndexOf(File.separator) + 1) + newDocumentName;
                File newDocumentDirectoryName = new File(newDocumentPath);
                if (!newDocumentDirectoryName.exists()) {
                    documentDirectory.renameTo(newDocumentDirectoryName);
                    document.setDocumentPath(newDocumentPath);
                    document.setName(newDocumentName);
                    getDocumentService().save(document);
                    // Подкорректируем пути к версиям документа
                    List<DocumentVersion> versions = getDocumentVersionRepository().findByDocument(document.getId());
                    for (DocumentVersion version : versions) {
                        version.setThis_version_document_path(version.getThis_version_document_path().replace(
                                oldDocumentName, newDocumentName));
                    }
                    getDocumentVersionRepository().saveAll(versions);
                    messagesList.add("Документ переименован успешно");
                }
                else {
                    messagesList.add("Документ с таким именем уже существует");
                }
            }
            else {
                messagesList.add("Переименовываемый документ не существует");
            }
        }
        return messagesList;
    }

    // Необходимо корректно сопоставить дату и время из бд с полученными от пользователя
    public boolean collateDateTimes(String fromDB, String fromRequest) {
        fromRequest = getDocumentUploadService().convertRussianDateToSqlDateTime(fromRequest);
        return fromDB.equals(fromRequest);
    }

    // Метод изменения описания документа
    public List<String> editDescription(String documentName, String newDescription, String token) {
        List<String> messagesList = new ArrayList<String>();
        if (token == null)
            messagesList.add("Ошибка аутентификации: токен равен null");
        if (token.equals("Ошибка аутентификации: токен пуст"))
            messagesList.add("");
        Integer creator_id = null;
        if (messagesList.size() == 0)
            creator_id = getDocumentUploadService().getCreatorId(token);
        if (creator_id == null)
            messagesList.add("Пользователь, загрузивший документ, не найден - изменение описания документа невозможно");
        if (messagesList.size() == 0) {
            Document document = getDocumentRepository().findByCreatorAndName(creator_id, documentName);
            if (document != null) {
                document.setDescription(newDescription);
                getDocumentService().save(document);
                messagesList.add("Описание документа успешно изменено");
            }
            else {
                messagesList.add("Редактируемый документ не найден");
            }
        }
        return messagesList;
    }

    // Метод измнения вида документа
    public List<String> editType(String documentName, String newType, String token) {
        List<String> messagesList = new ArrayList<String>();
        if (token == null)
            messagesList.add("Ошибка аутентификации: токен равен null");
        if (token.equals("Ошибка аутентификации: токен пуст"))
            messagesList.add("");
        Integer creator_id = null;
        if (messagesList.size() == 0)
            creator_id = getDocumentUploadService().getCreatorId(token);
        if (creator_id == null)
            messagesList.add("Пользователь, загрузивший документ, не найден - изменение типа документа невозможно");
        if (messagesList.size() == 0) {
            if (getDocumentTypeRepository().getDocumentTypeByType(newType) != null) {
                Document document = getDocumentRepository().findByCreatorAndName(creator_id, documentName);
                if (document != null) {
                    document.setType(getDocumentTypeRepository().getDocumentTypeByType(newType).getId());
                    getDocumentService().save(document);
                    messagesList.add("Тип документа успешно изменен");
                }
                else {
                    messagesList.add("Редактируемый документ не найден");
                }
            }
            else {
                messagesList.add("Указан несуществующий тип документа");
            }
        }
        return messagesList;
    }

    // Метод изменения типа документа
    public List<String> editKind(String documentName, String newKind, String token) {
        List<String> messagesList = new ArrayList<String>();
        if (token == null)
            messagesList.add("Ошибка аутентификации: токен равен null");
        if (token.equals("Ошибка аутентификации: токен пуст"))
            messagesList.add("");
        Integer creator_id = null;
        if (messagesList.size() == 0)
            creator_id = getDocumentUploadService().getCreatorId(token);
        if (creator_id == null)
            messagesList.add("Пользователь, загрузивший документ, не найден - изменение вида документа невозможно");
        if (messagesList.size() == 0) {
            if (getDocumentKindRepository().getDocumentKindByKind(newKind) != null) {
                Document document = getDocumentRepository().findByCreatorAndName(creator_id, documentName);

                if (document != null) {
                    document.setKind(getDocumentKindRepository().getDocumentKindByKind(newKind).getId());
                    getDocumentService().save(document);
                    messagesList.add("Вид документа успешно изменен");
                }
                else {
                    messagesList.add("Редактируемый документ не найден");
                }
            }
            else {
                messagesList.add("Указан несуществующий вид документа");
            }
        }
        return messagesList;
    }

    // Метод изменения прав видимости документа
    public List<String> editViewRights(String documentName, String newViewRightsString, String projectName,
                                       String projectAreaName, String token) {
        List<String> messagesList = new ArrayList<String>();
        if (token == null)
            messagesList.add("Ошибка аутентификации: токен равен null");
        if (token.equals("Ошибка аутентификации: токен пуст"))
            messagesList.add("");
        Integer creator_id = null;
        if (messagesList.size() == 0)
            creator_id = getDocumentUploadService().getCreatorId(token);
        if (creator_id == null)
            messagesList.add("Пользователь, загрузивший документ, не найден - изменение зоны видимости документа невозможно");
        if (messagesList.size() == 0) {
            Integer newViewRights = getDocumentUploadService().getViewRights(newViewRightsString);
            if (newViewRights!= null) {
                Document document = getDocumentRepository().findByCreatorAndName(creator_id, documentName);
                if (document != null) {
                    // Если новая видимость - программа проектов
                    if (newViewRights == 6 && projectAreaName != null && projectName == null) {
                        ViewRightsProject oldViewRightsProject = getViewRightsProjectRepository().findByDocument(document.getId());
                        ViewRightsArea oldViewRightsArea = getViewRightsAreaRepository().findByDocument(document.getId());
                        // Удалим старую ассоциацию с проектом или программой проектов, если она есть
                        if (oldViewRightsArea != null) {
                            getViewRightsAreaRepository().delete(oldViewRightsArea);
                            messagesList.add("Удалены предыдущие права доступа для программы проектов");
                        }
                        if (oldViewRightsProject != null) {
                            getViewRightsProjectRepository().delete(oldViewRightsProject);
                            messagesList.add("Удалены предыдущие права доступа для проекта");
                        }
                        // Найдем новую программу проектов
                        ProjectArea projectArea = getProjectAreaRepository().findByAreaAndAdvisor(projectAreaName, creator_id);
                        if (projectArea != null) {
                            ViewRightsArea newViewRightsArea = new ViewRightsArea();
                            newViewRightsArea.setDocument(document.getId());
                            newViewRightsArea.setArea(projectArea.getId());
                            getViewRightsAreaRepository().save(newViewRightsArea);
                            document.setViewRightsInteger(6);
                            getDocumentService().save(document);
                            messagesList.add("Права доступа успешно изменены");
                        } else {
                            document.setViewRightsInteger(3);
                            getDocumentService().save(document);
                            messagesList.add("Произошла ошибка смены прав доступа: установлена видимость только создателю");
                        }
                    }
                    // Если новая видимость - проект
                    else if (newViewRights == 8 && projectAreaName == null && projectName != null) {
                        ViewRightsProject oldViewRightsProject = getViewRightsProjectRepository().findByDocument(document.getId());
                        ViewRightsArea oldViewRightsArea = getViewRightsAreaRepository().findByDocument(document.getId());
                        // Удалим старую ассоциацию с проектом или программой проектов, если она есть
                        if (oldViewRightsArea != null) {
                            getViewRightsAreaRepository().delete(oldViewRightsArea);
                            messagesList.add("Удалены предыдущие права доступа для программы проектов");
                        }
                        if (oldViewRightsProject != null) {
                            getViewRightsProjectRepository().delete(oldViewRightsProject);
                            messagesList.add("Удалены предыдущие права доступа для проекта");
                        }
                        // Найдем новый проект
                        Project project = getProjectRepository().findByScientificAdvisorIDAndName(creator_id, projectName);
                        if (project != null) {
                            ViewRightsProject newViewRightsProject = new ViewRightsProject();
                            newViewRightsProject.setDocument(document.getId());
                            newViewRightsProject.setProject(project.getId());
                            getViewRightsProjectRepository().save(newViewRightsProject);
                            document.setViewRightsInteger(8);
                            getDocumentService().save(document);
                            messagesList.add("Права доступа успешно изменены");
                        } else {
                            document.setViewRightsInteger(3);
                            getDocumentService().save(document);
                            messagesList.add("Произошла ошибка смены прав доступа: установлена видимость только создателю");
                        }
                    }
                    // Изменение зоны видимости без затрагивания проекта или программы проектов
                    else {
                        ViewRightsProject oldViewRightsProject = getViewRightsProjectRepository().findByDocument(document.getId());
                        ViewRightsArea oldViewRightsArea = getViewRightsAreaRepository().findByDocument(document.getId());
                        // Удалим старую ассоциацию с проектом или программой проектов, если она есть
                        if (oldViewRightsArea != null) {
                            getViewRightsAreaRepository().delete(oldViewRightsArea);
                            messagesList.add("Удалены предыдущие права доступа для программы проектов");
                        }
                        if (oldViewRightsProject != null) {
                            getViewRightsProjectRepository().delete(oldViewRightsProject);
                            messagesList.add("Удалены предыдущие права доступа для проекта");
                        }
                        document.setViewRightsInteger(newViewRights);
                        getDocumentService().save(document);
                        messagesList.add("Зона видимости документа успешно изменена");
                    }
                }
                else {
                    messagesList.add("Редактируемый документ не найден");
                }
            }
            else {
                messagesList.add("Указан несуществующий вид прав видимости");
            }
        }
        return messagesList;
    }

    // Студент отправляет версию задания научному руководителю
    public String studentSendingTask(String token, String newStatus, Integer versionID) {
        if (newStatus.equals("Рассматривается")) {
            Integer studentID = getDocumentUploadService().getCreatorId(token);
            if (studentID != null && versionID != null) {
                DocumentVersion documentVersion;
                try {
                    documentVersion = getDocumentVersionRepository().findById(versionID).get();
                } catch (NoSuchElementException noSuchElementException) {
                    documentVersion = null;
                }
                if (documentVersion != null) {
                    Document document = getDocumentRepository().findById(documentVersion.getDocument()).get();
                    int type = document.getType();
                    switch (type) {
                        case 1:
                            documentVersion.getNirTask().setStatus(4);
                            getNirTaskRepository().save(documentVersion.getNirTask());
                            break;
                        case 2:
                            documentVersion.getPpppuiopdTask().setStatus(4);
                            getPpppuiopdTaskRepository().save(documentVersion.getPpppuiopdTask());
                            break;
                        case 3:
                            documentVersion.getPdTask().setStatus(4);
                            getPdTaskRepository().save(documentVersion.getPdTask());
                            break;
                        case 4:
                            documentVersion.getVkrTask().setVkr_status(4);
                            getVkrTaskRepository().save(documentVersion.getVkrTask());
                            break;
                        default:
                            return "Некорректно указан этап";
                    }

                    return "Версия документа успешно отправлена";
                } else {
                    return "Версия документа не найдена";
                }
            } else {
                return "ID студента или версии не найден";
            }
        } else {
            return "Неверный параметр статуса. Невозможно отправить задание";
        }
    }

    // Студент отправляет версию остальных документов по вкр научному руководителю
    public String studentSendingVkrStuff(String token, String newStatus, Integer versionID) {
        if (newStatus.equals("Рассматривается")) {
            Integer studentID = getDocumentUploadService().getCreatorId(token);
            if (studentID != null && versionID != null) {
                DocumentVersion documentVersion;
                try {
                    documentVersion = getDocumentVersionRepository().findById(versionID).get();
                } catch (NoSuchElementException noSuchElementException) {
                    documentVersion = null;
                }
                if (documentVersion != null) {
                    Document document = getDocumentRepository().findById(documentVersion.getDocument()).get();
                    int kind = document.getKind();
                    switch (kind) {
                        case 6:
                            documentVersion.getVkrAllowance().setAllowanceStatus(4);
                            getVkrAllowanceRepository().save(documentVersion.getVkrAllowance());
                            break;
                        case 7:
                            documentVersion.getAdvisorConclusion().setConclusionStatus(4);
                            getVkrConclusionRepository().save(documentVersion.getAdvisorConclusion());
                            break;
                        case 8:
                            documentVersion.getVkrAntiplagiat().setAntiplagiatStatus(4);
                            getVkrAntiplagiatRepository().save(documentVersion.getVkrAntiplagiat());
                            break;
                        case 9:
                            documentVersion.getVkrPresentation().setPresentationStatus(4);
                            getVkrPresentationRepository().save(documentVersion.getVkrPresentation());
                            break;
                        default:
                            return "Некорректно указан этап";
                    }
                    return "Версия документа успешно отправлена";
                } else {
                    return "Версия документа не найдена";
                }
            } else {
                return "ID студента или версии не найден";
            }
        } else {
            return "Неверный параметр статуса. Невозможно отправить задание";
        }
    }

    // Научный руководитель одобряет или замечает задание
    public String advisorCheckTask(String token, String newStatus, Integer versionID) {
        if (newStatus.equals("Одобрено") || newStatus.equals("Замечания")) {
            Integer advisorID = getDocumentUploadService().getCreatorId(token);
            if (advisorID != null && versionID != null) {
                DocumentVersion documentVersion;
                try {
                    documentVersion = getDocumentVersionRepository().findById(versionID).get();
                } catch (NoSuchElementException noSuchElementException) {
                    documentVersion = null;
                }
                if (documentVersion != null) {
                    Document document = getDocumentRepository().findById(documentVersion.getDocument()).get();
                    int type = document.getType();
                    switch (type) {
                        case 1:
                            if (newStatus.equals("Одобрено") &&
                                    documentVersion.getNirTask().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getNirTask().setStatus(2);
                                getNirTaskRepository().save(documentVersion.getNirTask());
                                return "Версия документа успешно прорецензирована";
                            } else if (newStatus.equals("Замечания") &&
                                    documentVersion.getNirTask().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getNirTask().setStatus(3);
                                getNirTaskRepository().save(documentVersion.getNirTask());
                                return "Версия документа успешно прорецензирована";
                            } else if (documentVersion.getEditor() == advisorID) {
                                if (newStatus.equals("Одобрено")) {
                                    documentVersion.getNirTask().setStatus(2);
                                    getNirTaskRepository().save(documentVersion.getNirTask());
                                    return "Вы отправили студенту свою версию задания с статусом одобрено";
                                } else if (newStatus.equals("Замечания")) {
                                    documentVersion.getNirTask().setStatus(3);
                                    getNirTaskRepository().save(documentVersion.getNirTask());
                                    return "Вы отправили студенту свою версию задания с статусом замечания";
                                }
                            }
                        case 2:
                            if (newStatus.equals("Одобрено") &&
                                    documentVersion.getPpppuiopdTask().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getPpppuiopdTask().setStatus(2);
                                getPpppuiopdTaskRepository().save(documentVersion.getPpppuiopdTask());
                                return "Версия документа успешно прорецензирована";
                            } else if (newStatus.equals("Замечания") &&
                                    documentVersion.getPpppuiopdTask().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getPpppuiopdTask().setStatus(3);
                                getPpppuiopdTaskRepository().save(documentVersion.getPpppuiopdTask());
                                return "Версия документа успешно прорецензирована";
                            } else if (documentVersion.getEditor() == advisorID) {
                                if (newStatus.equals("Одобрено")) {
                                    documentVersion.getPpppuiopdTask().setStatus(2);
                                    getPpppuiopdTaskRepository().save(documentVersion.getPpppuiopdTask());
                                    return "Вы отправили студенту свою версию задания с статусом одобрено";
                                } else if (newStatus.equals("Замечания")) {
                                    documentVersion.getPpppuiopdTask().setStatus(3);
                                    getPpppuiopdTaskRepository().save(documentVersion.getPpppuiopdTask());
                                    return "Вы отправили студенту свою версию задания с статусом замечания";
                                }
                            }
                        case 3:
                            if (newStatus.equals("Одобрено") &&
                                    documentVersion.getPdTask().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getPdTask().setStatus(2);
                                getPdTaskRepository().save(documentVersion.getPdTask());
                                return "Версия документа успешно прорецензирована";
                            } else if (newStatus.equals("Замечания") &&
                                    documentVersion.getPdTask().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getPdTask().setStatus(3);
                                getPdTaskRepository().save(documentVersion.getPdTask());
                                return "Версия документа успешно прорецензирована";
                            } else if (documentVersion.getEditor() == advisorID) {
                                if (newStatus.equals("Одобрено")) {
                                    documentVersion.getPdTask().setStatus(2);
                                    getPdTaskRepository().save(documentVersion.getPdTask());
                                    return "Вы отправили студенту свою версию задания с статусом одобрено";
                                } else if (newStatus.equals("Замечания")) {
                                    documentVersion.getPdTask().setStatus(3);
                                    getPdTaskRepository().save(documentVersion.getPdTask());
                                    return "Вы отправили студенту свою версию задания с статусом замечания";
                                }
                            }
                        case 4:
                            if (newStatus.equals("Одобрено") &&
                                    documentVersion.getVkrTask().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getVkrTask().setVkr_status(2);
                                getVkrTaskRepository().save(documentVersion.getVkrTask());
                                return "Версия документа успешно прорецензирована";
                            } else if (newStatus.equals("Замечания") &&
                                    documentVersion.getVkrTask().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getVkrTask().setVkr_status(3);
                                getVkrTaskRepository().save(documentVersion.getVkrTask());
                                return "Версия документа успешно прорецензирована";
                            } else if (documentVersion.getEditor() == advisorID) {
                                if (newStatus.equals("Одобрено")) {
                                    documentVersion.getVkrTask().setVkr_status(2);
                                    getVkrTaskRepository().save(documentVersion.getVkrTask());
                                    return "Вы отправили студенту свою версию задания с статусом одобрено";
                                } else if (newStatus.equals("Замечания")) {
                                    documentVersion.getVkrTask().setVkr_status(3);
                                    getVkrTaskRepository().save(documentVersion.getVkrTask());
                                    return "Вы отправили студенту свою версию задания с статусом замечания";
                                }
                            }
                        default:
                            break;
                    }
                    return "Вы не можете оценить не отправленную студентом или уже оцененную версию";
                } else {
                    return "Версия документа не найдена";
                }
            } else {
                return "ID научного руководителя или версии не найден";
            }
        } else {
            return "Неверный параметр статуса. Невозможно прорецензировать задание";
        }
    }

    // Научный руководитель одобряет или замечает один из документов по ВКР
    public String advisorCheckVkrStuff(String token, String newStatus, Integer versionID) {
        if (newStatus.equals("Одобрено") || newStatus.equals("Замечания")) {
            Integer advisorID = getDocumentUploadService().getCreatorId(token);
            if (advisorID != null && versionID != null) {
                DocumentVersion documentVersion;
                try {
                    documentVersion = getDocumentVersionRepository().findById(versionID).get();
                } catch (NoSuchElementException noSuchElementException) {
                    documentVersion = null;
                }
                if (documentVersion != null) {
                    Document document = getDocumentRepository().findById(documentVersion.getDocument()).get();
                    int kind = document.getKind();
                    switch (kind) {
                        case 6:
                            if (newStatus.equals("Одобрено") &&
                                    documentVersion.getVkrAllowance().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getVkrAllowance().setAllowanceStatus(2);
                                getVkrAllowanceRepository().save(documentVersion.getVkrAllowance());
                                return "Версия документа успешно прорецензирована";
                            } else if (newStatus.equals("Замечания") &&
                                    documentVersion.getVkrAllowance().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getVkrAllowance().setAllowanceStatus(3);
                                getVkrAllowanceRepository().save(documentVersion.getVkrAllowance());
                                return "Версия документа успешно прорецензирована";
                            } else if (documentVersion.getEditor() == advisorID) {
                                if (newStatus.equals("Одобрено")) {
                                    documentVersion.getVkrAllowance().setAllowanceStatus(2);
                                    getVkrAllowanceRepository().save(documentVersion.getVkrAllowance());
                                    return "Вы отправили студенту свою версию допуска с статусом одобрено";
                                } else if (newStatus.equals("Замечания")) {
                                    documentVersion.getVkrAllowance().setAllowanceStatus(3);
                                    getVkrAllowanceRepository().save(documentVersion.getVkrAllowance());
                                    return "Вы отправили студенту свою версию допуска с статусом замечания";
                                }
                            }
                        case 7:
                            if (newStatus.equals("Одобрено") &&
                                    documentVersion.getAdvisorConclusion().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getAdvisorConclusion().setConclusionStatus(2);
                                getVkrConclusionRepository().save(documentVersion.getAdvisorConclusion());
                                return "Версия документа успешно прорецензирована";
                            } else if (newStatus.equals("Замечания") &&
                                    documentVersion.getAdvisorConclusion().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getAdvisorConclusion().setConclusionStatus(3);
                                getVkrConclusionRepository().save(documentVersion.getAdvisorConclusion());
                                return "Версия документа успешно прорецензирована";
                            } else if (documentVersion.getEditor() == advisorID) {
                                if (newStatus.equals("Одобрено")) {
                                    documentVersion.getAdvisorConclusion().setConclusionStatus(2);
                                    getVkrConclusionRepository().save(documentVersion.getAdvisorConclusion());
                                    return "Вы отправили студенту свою версию задания с статусом одобрено";
                                } else if (newStatus.equals("Замечания")) {
                                    documentVersion.getAdvisorConclusion().setConclusionStatus(3);
                                    getVkrConclusionRepository().save(documentVersion.getAdvisorConclusion());
                                    return "Вы отправили студенту свою версию задания с статусом замечания";
                                }
                            }
                        case 8:
                            if (newStatus.equals("Одобрено") &&
                                    documentVersion.getVkrAntiplagiat().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getVkrAntiplagiat().setAntiplagiatStatus(2);
                                getVkrAntiplagiatRepository().save(documentVersion.getVkrAntiplagiat());
                                return "Версия документа успешно прорецензирована";
                            } else if (newStatus.equals("Замечания") &&
                                    documentVersion.getVkrAntiplagiat().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getVkrAntiplagiat().setAntiplagiatStatus(3);
                                getVkrAntiplagiatRepository().save(documentVersion.getVkrAntiplagiat());
                                return "Версия документа успешно прорецензирована";
                            } else if (documentVersion.getEditor() == advisorID) {
                                if (newStatus.equals("Одобрено")) {
                                    documentVersion.getVkrAntiplagiat().setAntiplagiatStatus(2);
                                    getVkrAntiplagiatRepository().save(documentVersion.getVkrAntiplagiat());
                                    return "Вы отправили студенту свою версию задания с статусом одобрено";
                                } else if (newStatus.equals("Замечания")) {
                                    documentVersion.getVkrAntiplagiat().setAntiplagiatStatus(3);
                                    getVkrAntiplagiatRepository().save(documentVersion.getVkrAntiplagiat());
                                    return "Вы отправили студенту свою версию задания с статусом замечания";
                                }
                            }
                        case 9:
                            if (newStatus.equals("Одобрено") &&
                                    documentVersion.getVkrPresentation().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getVkrPresentation().setPresentationStatus(2);
                                getVkrPresentationRepository().save(documentVersion.getVkrPresentation());
                                return "Версия документа успешно прорецензирована";
                            } else if (newStatus.equals("Замечания") &&
                                    documentVersion.getVkrPresentation().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getVkrPresentation().setPresentationStatus(3);
                                getVkrPresentationRepository().save(documentVersion.getVkrPresentation());
                                return "Версия документа успешно прорецензирована";
                            } else if (documentVersion.getEditor() == advisorID) {
                                if (newStatus.equals("Одобрено")) {
                                    documentVersion.getVkrPresentation().setPresentationStatus(2);
                                    getVkrPresentationRepository().save(documentVersion.getVkrPresentation());
                                    return "Вы отправили студенту свою версию задания с статусом одобрено";
                                } else if (newStatus.equals("Замечания")) {
                                    documentVersion.getVkrPresentation().setPresentationStatus(3);
                                    getVkrPresentationRepository().save(documentVersion.getVkrPresentation());
                                    return "Вы отправили студенту свою версию задания с статусом замечания";
                                }
                            }
                        default:
                            break;
                    }
                    return "Вы не можете оценить не отправленную студентом или уже оцененную версию";
                } else {
                    return "Версия документа не найдена";
                }
            } else {
                return "ID научного руководителя или версии не найден";
            }
        } else {
            return "Неверный параметр статуса. Невозможно прорецензировать задание";
        }
    }

    // Студент удаляет версию задания
    public String studentDeleteTaskVersion(String token, Integer versionID) {
        Integer studentID = getDocumentUploadService().getCreatorId(token);
        DocumentVersion documentVersion;
        try {
            documentVersion = getDocumentVersionRepository().findById(versionID).get();
            if (studentID != null) {
                if (documentVersion.getEditor() == studentID) {
                    Document document = getDocumentRepository().findById(documentVersion.getDocument()).get();
                    int type = document.getType();
                    switch (type) {
                        case 1:
                            if (!isLastChecked(documentVersion, "nirTask")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            } else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 2:
                            if (!isLastChecked(documentVersion, "ppppuiopdTask")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            } else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 3:
                            if (!isLastChecked(documentVersion, "pdTask")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            } else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 4:
                            if (!isLastChecked(documentVersion, "vkrTask")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            } else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        default:
                            return "Непредвиденная ошибка при удалении версии задания";
                    }
                } else {
                    return "Вы не можете удалить версию документа, которую создали не вы";
                }
            } else {
                return "ID студента не найден";
            }
        } catch (NoSuchElementException noSuchElementException) {
            return "Версия документа не найдена";
        }
    }

    // Научный руководитель удаляет версию задания
    public String advisorDeleteTaskVersion(String token, Integer versionID, Integer studentID) {
        Integer advisorID = getDocumentUploadService().getCreatorId(token);
        DocumentVersion documentVersion;
        try {
            documentVersion = getDocumentVersionRepository().findById(versionID).get();
            if (advisorID != null && studentID != null) {
                AssociatedStudents associatedStudent =
                        getAssociatedStudentsRepository().findByScientificAdvisorAndStudent(advisorID, studentID);
                if (associatedStudent != null) {
                    Document document = getDocumentRepository().findById(documentVersion.getDocument()).get();
                    int type = document.getType();
                    switch (type) {
                        case 1:
                            if (documentVersion.getEditor() == advisorID &&
                                    !isLastChecked(documentVersion, "nirTask")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            }
                            else if (documentVersion.getEditor() == studentID &&
                                    !isLastChecked(documentVersion, "nirTask")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            }
                            else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 2:
                            if (documentVersion.getEditor() == advisorID &&
                                    !isLastChecked(documentVersion, "ppppuiopdTask")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            }
                            else if (documentVersion.getEditor() == studentID &&
                                    !isLastChecked(documentVersion, "ppppuiopdTask")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            }
                            else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 3:
                            if (documentVersion.getEditor() == advisorID &&
                                    !isLastChecked(documentVersion, "pdTask")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            }
                            else if (documentVersion.getEditor() == studentID &&
                                    !isLastChecked(documentVersion, "pdTask")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            }
                            else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 4:
                            if (documentVersion.getEditor() == advisorID &&
                                    !isLastChecked(documentVersion, "vkrTask")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            }
                            else if (documentVersion.getEditor() == studentID &&
                                    !isLastChecked(documentVersion, "vkrTask")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            }
                            else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        default:
                            return "Непредвиденная ошибка при удалении версии задания научным руководителем";
                    }
                } else {
                    return "Попытка удалить чужую версию документа";
                }
            } else {
                return "Переданные ID руководителя и студента не найдены";
            }
        } catch (NoSuchElementException noSuchElementException) {
            return "Версия доумента не найдена";
        }
    }

    // Студент удаляет версию вкрного стаффа
    public String studentDeleteVkrStuffVersion(String token, Integer versionID) {
        Integer studentID = getDocumentUploadService().getCreatorId(token);
        DocumentVersion documentVersion;
        try {
            documentVersion = getDocumentVersionRepository().findById(versionID).get();
            if (studentID != null) {
                if (documentVersion.getEditor() == studentID) {
                    Document document = getDocumentRepository().findById(documentVersion.getDocument()).get();
                    int kind = document.getKind();
                    switch (kind) {
                        case 6:
                            if (!isLastChecked(documentVersion, "vkrAllowance")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            } else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 7:
                            if (!isLastChecked(documentVersion, "advisorConclusion")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            } else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 8:
                            if (!isLastChecked(documentVersion, "vkrAnitplagiat")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            } else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 9:
                            if (!isLastChecked(documentVersion, "vkrPresentation")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            } else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        default:
                            return "Непредвиденная ошибка при удалении версии задания";
                    }
                } else {
                    return "Вы не можете удалить версию документа, которую создали не вы";
                }
            } else {
                return "ID студента не найден";
            }
        } catch (NoSuchElementException noSuchElementException) {
            return "Версия документа не найдена";
        }
    }

    // Научный руководитель удаляет версию вкрного стаффа
    public String advisorDeleteVkrStuffVersion(String token, Integer versionID, Integer studentID) {
        Integer advisorID = getDocumentUploadService().getCreatorId(token);
        DocumentVersion documentVersion;
        try {
            documentVersion = getDocumentVersionRepository().findById(versionID).get();
            if (advisorID != null && studentID != null) {
                AssociatedStudents associatedStudent =
                        getAssociatedStudentsRepository().findByScientificAdvisorAndStudent(advisorID, studentID);
                if (associatedStudent != null) {
                    Document document = getDocumentRepository().findById(documentVersion.getDocument()).get();
                    int kind = document.getKind();
                    switch (kind) {
                        case 6:
                            if (documentVersion.getEditor() == advisorID &&
                                    !isLastChecked(documentVersion, "vkrAllowance")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            }
                            else if (documentVersion.getEditor() == studentID &&
                                    !isLastChecked(documentVersion, "vkrAllowance")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            }
                            else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 7:
                            if (documentVersion.getEditor() == advisorID &&
                                    !isLastChecked(documentVersion, "advisorConclusion")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            }
                            else if (documentVersion.getEditor() == studentID &&
                                    !isLastChecked(documentVersion, "advisorConclusion")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            }
                            else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 8:
                            if (documentVersion.getEditor() == advisorID &&
                                    !isLastChecked(documentVersion, "vkrAnitplagiat")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            }
                            else if (documentVersion.getEditor() == studentID &&
                                    !isLastChecked(documentVersion, "vkrAnitplagiat")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            }
                            else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 9:
                            if (documentVersion.getEditor() == advisorID &&
                                    !isLastChecked(documentVersion, "vkrPresentation")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            }
                            else if (documentVersion.getEditor() == studentID &&
                                    !isLastChecked(documentVersion, "vkrPresentation")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия документа успешно удалена";
                            }
                            else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        default:
                            return "Непредвиденная ошибка при удалении версии задания научным руководителем";
                    }
                } else {
                    return "Попытка удалить чужую версию документа";
                }
            } else {
                return "Переданные ID руководителя и студента не найдены";
            }
        } catch (NoSuchElementException noSuchElementException) {
            return "Версия доумента не найдена";
        }
    }

    // Студент отправляет версию отчета научному руководителю
    public String studentSendingReport(String token, String newStatus, Integer versionID) {
        if (newStatus.equals("Рассматривается")) {
            Integer studentID = getDocumentUploadService().getCreatorId(token);
            if (studentID != null && versionID != null) {
                DocumentVersion documentVersion;
                try {
                    documentVersion = getDocumentVersionRepository().findById(versionID).get();
                } catch (NoSuchElementException noSuchElementException) {
                    documentVersion = null;
                }
                if (documentVersion != null) {
                    Document document = getDocumentRepository().findById(documentVersion.getDocument()).get();
                    int type = document.getType();
                    switch (type) {
                        case 1:
                            documentVersion.getNirReport().setNirReportStatus(4);
                            getNirReportRepository().save(documentVersion.getNirReport());
                            break;
                        case 2:
                            documentVersion.getPpppuiopdReport().setPpppuiopdReportStatus(4);
                            getPpppuiopdReportRepository().save(documentVersion.getPpppuiopdReport());
                            break;
                        case 3:
                            documentVersion.getPdReport().setPdReportStatus(4);
                            getPdReportRepository().save(documentVersion.getPdReport());
                            break;
                        case 4:
                            documentVersion.getVkrReport().setVkrReportStatus(4);
                            getVkrReportRepository().save(documentVersion.getVkrReport());
                            break;
                        default:
                            return "Некорректно указан этап";
                    }
                    return "Версия отчёта успешно отправлена";
                } else {
                    return "Версия отчёта не найдена";
                }
            } else {
                return "ID студента или версии не найден";
            }
        } else {
            return "Неверный параметр статуса. Невозможно отправить задание";
        }
    }

    // Научный руководитель одобряет или замечает отчет
    public String advisorCheckReport(String token, String newStatus, Integer versionID) {
        if (newStatus.equals("Неудовлетворительно") || newStatus.equals("Удовлетворительно") ||
                newStatus.equals("Хорошо") || newStatus.equals("Отлично") || newStatus.equals("Замечания")) {
            Integer advisorID = getDocumentUploadService().getCreatorId(token);
            if (advisorID != null && versionID != null) {
                DocumentVersion documentVersion;
                try {
                    documentVersion = getDocumentVersionRepository().findById(versionID).get();
                } catch (NoSuchElementException noSuchElementException) {
                    documentVersion = null;
                }
                if (documentVersion != null) {
                    Document document = getDocumentRepository().findById(documentVersion.getDocument()).get();
                    int type = document.getType();
                    switch (type) {
                        case 1:
                            if (determineMark(newStatus) != 0 &&
                                    documentVersion.getNirReport().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getNirReport().setNirReportStatus(determineMark(newStatus));
                                getNirReportRepository().save(documentVersion.getNirReport());
                                return "Версия документа успешно прорецензирована";
                            }
                            else if (documentVersion.getEditor() == advisorID) {
                                if (determineMark(newStatus) != 0) {
                                    documentVersion.getNirReport().setNirReportStatus(determineMark(newStatus));
                                    getNirReportRepository().save(documentVersion.getNirReport());
                                    return "Вы отправили студенту свою версию отчёта";
                                }
                            }
                        case 2:
                            if (determineMark(newStatus) != 0 &&
                                    documentVersion.getPpppuiopdReport().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getPpppuiopdReport().setPpppuiopdReportStatus(determineMark(newStatus));
                                getPpppuiopdReportRepository().save(documentVersion.getPpppuiopdReport());
                                return "Версия документа успешно прорецензирована";
                            }
                            else if (documentVersion.getEditor() == advisorID) {
                                if (determineMark(newStatus) != 0) {
                                    documentVersion.getPpppuiopdReport().setPpppuiopdReportStatus(determineMark(newStatus));
                                    getPpppuiopdReportRepository().save(documentVersion.getPpppuiopdReport());
                                    return "Вы отправили студенту свою версию задания с статусом одобрено";
                                }
                            }
                        case 3:
                            if (determineMark(newStatus) != 0 &&
                                    documentVersion.getPdReport().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getPdReport().setPdReportStatus(determineMark(newStatus));
                                getPdReportRepository().save(documentVersion.getPdReport());
                                return "Версия документа успешно прорецензирована";
                            }
                            else if (documentVersion.getEditor() == advisorID) {
                                if (determineMark(newStatus) != 0) {
                                    documentVersion.getPdReport().setPdReportStatus(determineMark(newStatus));
                                    getPdReportRepository().save(documentVersion.getPdReport());
                                    return "Вы отправили студенту свою версию задания с статусом одобрено";
                                }
                            }
                        case 4:
                            if (determineMark(newStatus) != 0 &&
                                    documentVersion.getVkrReport().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getVkrReport().setVkrReportStatus(determineMark(newStatus));
                                getVkrReportRepository().save(documentVersion.getVkrReport());
                                return "Версия документа успешно прорецензирована";
                            }
                            else if (documentVersion.getEditor() == advisorID) {
                                if (determineMark(newStatus) != 0) {
                                    documentVersion.getVkrReport().setVkrReportStatus(determineMark(newStatus));
                                    getVkrReportRepository().save(documentVersion.getVkrReport());
                                    return "Вы отправили студенту свою версию задания с статусом одобрено";
                                }
                            }
                        default:
                            break;
                    }
                    return "Вы не можете оценить не отправленную студентом или уже оцененную версию";
                } else {
                    return "Версия документа не найдена";
                }
            } else {
                return "ID научного руководителя или версии не найден";
            }
        } else {
            return "Неверный параметр статуса. Невозможно прорецензировать задание";
        }
    }

    // Студент удаляет версию отчета
    public String studentDeleteReportVersion(String token, Integer versionID) {
        Integer studentID = getDocumentUploadService().getCreatorId(token);
        DocumentVersion documentVersion;
        try {
            documentVersion = getDocumentVersionRepository().findById(versionID).get();
            if (studentID != null) {
                if (documentVersion.getEditor() == studentID) {
                    Document document = getDocumentRepository().findById(documentVersion.getDocument()).get();
                    int type = document.getType();
                    switch (type) {
                        case 1:
                            if (!isLastChecked(documentVersion, "nirReport")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            }
                            else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 2:
                            if (!isLastChecked(documentVersion, "ppppuiopdReport")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            }
                            else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 3:
                            if (!isLastChecked(documentVersion, "pdReport")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            }
                            else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 4:
                            if (!isLastChecked(documentVersion, "vkrReport")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            }
                            else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        default:
                            return "Непредвиденная ошибка при удалении версии отчета";
                    }
                } else {
                    return "Вы не можете удалить версию отчета, которую создали не вы";
                }
            } else {
                return "ID студента не найден";
            }
        } catch (NoSuchElementException noSuchElementException) {
            return "Версия отчета не найдена";
        }
    }

    // Научный руководитель удаляет версию отчета
    public String advisorDeleteReportVersion(String token, Integer versionID, Integer studentID) {
        Integer advisorID = getDocumentUploadService().getCreatorId(token);
        DocumentVersion documentVersion;
        try {
            documentVersion = getDocumentVersionRepository().findById(versionID).get();
            if (advisorID != null && studentID != null) {
                AssociatedStudents associatedStudent =
                        getAssociatedStudentsRepository().findByScientificAdvisorAndStudent(advisorID, studentID);
                if (associatedStudent != null) {
                    Document document = getDocumentRepository().findById(documentVersion.getDocument()).get();
                    int type = document.getType();
                    switch (type) {
                        case 1:
                            if (documentVersion.getEditor() == advisorID &&
                                    !isLastChecked(documentVersion, "nirReport")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            }
                            else if (documentVersion.getEditor() == studentID &&
                                    !isLastChecked(documentVersion, "nirReport")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            }
                            else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 2:
                            if (documentVersion.getEditor() == advisorID &&
                                    !isLastChecked(documentVersion, "ppppuiopdReport")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            }
                            else if (documentVersion.getEditor() == studentID &&
                                    !isLastChecked(documentVersion, "ppppuiopdReport")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            }
                            else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 3:
                            if (documentVersion.getEditor() == advisorID &&
                                    !isLastChecked(documentVersion, "pdReport")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            }
                            else if (documentVersion.getEditor() == studentID &&
                                    !isLastChecked(documentVersion, "pdReport")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            }
                            else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        case 4:
                            if (documentVersion.getEditor() == advisorID &&
                                    !isLastChecked(documentVersion, "vkrReport")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            }
                            else if (documentVersion.getEditor() == studentID &&
                                    !isLastChecked(documentVersion, "vkrReport")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                getDocumentVersionRepository().delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            }
                            else {
                                return "Запрещено удалять последнюю прорецензированную версию документа";
                            }
                        default:
                            return "Непредвиденная ошибка при удалении версии отчета научным руководителем";
                    }
                } else {
                    return "Попытка удалить чужую версию отчета";
                }
            } else {
                return "Переданные ID руководителя и студента не найдены";
            }
        } catch (NoSuchElementException noSuchElementException) {
            return "Версия отчета не найдена";
        }
    }

    public StudentDocumentsStatusView getStudentsDocumentStatus(Integer studentID) {
        StudentDocumentsStatusView statusView = new StudentDocumentsStatusView(0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, false, false,
                false, false, false, false,
                false, false);
        Document nirTask;
        Document nirReport;
        Document ppppuiopdTask;
        Document ppppuiopdReport;
        Document pdTask;
        Document pdReport;
        Document vkrTask;
        Document vkrReport;
        Document vkrPresentation;
        Document vkrAllowance;
        Document vkrAntiplagiat;
        Document vkrAdvisorConclusion;
        try {
            // НИР
            if (getDocumentRepository().findByTypeAndKindAndCreator(1, 2, studentID).size() == 1) {
                nirTask = getDocumentRepository().findByTypeAndKindAndCreator(1, 2, studentID).get(0);
                List<DocumentVersion> nirTaskVersions = getDocumentVersionRepository().findByDocument(nirTask.getId());
                // Пройдем по версиям задания студента
                for (DocumentVersion nirTaskVersion : nirTaskVersions) {
                    if (nirTaskVersion.getNirTask().getDocumentStatus().getStatus().equals("Одобрено")) {
                        statusView.setNirTaskStatus(1);
                    }
                    if (nirTaskVersion.getNirTask().isHocRate()) {
                        statusView.setNirTaskHocRate(true);
                    }
                }
            }
            if (getDocumentRepository().findByTypeAndKindAndCreator(1, 3, studentID).size() == 1) {
                nirReport = getDocumentRepository().findByTypeAndKindAndCreator(1, 3, studentID).get(0);
                List<DocumentVersion> nirReportVersions = getDocumentVersionRepository().findByDocument(nirReport.getId());
                // Пройдем по версиям отчёта студента
                for (DocumentVersion nirReportVersion: nirReportVersions) {
                    if (nirReportVersion.getNirReport().getDocumentStatus().getStatus().equals("Неудовлетворительно")) {
                        statusView.setNirReportStatus(2);
                    } else if (nirReportVersion.getNirReport().getDocumentStatus().getStatus().equals("Удовлетворительно")) {
                        statusView.setNirReportStatus(3);
                    } else if (nirReportVersion.getNirReport().getDocumentStatus().getStatus().equals("Хорошо")) {
                        statusView.setNirReportStatus(4);
                    } else if (nirReportVersion.getNirReport().getDocumentStatus().getStatus().equals("Отлично")) {
                        statusView.setNirReportStatus(5);
                    }
                    if (nirReportVersion.getNirReport().isHocRate()) {
                        statusView.setNirReportHocRate(true);
                    }
                }
            }
            // ППППУиОПД
            if (getDocumentRepository().findByTypeAndKindAndCreator(2, 2, studentID).size() == 1) {
                ppppuiopdTask = getDocumentRepository().findByTypeAndKindAndCreator(2, 2, studentID).get(0);
                List<DocumentVersion> ppppuiopdTaskVersions = getDocumentVersionRepository().findByDocument(ppppuiopdTask.getId());
                // Пройдем по версиям задания студента
                for (DocumentVersion ppppuiopdTaskVersion : ppppuiopdTaskVersions) {
                    if (ppppuiopdTaskVersion.getPpppuiopdTask().getDocumentStatus().getStatus().equals("Одобрено")) {
                        statusView.setPpppuipdTaskStatus(1);
                    }
                    if (ppppuiopdTaskVersion.getPpppuiopdTask().isHocRate()) {
                        statusView.setPpppuipdTaskHocRate(true);
                    }
                }
            }
            if (getDocumentRepository().findByTypeAndKindAndCreator(2, 3, studentID).size() == 1) {
                ppppuiopdReport = getDocumentRepository().findByTypeAndKindAndCreator(2, 3, studentID).get(0);
                List<DocumentVersion> ppppuiopdReportVersions = getDocumentVersionRepository().findByDocument(ppppuiopdReport.getId());
                // Пройдем по версиям отчёта студента
                for (DocumentVersion ppppuiopdReportVersion: ppppuiopdReportVersions) {
                    switch (ppppuiopdReportVersion.getPpppuiopdReport().getDocumentStatus().getStatus()) {
                        case "Неудовлетворительно" -> statusView.setPpppuipdReportStatus(2);
                        case "Удовлетворительно" -> statusView.setPpppuipdReportStatus(3);
                        case "Хорошо" -> statusView.setPpppuipdReportStatus(4);
                        case "Отлично" -> statusView.setPpppuipdReportStatus(5);
                    }
                    if (ppppuiopdReportVersion.getPpppuiopdReport().isHocRate()) {
                        statusView.setPpppuipdReportHocRate(true);
                    }
                }
            }
            // ПП
            if (getDocumentRepository().findByTypeAndKindAndCreator(3, 2, studentID).size() == 1) {
                pdTask = getDocumentRepository().findByTypeAndKindAndCreator(3, 2, studentID).get(0);
                List<DocumentVersion> pdTaskVersions = getDocumentVersionRepository().findByDocument(pdTask.getId());
                // Пройдем по версиям задания студента
                for (DocumentVersion pdTaskVersion : pdTaskVersions) {
                    if (pdTaskVersion.getPdTask().getDocumentStatus().getStatus().equals("Одобрено")) {
                        statusView.setPpTaskStatus(1);
                    }
                    if (pdTaskVersion.getPdTask().isHocRate()) {
                        statusView.setPpTaskHocRate(true);
                    }
                }
            }
            if (getDocumentRepository().findByTypeAndKindAndCreator(3, 3, studentID).size() == 1) {
                pdReport = getDocumentRepository().findByTypeAndKindAndCreator(3, 3, studentID).get(0);
                List<DocumentVersion> pdReportVersions = getDocumentVersionRepository().findByDocument(pdReport.getId());
                // Пройдем по версиям отчёта студента
                for (DocumentVersion pdReportVersion: pdReportVersions) {
                    switch (pdReportVersion.getPdReport().getDocumentStatus().getStatus()) {
                        case "Неудовлетворительно" -> statusView.setPpReportStatus(2);
                        case "Удовлетворительно" -> statusView.setPpReportStatus(3);
                        case "Хорошо" -> statusView.setPpReportStatus(4);
                        case "Отлично" -> statusView.setPpReportStatus(5);
                    }
                    if (pdReportVersion.getPdReport().isHocRate()) {
                        statusView.setPpReportHocRate(true);
                    }
                }
            }
            // ВКР
            if (getDocumentRepository().findByTypeAndKindAndCreator(4, 2, studentID).size() == 1) {
                vkrTask = getDocumentRepository().findByTypeAndKindAndCreator(4, 2, studentID).get(0);
                List<DocumentVersion> vkrTaskVersions = getDocumentVersionRepository().findByDocument(vkrTask.getId());
                // Пройдем по версиям задания студента
                for (DocumentVersion vkrTaskVersion : vkrTaskVersions) {
                    if (vkrTaskVersion.getVkrTask().getDocumentStatus().getStatus().equals("Одобрено")) {
                        statusView.setVkrTask(1);
                    }
                    if (vkrTaskVersion.getVkrTask().isHocRate()) {
                        statusView.setVkrTaskHocRate(true);
                    }
                }
            }
            if (getDocumentRepository().findByTypeAndKindAndCreator(4, 3, studentID).size() == 1) {
                vkrReport = getDocumentRepository().findByTypeAndKindAndCreator(4, 3, studentID).get(0);
                List<DocumentVersion> vkrReportVersions = getDocumentVersionRepository().findByDocument(vkrReport.getId());
                // Пройдем по версиям отчёта студента
                for (DocumentVersion vkrReportVersion: vkrReportVersions) {
                    switch (vkrReportVersion.getVkrReport().getDocumentStatus().getStatus()) {
                        case "Неудовлетворительно" -> statusView.setVkrRPZ(2);
                        case "Удовлетворительно" -> statusView.setVkrRPZ(3);
                        case "Хорошо" -> statusView.setVkrRPZ(4);
                        case "Отлично" -> statusView.setVkrRPZ(5);
                    }
                    if (vkrReportVersion.getVkrReport().isHocRate()) {
                        statusView.setVkrRPZHocRate(true);
                    }
                }
            }
            if (getDocumentRepository().findByTypeAndKindAndCreator(4, 6, studentID).size() == 1) {
                vkrAllowance = getDocumentRepository().findByTypeAndKindAndCreator(4, 6, studentID).get(0);
                List<DocumentVersion> vkrAllowanceVersions = getDocumentVersionRepository().findByDocument(vkrAllowance.getId());
                // Пройдем по версиям отчёта студента
                for (DocumentVersion vkrAllowanceVersion: vkrAllowanceVersions) {
                    if (vkrAllowanceVersion.getVkrAllowance().getDocumentStatus().getStatus().equals("Одобрено")) {
                        statusView.setVkrAllowance(1);
                    }
                }
            }
            if (getDocumentRepository().findByTypeAndKindAndCreator(4, 7, studentID).size() == 1) {
                vkrAdvisorConclusion = getDocumentRepository().findByTypeAndKindAndCreator(4, 7, studentID).get(0);
                List<DocumentVersion> vkrConclusionVersions = getDocumentVersionRepository().findByDocument(vkrAdvisorConclusion.getId());
                // Пройдем по версиям отчёта студента
                for (DocumentVersion vkrConclusionVersion: vkrConclusionVersions) {
                    if (vkrConclusionVersion.getAdvisorConclusion().getDocumentStatus().getStatus().equals("Одобрено")) {
                        statusView.setVkrAdvisorFeedback(1);
                    }
                }
            }
            if (getDocumentRepository().findByTypeAndKindAndCreator(4, 8, studentID).size() == 1) {
                vkrAntiplagiat = getDocumentRepository().findByTypeAndKindAndCreator(4, 8, studentID).get(0);
                List<DocumentVersion> vkrAntiplagiatVersions = getDocumentVersionRepository().findByDocument(vkrAntiplagiat.getId());
                // Пройдем по версиям отчёта студента
                for (DocumentVersion vkrAntiplagiatVersion: vkrAntiplagiatVersions) {
                    if (vkrAntiplagiatVersion.getVkrAntiplagiat().getDocumentStatus().getStatus().equals("Одобрено")) {
                        statusView.setVkrAntiplagiat(1);
                    }
                }
            }
            if (getDocumentRepository().findByTypeAndKindAndCreator(4, 9, studentID).size() == 1) {
                vkrPresentation = getDocumentRepository().findByTypeAndKindAndCreator(4, 9, studentID).get(0);
                List<DocumentVersion> vkrPresentationVersions = getDocumentVersionRepository().findByDocument(vkrPresentation.getId());
                // Пройдем по версиям отчёта студента
                for (DocumentVersion vkrPresentationVersion: vkrPresentationVersions) {
                    if (vkrPresentationVersion.getVkrPresentation().getDocumentStatus().getStatus().equals("Одобрено")) {
                        statusView.setVkrPresentation(1);
                    }
                }
            }
            return statusView;
        } catch (NullPointerException nullPointerException) {
            return new StudentDocumentsStatusView(-1, -1, -1,
                    -1, -1, -1, -1, -1, -1,
                    -1, -1, -1, false, false,
                    false, false, false, false,
                    false, false);
        }
    }

    // Одобрить шаблон
    public String approveTemplate(String token, Integer documentID) {
        Integer creatorID = getDocumentUploadService().getCreatorId(token);
        Document template;
        try {
            template = getDocumentRepository().findById(documentID).get();
            if (template.getKind() == 5) {
                TemplateProperties templateProperties;
                try {
                    templateProperties = getTemplatePropertiesRepository().findById(template.getId()).get();
                    templateProperties.setApproved(true);
                    getTemplatePropertiesRepository().save(templateProperties);
                    return "Шаблон успешно одобрен";
                } catch (NoSuchElementException noSuchElementException) {
                    return "У шаблона отсутствует статус, ошибка целостности";
                }
            }
        } catch (NoSuchElementException noSuchElementException) {
            return "Шаблон не найден";
        }
        return "Что-то пошло не так";
    }

    // Одобрить приказ
    public String approveOrder(String token, Integer documentID) {
        Integer creatorID = getDocumentUploadService().getCreatorId(token);
        Document order;
        try {
            order = getDocumentRepository().findById(documentID).get();
            if (order.getKind() == 1) {
                OrderProperties orderProperties;
                try {
                    orderProperties = getOrderPropertiesRepository().findById(order.getId()).get();
                    orderProperties.setApproved(true);
                    getOrderPropertiesRepository().save(orderProperties);
                    return "Приказ успешно одобрен";
                } catch (NoSuchElementException noSuchElementException) {
                    return "У приказа отсутствует статус, ошибка целостности";
                }
            }
        } catch (NoSuchElementException noSuchElementException) {
            return "Приказ не найден";
        }
        return "Что-то пошло не так";
    }

    public Integer determineMark(String newStatus) {
        return switch (newStatus) {
            case "Замечания" -> 3;
            case "Неудовлетворительно" -> 5;
            case "Удовлетворительно" -> 6;
            case "Хорошо" -> 7;
            case "Отлично" -> 8;
            default -> 0;
        };
    }

    private boolean isLastChecked(DocumentVersion documentVersion, String versionType) {
        List<DocumentVersion> documentVersions = getDocumentVersionRepository().findByDocument(documentVersion.getDocument());
        DocumentVersion lastCheckedVersion = null;
        switch (versionType) {
            case "nirTask":
                for (DocumentVersion version: documentVersions) {
                    if (version.getNirTask().getStatus() == 2) {
                        lastCheckedVersion = version;
                    }
                }
                break;
            case "nirReport":
                for (DocumentVersion version: documentVersions) {
                    if (version.getNirReport().getNirReportStatus() == 5 ||
                            version.getNirReport().getNirReportStatus() == 6 ||
                            version.getNirReport().getNirReportStatus() == 7 ||
                            version.getNirReport().getNirReportStatus() == 8) {
                        lastCheckedVersion = version;
                    }
                }
                break;
            case "ppppuiopdTask":
                for (DocumentVersion version: documentVersions) {
                    if (version.getPpppuiopdTask().getStatus() == 2) {
                        lastCheckedVersion = version;
                    }
                }
                break;
            case "ppppuiopdReport":
                for (DocumentVersion version: documentVersions) {
                    if (version.getPpppuiopdReport().getPpppuiopdReportStatus() == 5 ||
                            version.getPpppuiopdReport().getPpppuiopdReportStatus() == 6 ||
                            version.getPpppuiopdReport().getPpppuiopdReportStatus() == 7 ||
                            version.getPpppuiopdReport().getPpppuiopdReportStatus() == 8) {
                        lastCheckedVersion = version;
                    }
                }
                break;
            case "pdTask":
                for (DocumentVersion version: documentVersions) {
                    if (version.getPdTask().getStatus() == 2) {
                        lastCheckedVersion = version;
                    }
                }
                break;
            case "pdReport":
                for (DocumentVersion version: documentVersions) {
                    if (version.getPdReport().getPdReportStatus() == 5 ||
                            version.getPdReport().getPdReportStatus() == 6 ||
                            version.getPdReport().getPdReportStatus() == 7 ||
                            version.getPdReport().getPdReportStatus() == 8) {
                        lastCheckedVersion = version;
                    }
                }
                break;
            case "vkrTask":
                for (DocumentVersion version: documentVersions) {
                    if (version.getVkrTask().getVkr_status() == 2) {
                        lastCheckedVersion = version;
                    }
                }
                break;
            case "vkrReport":
                for (DocumentVersion version: documentVersions) {
                    if (version.getVkrReport().getVkrReportStatus() == 5 ||
                            version.getVkrReport().getVkrReportStatus() == 6 ||
                            version.getVkrReport().getVkrReportStatus() == 7 ||
                            version.getVkrReport().getVkrReportStatus() == 8) {
                        lastCheckedVersion = version;
                    }
                }
                break;
            case "advisorConclusion":
                for (DocumentVersion version: documentVersions) {
                    if (version.getAdvisorConclusion().getConclusionStatus() == 2) {
                        lastCheckedVersion = version;
                    }
                }
                break;
            case "vkrAllowance":
                for (DocumentVersion version: documentVersions) {
                    if (version.getVkrAllowance().getAllowanceStatus() == 2) {
                        lastCheckedVersion = version;
                    }
                }
                break;
            case "vkrAnitplagiat":
                for (DocumentVersion version: documentVersions) {
                    if (version.getVkrAntiplagiat().getAntiplagiatStatus() == 2) {
                        lastCheckedVersion = version;
                    }
                }
                break;
            case "vkrPresentation":
                for (DocumentVersion version: documentVersions) {
                    if (version.getVkrPresentation().getPresentationStatus() == 2) {
                        lastCheckedVersion = version;
                    }
                }
                break;
        }
        if (lastCheckedVersion != null) {
            return documentVersion.getId() == lastCheckedVersion.getId();
        }
        return false;
    }

    public DocumentRepository getDocumentRepository() {
        return documentRepository;
    }

    public DocumentUploadService getDocumentUploadService() {
        return documentUploadService;
    }

    public DocumentVersionRepository getDocumentVersionRepository() {
        return documentVersionRepository;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public DocumentTypeRepository getDocumentTypeRepository() {
        return documentTypeRepository;
    }

    public DocumentKindRepository getDocumentKindRepository() {
        return documentKindRepository;
    }

    public DocumentDownloadService getDocumentDownloadService() {
        return documentDownloadService;
    }

    public ViewRightsAreaRepository getViewRightsAreaRepository() {
        return viewRightsAreaRepository;
    }

    public NirTaskRepository getNirTaskRepository() {
        return nirTaskRepository;
    }

    public AssociatedStudentsRepository getAssociatedStudentsRepository() {
        return associatedStudentsRepository;
    }

    public NirReportRepository getNirReportRepository() {
        return nirReportRepository;
    }

    public ProjectRepository getProjectRepository() {
        return projectRepository;
    }

    public ProjectAreaRepository getProjectAreaRepository() {
        return projectAreaRepository;
    }

    public TemplatePropertiesRepository getTemplatePropertiesRepository() {
        return templatePropertiesRepository;
    }

    public OrderPropertiesRepository getOrderPropertiesRepository() {
        return orderPropertiesRepository;
    }

    public PpppuiopdReportRepository getPpppuiopdReportRepository() {
        return ppppuiopdReportRepository;
    }

    public PdReportRepository getPdReportRepository() {
        return pdReportRepository;
    }

    public PpppuiopdTaskRepository getPpppuiopdTaskRepository() {
        return ppppuiopdTaskRepository;
    }

    public PdTaskRepository getPdTaskRepository() {
        return pdTaskRepository;
    }

    public ViewRightsProjectRepository getViewRightsProjectRepository() {
        return viewRightsProjectRepository;
    }

    public VkrTaskRepository getVkrTaskRepository() {
        return vkrTaskRepository;
    }

    public VkrReportRepository getVkrReportRepository() {
        return vkrReportRepository;
    }

    public VkrAntiplagiatRepository getVkrAntiplagiatRepository() {
        return vkrAntiplagiatRepository;
    }

    public VkrPresentationRepository getVkrPresentationRepository() {
        return vkrPresentationRepository;
    }

    public VkrConclusionRepository getVkrConclusionRepository() {
        return vkrConclusionRepository;
    }

    public VkrAllowanceRepository getVkrAllowanceRepository() {
        return vkrAllowanceRepository;
    }

    public DocumentProcessorService getDocumentProcessorService() {
        return documentProcessorService;
    }
}