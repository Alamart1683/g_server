package g_server.g_server.application.service.documents;

import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.entity.documents.ViewRightsProject;
import g_server.g_server.application.entity.project.OccupiedStudents;
import g_server.g_server.application.entity.project.Project;
import g_server.g_server.application.entity.users.AssociatedStudents;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.entity.view.DocumentVersionView;
import g_server.g_server.application.entity.view.DocumentView;
import g_server.g_server.application.entity.view.TaskDocumentVersionView;
import g_server.g_server.application.repository.documents.DocumentRepository;
import g_server.g_server.application.repository.documents.DocumentVersionRepository;
import g_server.g_server.application.repository.documents.NirTaskRepository;
import g_server.g_server.application.repository.documents.ViewRightsProjectRepository;
import g_server.g_server.application.repository.project.OccupiedStudentsRepository;
import g_server.g_server.application.repository.project.ProjectRepository;
import g_server.g_server.application.repository.users.AssociatedStudentsRepository;
import g_server.g_server.application.repository.users.UsersRolesRepository;
import g_server.g_server.application.service.documents.crud.DocumentService;
import g_server.g_server.application.service.users.AssociatedStudentsService;
import g_server.g_server.application.service.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
// Сервис ответственный за представление
// и разграничение документов пользователям\
// TODO Обработать новую, седьмую область видимости
// TODO Доработать видимость проекта если это необходимо
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

    @Autowired
    private ViewRightsProjectRepository viewRightsProjectRepository;

    @Autowired
    private OccupiedStudentsRepository occupiedStudentsRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentProcessorService documentProcessorService;

    @Autowired
    private NirTaskRepository nirTaskRepository;

    // Проверить, может ли студент видеть данный документ
    private boolean checkStudentDocumentView(Users student, Users documentCreator, Document document) {
        // TODO Внимание, метод проверки работает только под текущий вариант зон видимости и ролей,
        // TODO иначе его придется переделывать
        // Определим уровень видимости документа
        Integer documentView = document.getView_rights();
        // Проверим соответствие ролей
        Integer documentCreatorRoleID = usersRolesRepository.findUsersRolesByUserId(documentCreator.getId()).getRoleId();
        Integer studentRoleID = usersRolesRepository.findUsersRolesByUserId(student.getId()).getRoleId();
        if (studentRoleID == 1 && (documentCreatorRoleID == 1 || documentCreatorRoleID == 2 || documentCreatorRoleID == 3)) {
            if (documentView > 2) {
                // Документ могут видеть только студенты данного научного руководителя
                if (documentView == 3 || documentView == 4) {
                    AssociatedStudents associatedStudent = associatedStudentsRepository.
                            findByScientificAdvisorAndStudent(documentCreator.getId(), student.getId());
                    if (associatedStudent != null) {
                        if (associatedStudent.isAccepted()) {
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
                else if (documentView == 5) {
                    return true;
                }
                else if (documentView == 6) {
                    Integer documentID = document.getId();
                    ViewRightsProject viewRightsProject = viewRightsProjectRepository.findByDocument(documentID);
                    List<OccupiedStudents> occupiedStudents =
                            occupiedStudentsRepository.findAllByProjectID(viewRightsProject.getProject());
                    for (OccupiedStudents occupiedStudent: occupiedStudents) {
                        if (occupiedStudent.getStudentID() == student.getId()) {
                            return true;
                        }
                    }
                    return false;
                }
                else if (documentView == 7) {
                    if (student.getId() == document.getCreator()) {
                        return true;
                    } else {
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
        else {
            return false;
        }
    }

    // Проверить, может ли научный руководитель/заведующий кафедрой видеть данный документ
    private boolean checkAdvisorDocumentView(Users lookingAdvisor, Users documentCreator, Document document) {
        // Определим уровень видимости документа
        Integer documentView = document.getView_rights();
        // Проверим соответствие ролей
        Integer lookingAdvisorRoleID = usersRolesRepository.
                findUsersRolesByUserId(lookingAdvisor.getId()).getRoleId();
        Integer documentCreatorRoleID = usersRolesRepository.
                findUsersRolesByUserId(documentCreator.getId()).getRoleId();
        if ((lookingAdvisorRoleID == 2 || lookingAdvisorRoleID == 3) &&
                (documentCreatorRoleID == 1 || documentCreatorRoleID == 2 || documentCreatorRoleID == 3)) {
            // Если зона видимости документа только для создателя и других НР или для всех
            // TODO Предполагается, что документы с правом видимости для всех может загружать
            // TODO только заведующий кафедрой и это документы типа приказов и всего такого
            // TODO этот момент надо учесть при загрузке прав видимости документов на клиент
            // TODO то есть в идеале надо сделать отдельную выдачу списка типов документов и прав видимостей
            // TODO для научных руководителей и заведующего кафедрой
            if (documentView > 0 || documentView < 8) {
                // Если документ может видеть только его создатель или документ привязан к проекту и
                // его может видеть созадтель проекта
                if (documentView == 1 || documentView == 6) {
                    // Если желающий увидеть документ НР сам его загрузил
                    if (lookingAdvisor.getId() == documentCreator.getId()) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                // Если документ могут видеть все НР
                else if (documentView == 3) {
                    // Если желающий увидеть документ сам его загрузил
                    if (lookingAdvisor.getId() == documentCreator.getId()) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                // Если документ загрузил студент и смотрящий - его научный руководитель
                else if (documentView == 7) {
                    AssociatedStudents associatedStudent;
                    try {
                        associatedStudent = associatedStudentsRepository.findByScientificAdvisorAndStudent(
                                lookingAdvisor.getId(), documentCreator.getId());
                    } catch (NullPointerException nullPointerException) {
                        associatedStudent = null;
                    }
                    if (associatedStudent != null) {
                        return true;
                    } else {
                        return false;
                    }
                }
                else {
                    return true;
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

    // Сформировать список версий загруженного задания студента
    public List<TaskDocumentVersionView> getStudentTaskVersions(String token, String taskType) {
        Integer studentID = associatedStudentsService.getUserId(token);
        Integer advisorID = associatedStudentsRepository.findByStudent(studentID).getScientificAdvisor();
        if (studentID != null && advisorID != null) {
            List<TaskDocumentVersionView> taskVersionView = new ArrayList<>();
            Integer intTaskType = documentProcessorService.determineType(taskType);
            List<Document> documentList = documentRepository.findByTypeAndKindAndCreator(
                    intTaskType,
                    2,
                    studentID
            );
            if (documentList.size() == 1) {
                List<DocumentVersion> taskVersions =
                        documentVersionRepository.findByDocument(documentList.get(0).getId());
                for (DocumentVersion taskVersion: taskVersions) {
                    if (intTaskType == 1) {
                        if (taskVersion.getEditor() == studentID) {
                            taskVersionView.add(
                                    new TaskDocumentVersionView(
                                            taskVersion,
                                            nirTaskRepository.findByVersionID(taskVersion.getId())
                                    )
                            );
                        } else if (taskVersion.getEditor() == advisorID &&
                                !taskVersion.getNirTask().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            taskVersionView.add(
                                    new TaskDocumentVersionView(
                                            taskVersion,
                                            nirTaskRepository.findByVersionID(taskVersion.getId())
                                    )
                            );
                        }
                    } else if (intTaskType == 2) {
                        // TODO Задел под практику по получению знаний и умений
                    } else if (intTaskType == 3) {
                        // TODO Задел под преддипломную практику
                    } else if (intTaskType == 4) {
                       // TODO Задел под вкр
                    } else {

                    }
                }
                return taskVersionView;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // Сформировать список версий загруженного задания студента для научного руководителя
    public List<TaskDocumentVersionView> getAdvisorStudentTaskVersions(String token, String taskType, Integer studentID) {
        Integer advisorID = associatedStudentsService.getUserId(token);
        if (studentID != null && advisorID != null) {
            List<TaskDocumentVersionView> taskVersionView = new ArrayList<>();
            Integer intTaskType = documentProcessorService.determineType(taskType);
            List<Document> documentList = documentRepository.findByTypeAndKindAndCreator(
                    intTaskType,
                    2,
                    studentID
            );
            if (documentList.size() == 1) {
                List<DocumentVersion> taskVersions =
                        documentVersionRepository.findByDocument(documentList.get(0).getId());
                for (DocumentVersion taskVersion: taskVersions) {
                    if (intTaskType == 1) {
                        if (taskVersion.getEditor() == advisorID) {
                            taskVersionView.add(
                                    new TaskDocumentVersionView(
                                            taskVersion,
                                            nirTaskRepository.findByVersionID(taskVersion.getId())
                                    )
                            );
                        } else if (taskVersion.getEditor() == studentID &&
                                !taskVersion.getNirTask().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            taskVersionView.add(
                                    new TaskDocumentVersionView(
                                            taskVersion,
                                            nirTaskRepository.findByVersionID(taskVersion.getId())
                                    )
                            );
                        }
                    } else if (intTaskType == 2) {
                        // TODO Задел под практику по получению знаний и умений
                    } else if (intTaskType == 3) {
                        // TODO Задел под преддипломную практику
                    } else if (intTaskType == 4) {
                        // TODO Задел под вкр
                    } else {

                    }
                }
                return taskVersionView;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}