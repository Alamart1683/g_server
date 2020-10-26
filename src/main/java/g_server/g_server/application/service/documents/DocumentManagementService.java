package g_server.g_server.application.service.documents;

import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.entity.documents.ViewRightsProject;
import g_server.g_server.application.entity.project.Project;
import g_server.g_server.application.entity.users.AssociatedStudents;
import g_server.g_server.application.repository.documents.*;
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
    private ProjectRepository projectRepository;

    @Autowired
    private ViewRightsProjectRepository viewRightsProjectRepository;

    @Autowired
    private NirTaskRepository nirTaskRepository;

    @Autowired
    private AssociatedStudentsRepository associatedStudentsRepository;

    @Autowired
    private NirReportRepository nirReportRepository;

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
    public List<String> editViewRights(String documentName, String newViewRightsString, String projectName, String token) {
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
                        Project project;
                        try {
                            project = projectRepository.findByScientificAdvisorIDAndName(creator_id, projectName);
                        } catch (NullPointerException nullPointerException) {
                            project = null;
                        }
                        if (project == null) {
                            messagesList.add("Ошибка изменения зоны видимости: проект не найден");
                        } else {
                            document.setView_rights(newViewRights);
                            documentService.save(document);
                            ViewRightsProject viewRightsProject = new ViewRightsProject(project.getId(), document.getId());
                            viewRightsProjectRepository.save(viewRightsProject);
                            messagesList.add("Зона видимости документа успешно изменена");
                        }
                    }
                    // Если зона видимости затрагивала проект и стала затрагивать другой
                    else if (document.getView_rights() == 6 && newViewRights == 6) {
                        Project project;
                        try {
                            project = projectRepository.findByScientificAdvisorIDAndName(creator_id, projectName);
                        } catch (NullPointerException nullPointerException) {
                            project = null;
                        }
                        if (project == null) {
                            messagesList.add("Ошибка изменения зоны видимости: проект не найден");
                        } else {
                            ViewRightsProject old;
                            try {
                                old = viewRightsProjectRepository.findByDocument(document.getId());
                            } catch (NullPointerException nullPointerException) {
                                old = null;
                            }
                            if (old == null) {
                                messagesList.add("Предыдущая проектная принадлежность документа не обнаружена");
                            }
                            else {
                                document.setView_rights(newViewRights);
                                documentService.save(document);
                                ViewRightsProject viewRightsProject = new ViewRightsProject(project.getId(), document.getId());
                                viewRightsProjectRepository.save(viewRightsProject);
                                messagesList.add("Зона видимости документа успешно изменена");
                            }
                        }
                    }
                    // Если зона видимости затрагивала проект и перестала его затрагивать
                    else if (document.getView_rights() == 6 && newViewRights != 6) {
                        ViewRightsProject old;
                        try {
                            old = viewRightsProjectRepository.findByDocument(document.getId());
                        } catch (NullPointerException nullPointerException) {
                            old = null;
                        }
                        if (old == null) {
                            messagesList.add("Предыдущая проектная принадлежность документа не обнаружена");
                        }
                        else {
                            viewRightsProjectRepository.deleteById(old.getId());
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
                    documentVersion.getNirTask().setStatus(4);
                    nirTaskRepository.save(documentVersion.getNirTask());
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
                    if (documentVersion.getNirTask().getDocumentStatus().getStatus().equals("Не отправлено")) {
                        File deleteFile = new File(documentVersion.getThis_version_document_path());
                        deleteFile.delete();
                        documentVersionRepository.delete(documentVersion);
                        return "Версия документа успешно удалена";
                    } else {
                        return "Запрещено удалять версии документа после его отправки";
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
                    if (documentVersion.getEditor() == advisorID &&
                            documentVersion.getNirTask().getDocumentStatus().getStatus().equals("Не отправлено")) {
                        File deleteFile = new File(documentVersion.getThis_version_document_path());
                        deleteFile.delete();
                        documentVersionRepository.delete(documentVersion);
                        return "Версия документа успешно удалена";
                    } else {
                        return "Вы можете удалить только свою неотправленную версию задания своего студента";
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
                    documentVersion.getNirReport().setNirReportStatus(4);
                    nirReportRepository.save(documentVersion.getNirReport());
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
                    if (documentVersion.getNirReport().getDocumentStatus().getStatus().equals("Не отправлено")) {
                        File deleteFile = new File(documentVersion.getThis_version_document_path());
                        deleteFile.delete();
                        documentVersionRepository.delete(documentVersion);
                        return "Версия отчета успешно удалена";
                    } else {
                        return "Запрещено удалять версии отчета после его отправки";
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

    // Научный руководитель удаляет версию задания
    public String advisorDeleteReportVersion(String token, Integer versionID, Integer studentID) {
        Integer advisorID = documentUploadService.getCreatorId(token);
        DocumentVersion documentVersion;
        try {
            documentVersion = documentVersionRepository.findById(versionID).get();
            if (advisorID != null && studentID != null) {
                AssociatedStudents associatedStudent =
                        associatedStudentsRepository.findByScientificAdvisorAndStudent(advisorID, studentID);
                if (associatedStudent != null) {
                    if (documentVersion.getEditor() == advisorID &&
                            documentVersion.getNirReport().getDocumentStatus().getStatus().equals("Не отправлено")) {
                        File deleteFile = new File(documentVersion.getThis_version_document_path());
                        deleteFile.delete();
                        documentVersionRepository.delete(documentVersion);
                        return "Версия отчета успешно удалена";
                    } else {
                        return "Вы можете удалить только свою неотправленную версию отчёта своего студента";
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
}