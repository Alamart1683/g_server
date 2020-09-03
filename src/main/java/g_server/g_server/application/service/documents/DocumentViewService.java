package g_server.g_server.application.service.documents;

import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.entity.users.AssociatedStudents;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.entity.view.DocumentVersionView;
import g_server.g_server.application.entity.view.DocumentView;
import g_server.g_server.application.repository.documents.DocumentVersionRepository;
import g_server.g_server.application.repository.users.AssociatedStudentsRepository;
import g_server.g_server.application.repository.users.UsersRolesRepository;
import g_server.g_server.application.service.documents.crud.DocumentService;
import g_server.g_server.application.service.documents.crud.DocumentVersionService;
import g_server.g_server.application.service.users.AssociatedStudentsService;
import g_server.g_server.application.service.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
// Сервис ответственный за представление
// и разграничение документов пользователям
// TODO Здесь должен быть реализован сервис, который
// TODO позволит получать студентам и преподам те списки
// TODO документов, что им дозволено видеть
public class DocumentViewService {
    @Autowired
    private UsersService usersService;

    @Autowired
    private AssociatedStudentsService associatedStudentsService;

    @Autowired
    private AssociatedStudentsRepository associatedStudentsRepository;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UsersRolesRepository usersRolesRepository;

    @Autowired
    private DocumentVersionRepository documentVersionRepository;

    // Проверить, может ли студент видеть данный документ
    private boolean checkStudentDocumentView(Users student, Users advisor, Document document) {
        // TODO Внимание, метод проверки работает только под текущий вариант зон видимости и ролей,
        // TODO иначе его придется переделывать
        // Определим уровень видимости документа
        Integer documentView = document.getView_rights();
        // Проверим соответствие ролей
        Integer advisorRoleID = usersRolesRepository.findUsersRolesByUserId(advisor.getId()).getRoleId();
        Integer studentRoleID = usersRolesRepository.findUsersRolesByUserId(student.getId()).getRoleId();
        if (studentRoleID == 1 && (advisorRoleID == 2 || advisorRoleID == 3)) {
            if (documentView > 2) {
                // Документ могут видеть только студенты данного научного руководителя
                if (documentView == 3 || documentView == 4) {
                    AssociatedStudents associatedStudent = associatedStudentsRepository.
                            findByScientificAdvisorAndStudent(advisor.getId(), student.getId());
                    if (associatedStudent.isAccepted()) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                else if (documentView == 5) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    // Проверить, может ли научный руководитель/заведующий кафедрой видеть данный документ
    private boolean checkAdvisorDocumentView(Users lookingAdvisor, Users documentCreatorAdvisor, Document document) {
        // Определим уровень видимости документа
        Integer documentView = document.getView_rights();
        // Проверим соответствие ролей
        Integer lookingAdvisorRoleID = usersRolesRepository.
                findUsersRolesByUserId(lookingAdvisor.getId()).getRoleId();
        Integer documentCreatorAdvisorRoleID = usersRolesRepository.
                findUsersRolesByUserId(documentCreatorAdvisor.getId()).getRoleId();
        if ((lookingAdvisorRoleID == 2 || lookingAdvisorRoleID == 3) &&
            documentCreatorAdvisorRoleID == 2 || documentCreatorAdvisorRoleID == 3) {
            // Если зона видимости документа только для создателя и других НР или для всех
            // TODO Предполагается, что документы с правом видимости для всех может загружать
            // TODO только заведующий кафедрой и это документы типа приказов и всего такого
            // TODO этот момент надо учесть при загрузке прав видимости документов на клиент
            // TODO то есть в идеале надо сделать отдельную выдачу списка типов документов и прав видимостей
            // TODO для научных руководителей и заведующего кафедрой
            if (documentView < 3 || documentView == 5) {
                // Если документ может видеть только его создатель
                if (documentView == 1) {
                    // Если желающий увидеть документ НР сам его загрузил
                    if (lookingAdvisor.getId() == documentCreatorAdvisor.getId()) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                // Если документ могут видеть все НР
                else if (documentView == 2 || documentView == 4 || documentView == 5) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    // Сформировать список документов в зависимсоти от роли пользователя
    public List<DocumentView> getUserDocumentView(String token) {
        if (token == null) {
            return null;
        }
        if (token.equals("")) {
            return null;
        }
        Integer userID = associatedStudentsService.getUserId(token);
        if (userID == null) {
            return null;
        }
        Integer userRoleID = usersRolesRepository.findUsersRolesByUserId(userID).getRoleId();
        Users user = usersService.findById(associatedStudentsService.getUserId(token)).get();
        // Если роль пользователя корректна, сформируем для него список документов
        if (userRoleID != null && user != null) {
            // Студент
            if (userRoleID == 1) {
                return getStudentDocumentView(user);
            }
            // НР или зав. кафедрой
            else if (userRoleID == 2 || userRoleID == 3) {
                return getAdvisorDocumentView(user);
            }
            // Админ или рут
            else if (userRoleID == 4 || userRoleID == 5) {
                return getAdminDocumentView(user);
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    // Сформировать список видимых документов для студента
    private List<DocumentView> getStudentDocumentView(Users user) {
        List<Document> documents = documentService.findAll();
        List<DocumentView> documentViewList = new ArrayList<>();
        for (Document document: documents) {
            // Поскольку студенты не могут видеть версии документов, просто не будем добавлять их в представление
            if (checkStudentDocumentView(user, document.getUser(), document)) {
                DocumentView documentView = new DocumentView(document, null);
                documentViewList.add(documentView);
            }
        }
        return documentViewList;
    }

    // Сформировать список видимых документов для НР и зав. кафедрой
    private List<DocumentView> getAdvisorDocumentView(Users user) {
        List<Document> documents = documentService.findAll();
        List<DocumentView> documentViewList = new ArrayList<>();
        for (Document document: documents) {
            if (checkAdvisorDocumentView(user, document.getUser(), document)) {
                List<DocumentVersion> documentVersions = documentVersionRepository.findByDocument(document.getId());
                List<DocumentVersionView> documentVersionViews = new ArrayList<>();
                for (DocumentVersion documentVersion: documentVersions) {
                    documentVersionViews.add(new DocumentVersionView(documentVersion));
                }
                DocumentView documentView = new DocumentView(document, documentVersionViews);
                documentViewList.add(documentView);
            }
        }
        return documentViewList;
    }

    // Сформировать список видимых документов для админа и выше
    private List<DocumentView> getAdminDocumentView(Users user) {
        List<Document> documents = documentService.findAll();
        List<DocumentView> documentViewList = new ArrayList<>();
        for (Document document: documents) {
            List<DocumentVersion> documentVersions = documentVersionRepository.findByDocument(document.getId());
            List<DocumentVersionView> documentVersionViews = new ArrayList<>();
            for (DocumentVersion documentVersion: documentVersions) {
                documentVersionViews.add(new DocumentVersionView(documentVersion));
            }
            DocumentView documentView = new DocumentView(document, documentVersionViews);
            documentViewList.add(documentView);
        }
        return documentViewList;
    }
}