package g_server.g_server.application.service.documents;

import g_server.g_server.application.entity.documents.*;
import g_server.g_server.application.entity.project.Project;
import g_server.g_server.application.entity.project.ProjectArea;
import g_server.g_server.application.entity.users.AssociatedStudents;
import g_server.g_server.application.entity.view.StudentDocumentsStatusView;
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
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentUploadService documentUploadService;

    @Autowired
    private DocumentVersionRepository documentVersionRepository;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private DocumentKindRepository documentKindRepository;

    @Autowired
    private DocumentDownloadService documentDownloadService;

    @Autowired
    private ViewRightsAreaRepository viewRightsAreaRepository;

    @Autowired
    private NirTaskRepository nirTaskRepository;

    @Autowired
    private AssociatedStudentsRepository associatedStudentsRepository;

    @Autowired
    private NirReportRepository nirReportRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectAreaRepository projectAreaRepository;

    @Autowired
    private TemplatePropertiesRepository templatePropertiesRepository;

    @Autowired
    private OrderPropertiesRepository orderPropertiesRepository;

    @Autowired
    private PpppuiopdReportRepository ppppuiopdReportRepository;

    @Autowired
    private PdReportRepository pdReportRepository;

    @Autowired
    private PpppuiopdTaskRepository ppppuiopdTaskRepository;

    @Autowired
    private PdTaskRepository pdTaskRepository;

    // Метод удаления документа вместе со всеми версиями
    public List<String> deleteDocument(String documentName, String token) {
        List<String> messagesList = new ArrayList<String>();
        if (token == null)
            messagesList.add("Ошибка аутентификации: токен равен null");
        if (token.equals("Ошибка аутентификации: токен пуст"))
            messagesList.add("");
        Integer creator_id = null;
        if (messagesList.size() == 0)
            creator_id = documentUploadService.getCreatorId(token);
        if (creator_id == null)
            messagesList.add("Пользователь, загрузивший документ, не найден - удаление документа невозможно");
        if (messagesList.size() == 0) {
            Document document = documentRepository.findByCreatorAndName(creator_id, documentName);
            // Если директория удаляемого документа существует, удалим его версии и саму директорию
            if (document != null) {
                File fileDirectory = new File(document.getDocument_path());
                if (fileDirectory.exists()) {
                    for (File file: fileDirectory.listFiles()) {
                        file.delete();
                    }
                    fileDirectory.delete();
                    documentRepository.deleteById(document.getId());
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
            creator_id = documentUploadService.getCreatorId(token);
        if (creator_id == null)
            messagesList.add("Пользователь, загрузивший документ, не найден - удаление версии документа невозможно");
        if (messagesList.size() == 0) {
            Document document = documentRepository.findByCreatorAndName(creator_id, documentName);
            // Если директория удаляемого документа существует, удалим его версию и саму директорию
            if (document != null) {
                File fileDirectory = new File(document.getDocument_path());
                if (fileDirectory.exists()) {
                    List<DocumentVersion> documentVersions = documentVersionRepository.findByDocument(document.getId());
                    if (documentVersions.size() != 0) {
                        if (documentVersions.size() == 1) {
                            if (collateDateTimes(documentVersions.get(0).getEditionDate(), editionDateTime)) {
                                String versionPath = documentVersions.get(0).getThis_version_document_path();
                                File file = new File(versionPath);
                                file.delete();
                                if (fileDirectory.listFiles().length == 0) {
                                    fileDirectory.delete();
                                }
                                documentRepository.deleteById(document.getId());
                                messagesList.add("Документ был успешно удален вместе с последней его версией");
                            }
                            else {
                                messagesList.add("Удаляемая версия документа не найдена");
                            }
                        }
                        else if (documentVersions.size() > 1) {
                            for (int i = 0; i < documentVersions.size(); i++) {
                                if (collateDateTimes(documentVersions.get(i).getEditionDate(), editionDateTime)) {
                                    File fileVersion = new File(documentVersions.get(i).getThis_version_document_path());
                                    fileVersion.delete();
                                    documentVersionRepository.delete(documentVersions.get(i));
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
            creator_id = documentUploadService.getCreatorId(token);
        if (creator_id == null)
            messagesList.add("Пользователь, загрузивший документ, не найден - переименование документа невозможно");
        if (!documentDownloadService.getFileExtension(newDocumentName).equals(documentDownloadService.getFileExtension(oldDocumentName)))
            messagesList.add("Запрещено изменять расширение переименовываемого документа");
        if (messagesList.size() == 0) {
            Document document = documentRepository.findByCreatorAndName(creator_id, oldDocumentName);
            // Если переименовываемый документ существует в базе данных, то переименуем его
            if (document != null) {
                File documentDirectory = new File(document.getDocument_path());
                String newDocumentPath = document.getDocument_path().substring(0,
                        document.getDocument_path().lastIndexOf(File.separator) + 1) + newDocumentName;
                File newDocumentDirectoryName = new File(newDocumentPath);
                if (!newDocumentDirectoryName.exists()) {
                    documentDirectory.renameTo(newDocumentDirectoryName);
                    document.setDocument_path(newDocumentPath);
                    document.setName(newDocumentName);
                    documentService.save(document);
                    // Подкорректируем пути к версиям документа
                    List<DocumentVersion> versions = documentVersionRepository.findByDocument(document.getId());
                    for (DocumentVersion version : versions) {
                        version.setThis_version_document_path(version.getThis_version_document_path().replace(
                                oldDocumentName, newDocumentName));
                    }
                    documentVersionRepository.saveAll(versions);
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
        fromRequest = documentUploadService.convertRussianDateToSqlDateTime(fromRequest);
        if (fromDB.equals(fromRequest))
            return true;
        else
            return false;
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
            creator_id = documentUploadService.getCreatorId(token);
        if (creator_id == null)
            messagesList.add("Пользователь, загрузивший документ, не найден - изменение описания документа невозможно");
        if (messagesList.size() == 0) {
            Document document = documentRepository.findByCreatorAndName(creator_id, documentName);
            if (document != null) {
                document.setDescription(newDescription);
                documentService.save(document);
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
            creator_id = documentUploadService.getCreatorId(token);
        if (creator_id == null)
            messagesList.add("Пользователь, загрузивший документ, не найден - изменение типа документа невозможно");
        if (messagesList.size() == 0) {
            if (documentTypeRepository.getDocumentTypeByType(newType) != null) {
                Document document = documentRepository.findByCreatorAndName(creator_id, documentName);
                if (document != null) {
                    document.setType(documentTypeRepository.getDocumentTypeByType(newType).getId());
                    documentService.save(document);
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
            creator_id = documentUploadService.getCreatorId(token);
        if (creator_id == null)
            messagesList.add("Пользователь, загрузивший документ, не найден - изменение вида документа невозможно");
        if (messagesList.size() == 0) {
            if (documentKindRepository.getDocumentKindByKind(newKind) != null) {
                Document document = documentRepository.findByCreatorAndName(creator_id, documentName);

                if (document != null) {
                    document.setKind(documentKindRepository.getDocumentKindByKind(newKind).getId());
                    documentService.save(document);
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
    public List<String> editViewRights(String documentName, String newViewRightsString, String projectAreaName, String token) {
        List<String> messagesList = new ArrayList<String>();
        if (token == null)
            messagesList.add("Ошибка аутентификации: токен равен null");
        if (token.equals("Ошибка аутентификации: токен пуст"))
            messagesList.add("");
        Integer creator_id = null;
        if (messagesList.size() == 0)
            creator_id = documentUploadService.getCreatorId(token);
        if (creator_id == null)
            messagesList.add("Пользователь, загрузивший документ, не найден - изменение зоны видимости документа невозможно");
        if (messagesList.size() == 0) {
            Integer newViewRights = documentUploadService.getViewRights(newViewRightsString);
            if (newViewRights!= null) {
                Document document = documentRepository.findByCreatorAndName(creator_id, documentName);
                if (document != null) {
                    // Если зона видимости не затрагивала проект и стала затрагивать
                    if (document.getView_rights() != 6 && newViewRights == 6) {
                        ProjectArea projectArea;
                        try {
                            projectArea = projectAreaRepository.findByAreaAndAdvisor(projectAreaName, creator_id);
                        } catch (NullPointerException nullPointerException) {
                            projectArea = null;
                        }
                        if (projectArea == null) {
                            messagesList.add("Ошибка изменения зоны видимости: проектная видимость не найдена");
                        } else {
                            document.setView_rights(newViewRights);
                            documentService.save(document);
                            ViewRightsArea viewRightsArea = new ViewRightsArea(document.getId(), projectArea.getId());
                            viewRightsAreaRepository.save(viewRightsArea);
                            messagesList.add("Зона видимости документа успешно изменена");
                        }
                    }
                    // Если зона видимости затрагивала проектную область и стала затрагивать другую
                    else if (document.getView_rights() == 6 && newViewRights == 6) {
                        Project project;
                        try {
                            project = projectRepository.findByScientificAdvisorIDAndName(creator_id, projectAreaName);
                        } catch (NullPointerException nullPointerException) {
                            project = null;
                        }
                        if (project == null) {
                            messagesList.add("Ошибка изменения зоны видимости: проектная облаасть не найдена");
                        } else {
                            ViewRightsArea old;
                            try {
                                old = viewRightsAreaRepository.findByDocumentAndArea(document.getId(), project.getArea());
                            } catch (NullPointerException nullPointerException) {
                                old = null;
                            }
                            if (old == null) {
                                messagesList.add("Предыдущая проектная принадлежность документа не обнаружена");
                            }
                            else {
                                document.setView_rights(newViewRights);
                                ProjectArea projectArea = projectAreaRepository.findByAreaAndAdvisor(projectAreaName, creator_id);
                                documentService.save(document);
                                viewRightsAreaRepository.delete(old);
                                ViewRightsArea viewRightsArea = new ViewRightsArea(document.getId(), projectArea.getId());
                                viewRightsAreaRepository.save(viewRightsArea);
                                messagesList.add("Зона видимости документа успешно изменена");
                            }
                        }
                    }
                    // Если зона видимости затрагивала проект и перестала его затрагивать
                    else if (document.getView_rights() == 6 && newViewRights != 6) {
                        ViewRightsArea old;
                        try {
                            old = viewRightsAreaRepository.findByDocument(document.getId());
                        } catch (NullPointerException nullPointerException) {
                            old = null;
                        }
                        if (old == null) {
                            messagesList.add("Предыдущая проектная принадлежность документа не обнаружена");
                        }
                        else {
                            viewRightsAreaRepository.delete(old);
                            document.setView_rights(newViewRights);
                            documentService.save(document);
                            messagesList.add("Зона видимости документа успешно изменена");
                        }
                    }
                    // Изменение зоны видимости без затрагивания проекта
                    else {
                        document.setView_rights(newViewRights);
                        documentService.save(document);
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
            Integer studentID = documentUploadService.getCreatorId(token);
            if (studentID != null && versionID != null) {
                DocumentVersion documentVersion;
                try {
                    documentVersion = documentVersionRepository.findById(versionID).get();
                } catch (NoSuchElementException noSuchElementException) {
                    documentVersion = null;
                }
                if (documentVersion != null) {
                    Document document = documentRepository.findById(documentVersion.getDocument()).get();
                    int type = document.getType();
                    switch (type) {
                        case 1:
                            documentVersion.getNirTask().setStatus(4);
                            nirTaskRepository.save(documentVersion.getNirTask());
                            break;
                        case 2:
                            documentVersion.getPpppuiopdTask().setStatus(4);
                            ppppuiopdTaskRepository.save(documentVersion.getPpppuiopdTask());
                            break;
                        case 3:
                            documentVersion.getPdTask().setStatus(4);
                            pdTaskRepository.save(documentVersion.getPdTask());
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
            Integer advisorID = documentUploadService.getCreatorId(token);
            if (advisorID != null && versionID != null) {
                DocumentVersion documentVersion;
                try {
                    documentVersion = documentVersionRepository.findById(versionID).get();
                } catch (NoSuchElementException noSuchElementException) {
                    documentVersion = null;
                }
                if (documentVersion != null) {
                    Document document = documentRepository.findById(documentVersion.getDocument()).get();
                    int type = document.getType();
                    switch (type) {
                        case 1:
                            if (newStatus.equals("Одобрено") &&
                                    documentVersion.getNirTask().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getNirTask().setStatus(2);
                                nirTaskRepository.save(documentVersion.getNirTask());
                                return "Версия документа успешно прорецензирована";
                            } else if (newStatus.equals("Замечания") &&
                                    documentVersion.getNirTask().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getNirTask().setStatus(3);
                                nirTaskRepository.save(documentVersion.getNirTask());
                                return "Версия документа успешно прорецензирована";
                            } else if (documentVersion.getEditor() == advisorID) {
                                if (newStatus.equals("Одобрено")) {
                                    documentVersion.getNirTask().setStatus(2);
                                    nirTaskRepository.save(documentVersion.getNirTask());
                                    return "Вы отправили студенту свою версию задания с статусом одобрено";
                                } else if (newStatus.equals("Замечания")) {
                                    documentVersion.getNirTask().setStatus(3);
                                    nirTaskRepository.save(documentVersion.getNirTask());
                                    return "Вы отправили студенту свою версию задания с статусом замечания";
                                }
                            }
                        case 2:
                            if (newStatus.equals("Одобрено") &&
                                    documentVersion.getPpppuiopdTask().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getPpppuiopdTask().setStatus(2);
                                ppppuiopdTaskRepository.save(documentVersion.getPpppuiopdTask());
                                return "Версия документа успешно прорецензирована";
                            } else if (newStatus.equals("Замечания") &&
                                    documentVersion.getPpppuiopdTask().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getPpppuiopdTask().setStatus(3);
                                ppppuiopdTaskRepository.save(documentVersion.getPpppuiopdTask());
                                return "Версия документа успешно прорецензирована";
                            } else if (documentVersion.getEditor() == advisorID) {
                                if (newStatus.equals("Одобрено")) {
                                    documentVersion.getPpppuiopdTask().setStatus(2);
                                    ppppuiopdTaskRepository.save(documentVersion.getPpppuiopdTask());
                                    return "Вы отправили студенту свою версию задания с статусом одобрено";
                                } else if (newStatus.equals("Замечания")) {
                                    documentVersion.getPpppuiopdTask().setStatus(3);
                                    ppppuiopdTaskRepository.save(documentVersion.getPpppuiopdTask());
                                    return "Вы отправили студенту свою версию задания с статусом замечания";
                                }
                            }
                        case 3:
                            if (newStatus.equals("Одобрено") &&
                                    documentVersion.getPdTask().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getPdTask().setStatus(2);
                                pdTaskRepository.save(documentVersion.getPdTask());
                                return "Версия документа успешно прорецензирована";
                            } else if (newStatus.equals("Замечания") &&
                                    documentVersion.getPdTask().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getPdTask().setStatus(3);
                                pdTaskRepository.save(documentVersion.getPdTask());
                                return "Версия документа успешно прорецензирована";
                            } else if (documentVersion.getEditor() == advisorID) {
                                if (newStatus.equals("Одобрено")) {
                                    documentVersion.getPdTask().setStatus(2);
                                    pdTaskRepository.save(documentVersion.getPdTask());
                                    return "Вы отправили студенту свою версию задания с статусом одобрено";
                                } else if (newStatus.equals("Замечания")) {
                                    documentVersion.getPdTask().setStatus(3);
                                    pdTaskRepository.save(documentVersion.getPdTask());
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
        Integer studentID = documentUploadService.getCreatorId(token);
        DocumentVersion documentVersion;
        try {
            documentVersion = documentVersionRepository.findById(versionID).get();
            if (studentID != null) {
                if (documentVersion.getEditor() == studentID) {
                    Document document = documentRepository.findById(documentVersion.getDocument()).get();
                    int type = document.getType();
                    switch (type) {
                        case 1:
                            if (documentVersion.getNirTask().getDocumentStatus().getStatus().equals("Не отправлено")
                                    || documentVersion.getNirTask().getDocumentStatus().getStatus().equals("Замечания")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                documentVersionRepository.delete(documentVersion);
                                return "Версия документа успешно удалена";
                            } else {
                                return "Запрещено удалять версии документа после его отправки";
                            }
                        case 2:
                            if (documentVersion.getPpppuiopdTask().getDocumentStatus().getStatus().equals("Не отправлено")
                                    || documentVersion.getPpppuiopdTask().getDocumentStatus().getStatus().equals("Замечания")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                documentVersionRepository.delete(documentVersion);
                                return "Версия документа успешно удалена";
                            } else {
                                return "Запрещено удалять версии документа после его отправки";
                            }
                        case 3:
                            if (documentVersion.getPdTask().getDocumentStatus().getStatus().equals("Не отправлено")
                                    || documentVersion.getPdTask().getDocumentStatus().getStatus().equals("Замечания")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                documentVersionRepository.delete(documentVersion);
                                return "Версия документа успешно удалена";
                            } else {
                                return "Запрещено удалять версии документа после его отправки";
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
        Integer advisorID = documentUploadService.getCreatorId(token);
        DocumentVersion documentVersion;
        try {
            documentVersion = documentVersionRepository.findById(versionID).get();
            if (advisorID != null && studentID != null) {
                AssociatedStudents associatedStudent =
                        associatedStudentsRepository.findByScientificAdvisorAndStudent(advisorID, studentID);
                if (associatedStudent != null) {
                    Document document = documentRepository.findById(documentVersion.getDocument()).get();
                    int type = document.getType();
                    switch (type) {
                        case 1:
                            if (documentVersion.getEditor() == advisorID &&
                                    documentVersion.getNirTask().getDocumentStatus().getStatus().equals("Не отправлено")
                                    || documentVersion.getNirTask().getDocumentStatus().getStatus().equals("Замечания")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                documentVersionRepository.delete(documentVersion);
                                return "Версия документа успешно удалена";
                            } else {
                                return "Вы можете удалить только свою неотправленную версию задания своего студента";
                            }
                        case 2:
                            if (documentVersion.getEditor() == advisorID &&
                                    documentVersion.getPpppuiopdTask().getDocumentStatus().getStatus().equals("Не отправлено")
                                    || documentVersion.getPpppuiopdTask().getDocumentStatus().getStatus().equals("Замечания")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                documentVersionRepository.delete(documentVersion);
                                return "Версия документа успешно удалена";
                            } else {
                                return "Вы можете удалить только свою неотправленную версию задания своего студента";
                            }
                        case 3:
                            if (documentVersion.getEditor() == advisorID &&
                                    documentVersion.getPdTask().getDocumentStatus().getStatus().equals("Не отправлено")
                                    || documentVersion.getPdTask().getDocumentStatus().getStatus().equals("Замечания")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                documentVersionRepository.delete(documentVersion);
                                return "Версия документа успешно удалена";
                            } else {
                                return "Вы можете удалить только свою неотправленную версию задания своего студента";
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
            Integer studentID = documentUploadService.getCreatorId(token);
            if (studentID != null && versionID != null) {
                DocumentVersion documentVersion;
                try {
                    documentVersion = documentVersionRepository.findById(versionID).get();
                } catch (NoSuchElementException noSuchElementException) {
                    documentVersion = null;
                }
                if (documentVersion != null) {
                    Document document = documentRepository.findById(documentVersion.getDocument()).get();
                    int type = document.getType();
                    switch (type) {
                        case 1:
                            documentVersion.getNirReport().setNirReportStatus(4);
                            nirReportRepository.save(documentVersion.getNirReport());
                            break;
                        case 2:
                            documentVersion.getPpppuiopdReport().setPpppuiopdReportStatus(4);
                            ppppuiopdReportRepository.save(documentVersion.getPpppuiopdReport());
                            break;
                        case 3:
                            documentVersion.getPdReport().setPdReportStatus(4);
                            pdReportRepository.save(documentVersion.getPdReport());
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
        if (newStatus.equals("Одобрено") || newStatus.equals("Замечания")) {
            Integer advisorID = documentUploadService.getCreatorId(token);
            if (advisorID != null && versionID != null) {
                DocumentVersion documentVersion;
                try {
                    documentVersion = documentVersionRepository.findById(versionID).get();
                } catch (NoSuchElementException noSuchElementException) {
                    documentVersion = null;
                }
                if (documentVersion != null) {
                    Document document = documentRepository.findById(documentVersion.getDocument()).get();
                    int type = document.getType();
                    switch (type) {
                        case 1:
                            if (newStatus.equals("Одобрено") &&
                                    documentVersion.getNirReport().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getNirReport().setNirReportStatus(2);
                                nirReportRepository.save(documentVersion.getNirReport());
                                return "Версия документа успешно прорецензирована";
                            } else if (newStatus.equals("Замечания") &&
                                    documentVersion.getNirReport().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getNirReport().setNirReportStatus(3);
                                nirReportRepository.save(documentVersion.getNirReport());
                                return "Версия документа успешно прорецензирована";
                            }
                            else if (documentVersion.getEditor() == advisorID) {
                                if (newStatus.equals("Одобрено")) {
                                    documentVersion.getNirReport().setNirReportStatus(2);
                                    nirReportRepository.save(documentVersion.getNirReport());
                                    return "Вы отправили студенту свою версию задания с статусом одобрено";
                                } else if (newStatus.equals("Замечания")) {
                                    documentVersion.getNirReport().setNirReportStatus(3);
                                    nirReportRepository.save(documentVersion.getNirReport());
                                    return "Вы отправили студенту свою версию задания с статусом замечания";
                                }
                            }
                        case 2:
                            if (newStatus.equals("Одобрено") &&
                                    documentVersion.getPpppuiopdReport().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getPpppuiopdReport().setPpppuiopdReportStatus(2);
                                ppppuiopdReportRepository.save(documentVersion.getPpppuiopdReport());
                                return "Версия документа успешно прорецензирована";
                            } else if (newStatus.equals("Замечания") &&
                                    documentVersion.getPpppuiopdReport().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getPpppuiopdReport().setPpppuiopdReportStatus(3);
                                ppppuiopdReportRepository.save(documentVersion.getPpppuiopdReport());
                                return "Версия документа успешно прорецензирована";
                            }
                            else if (documentVersion.getEditor() == advisorID) {
                                if (newStatus.equals("Одобрено")) {
                                    documentVersion.getPpppuiopdReport().setPpppuiopdReportStatus(2);
                                    ppppuiopdReportRepository.save(documentVersion.getPpppuiopdReport());
                                    return "Вы отправили студенту свою версию задания с статусом одобрено";
                                } else if (newStatus.equals("Замечания")) {
                                    documentVersion.getPpppuiopdReport().setPpppuiopdReportStatus(3);
                                    ppppuiopdReportRepository.save(documentVersion.getPpppuiopdReport());
                                    return "Вы отправили студенту свою версию задания с статусом замечания";
                                }
                            }
                        case 3:
                            if (newStatus.equals("Одобрено") &&
                                    documentVersion.getPdReport().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getPdReport().setPdReportStatus(2);
                                pdReportRepository.save(documentVersion.getPdReport());
                                return "Версия документа успешно прорецензирована";
                            } else if (newStatus.equals("Замечания") &&
                                    documentVersion.getPdReport().getDocumentStatus().getStatus().equals("Рассматривается")) {
                                documentVersion.getPdReport().setPdReportStatus(3);
                                pdReportRepository.save(documentVersion.getPdReport());
                                return "Версия документа успешно прорецензирована";
                            }
                            else if (documentVersion.getEditor() == advisorID) {
                                if (newStatus.equals("Одобрено")) {
                                    documentVersion.getPdReport().setPdReportStatus(2);
                                    pdReportRepository.save(documentVersion.getPdReport());
                                    return "Вы отправили студенту свою версию задания с статусом одобрено";
                                } else if (newStatus.equals("Замечания")) {
                                    documentVersion.getPdReport().setPdReportStatus(3);
                                    pdReportRepository.save(documentVersion.getPdReport());
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

    // Студент удаляет версию отчета
    public String studentDeleteReportVersion(String token, Integer versionID) {
        Integer studentID = documentUploadService.getCreatorId(token);
        DocumentVersion documentVersion;
        try {
            documentVersion = documentVersionRepository.findById(versionID).get();
            if (studentID != null) {
                if (documentVersion.getEditor() == studentID) {
                    Document document = documentRepository.findById(documentVersion.getDocument()).get();
                    int type = document.getType();
                    switch (type) {
                        case 1:
                            if (documentVersion.getNirReport().getDocumentStatus().getStatus().equals("Не отправлено")
                                    || documentVersion.getNirReport().getDocumentStatus().getStatus().equals("Замечания")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                documentVersionRepository.delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            } else {
                                return "Запрещено удалять версии отчета после его отправки";
                            }
                        case 2:
                            if (documentVersion.getPpppuiopdReport().getDocumentStatus().getStatus().equals("Не отправлено")
                                    || documentVersion.getPpppuiopdReport().getDocumentStatus().getStatus().equals("Замечания")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                documentVersionRepository.delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            } else {
                                return "Запрещено удалять версии отчета после его отправки";
                            }
                        case 3:
                            if (documentVersion.getPdReport().getDocumentStatus().getStatus().equals("Не отправлено")
                                    || documentVersion.getPdReport().getDocumentStatus().getStatus().equals("Замечания")) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                documentVersionRepository.delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            } else {
                                return "Запрещено удалять версии отчета после его отправки";
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
        Integer advisorID = documentUploadService.getCreatorId(token);
        DocumentVersion documentVersion;
        try {
            documentVersion = documentVersionRepository.findById(versionID).get();
            if (advisorID != null && studentID != null) {
                AssociatedStudents associatedStudent =
                        associatedStudentsRepository.findByScientificAdvisorAndStudent(advisorID, studentID);
                if (associatedStudent != null) {
                    Document document = documentRepository.findById(documentVersion.getDocument()).get();
                    int type = document.getType();
                    switch (type) {
                        case 1:
                            if (documentVersion.getEditor() == advisorID &&
                                    (documentVersion.getNirReport().getDocumentStatus().getStatus().equals("Не отправлено")
                                            || documentVersion.getNirReport().getDocumentStatus().getStatus().equals("Замечания"))) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                documentVersionRepository.delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            } else {
                                return "Вы можете удалить только свою неотправленную версию отчёта своего студента";
                            }
                        case 2:
                            if (documentVersion.getEditor() == advisorID &&
                                    (documentVersion.getPpppuiopdReport().getDocumentStatus().getStatus().equals("Не отправлено")
                                            || documentVersion.getPpppuiopdReport().getDocumentStatus().getStatus().equals("Замечания"))) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                documentVersionRepository.delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            } else {
                                return "Вы можете удалить только свою неотправленную версию отчёта своего студента";
                            }
                        case 3:
                            if (documentVersion.getEditor() == advisorID &&
                                    (documentVersion.getPdReport().getDocumentStatus().getStatus().equals("Не отправлено")
                                            || documentVersion.getPdReport().getDocumentStatus().getStatus().equals("Замечания"))) {
                                File deleteFile = new File(documentVersion.getThis_version_document_path());
                                deleteFile.delete();
                                documentVersionRepository.delete(documentVersion);
                                return "Версия отчета успешно удалена";
                            } else {
                                return "Вы можете удалить только свою неотправленную версию отчёта своего студента";
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
                0, 0, 0, 0);
        Document nirTask;
        Document nirReport;
        Document ppppuiopdTask;
        Document ppppuiopdReport;
        Document pdTask;
        Document pdReport;
        // TODO Сделать обработку остальных документов когда они появятся
        try {
            // НИР
            if (documentRepository.findByTypeAndKindAndCreator(1, 2, studentID).size() == 1) {
                nirTask = documentRepository.findByTypeAndKindAndCreator(1, 2, studentID).get(0);
                List<DocumentVersion> nirTaskVersions = documentVersionRepository.findByDocument(nirTask.getId());
                // Пройдем по версиям задания студента
                for (DocumentVersion nirTaskVersion : nirTaskVersions) {
                    if (nirTaskVersion.getNirTask().getDocumentStatus().getStatus().equals("Одобрено")) {
                        statusView.setNirTaskStatus(1);
                    }
                }
            }
            if (documentRepository.findByTypeAndKindAndCreator(1, 3, studentID).size() == 1) {
                nirReport = documentRepository.findByTypeAndKindAndCreator(1, 3, studentID).get(0);
                List<DocumentVersion> nirReportVersions = documentVersionRepository.findByDocument(nirReport.getId());
                // Пройдем по версиям отчёта студента
                for (DocumentVersion nirReportVersion: nirReportVersions) {
                    if (nirReportVersion.getNirReport().getDocumentStatus().getStatus().equals("Одобрено")) {
                        statusView.setNirReportStatus(1);
                    }
                }
            }
            // ППППУиОПД
            if (documentRepository.findByTypeAndKindAndCreator(2, 2, studentID).size() == 1) {
                ppppuiopdTask = documentRepository.findByTypeAndKindAndCreator(2, 2, studentID).get(0);
                List<DocumentVersion> ppppuiopdTaskVersions = documentVersionRepository.findByDocument(ppppuiopdTask.getId());
                // Пройдем по версиям задания студента
                for (DocumentVersion ppppuiopdTaskVersion : ppppuiopdTaskVersions) {
                    if (ppppuiopdTaskVersion.getPpppuiopdTask().getDocumentStatus().getStatus().equals("Одобрено")) {
                        statusView.setPpppuipdTaskStatus(1);
                    }
                }
            }
            if (documentRepository.findByTypeAndKindAndCreator(2, 3, studentID).size() == 1) {
                ppppuiopdReport = documentRepository.findByTypeAndKindAndCreator(2, 3, studentID).get(0);
                List<DocumentVersion> ppppuiopdReportVersions = documentVersionRepository.findByDocument(ppppuiopdReport.getId());
                // Пройдем по версиям отчёта студента
                for (DocumentVersion ppppuiopdReportVersion: ppppuiopdReportVersions) {
                    if (ppppuiopdReportVersion.getPpppuiopdReport().getDocumentStatus().getStatus().equals("Одобрено")) {
                        statusView.setPpppuipdReportStatus(1);
                    }
                }
            }
            // ПП
            if (documentRepository.findByTypeAndKindAndCreator(3, 2, studentID).size() == 1) {
                pdTask = documentRepository.findByTypeAndKindAndCreator(3, 2, studentID).get(0);
                List<DocumentVersion> pdTaskVersions = documentVersionRepository.findByDocument(pdTask.getId());
                // Пройдем по версиям задания студента
                for (DocumentVersion pdTaskVersion : pdTaskVersions) {
                    if (pdTaskVersion.getPpppuiopdTask().getDocumentStatus().getStatus().equals("Одобрено")) {
                        statusView.setPpTaskStatus(1);
                    }
                }
            }
            if (documentRepository.findByTypeAndKindAndCreator(3, 3, studentID).size() == 1) {
                pdReport = documentRepository.findByTypeAndKindAndCreator(3, 3, studentID).get(0);
                List<DocumentVersion> ppppuiopdReportVersions = documentVersionRepository.findByDocument(pdReport.getId());
                // Пройдем по версиям отчёта студента
                for (DocumentVersion ppppuiopdReportVersion: ppppuiopdReportVersions) {
                    if (ppppuiopdReportVersion.getPdReport().getDocumentStatus().getStatus().equals("Одобрено")) {
                        statusView.setPpReportStatus(1);
                    }
                }
            }
            return statusView;
        } catch (NullPointerException nullPointerException) {
            return new StudentDocumentsStatusView(-1, -1, -1,
                    -1, -1, -1, -1, -1, -1,
                    -1, -1, -1);
        }
    }

    // Одобрить шаблон
    public String approveTemplate(String token, Integer documentID) {
        Integer creatorID = documentUploadService.getCreatorId(token);
        Document template;
        try {
            template = documentRepository.findById(documentID).get();
            if (template.getKind() == 5) {
                TemplateProperties templateProperties;
                try {
                    templateProperties = templatePropertiesRepository.findById(template.getId()).get();
                    templateProperties.setApproved(true);
                    templatePropertiesRepository.save(templateProperties);
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
        Integer creatorID = documentUploadService.getCreatorId(token);
        Document order;
        try {
            order = documentRepository.findById(documentID).get();
            if (order.getKind() == 1) {
                OrderProperties orderProperties;
                try {
                    orderProperties = orderPropertiesRepository.findById(order.getId()).get();
                    orderProperties.setApproved(true);
                    orderPropertiesRepository.save(orderProperties);
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
}