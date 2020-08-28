package g_server.g_server.application.service.documents;

import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.repository.documents.DocumentRepository;
import g_server.g_server.application.repository.documents.DocumentVersionRepository;
import g_server.g_server.application.service.documents.crud.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    boolean collateDateTimes(String fromDB, String fromRequest) {
        fromRequest = documentUploadService.convertRussianDateToSqlDateTime(fromRequest);
        if (fromDB.equals(fromRequest))
            return true;
        else return false;
    }
}