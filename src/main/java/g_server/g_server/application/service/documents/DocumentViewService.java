package g_server.g_server.application.service.documents;

import g_server.g_server.application.entity.documents.*;
import g_server.g_server.application.entity.project.OccupiedStudents;
import g_server.g_server.application.entity.project.Project;
import g_server.g_server.application.entity.users.AssociatedStudents;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.entity.users.UsersRoles;
import g_server.g_server.application.entity.view.*;
import g_server.g_server.application.repository.documents.*;
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
// и разграничение документов пользователям
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

    @Autowired
    private NirReportRepository nirReportRepository;

    @Autowired
    private ViewRightsAreaRepository viewRightsAreaRepository;

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
                    OccupiedStudents occupiedStudent = occupiedStudentsRepository.findByStudentID(student.getId());
                    if (occupiedStudent != null) {
                        ViewRightsArea viewRightsArea = viewRightsAreaRepository.findByDocumentAndArea(
                                document.getId(), occupiedStudent.getProject().getProjectArea().getId());
                        if (viewRightsArea != null) {
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
                // его может видеть создатель проектной области
                if (documentView == 1 || documentView == 6) {
                    // Если желающий увидеть документ НР сам его загрузил
                    if (lookingAdvisor.getId() == documentCreator.getId()) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                // Если документ могут видеть только студенты НР
                else if (documentView == 3) {
                    // Если желающий увидеть документ сам его загрузил
                    if (lookingAdvisor.getId() == documentCreator.getId()) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                // Если документ виден научным руководителям и студентам или только научным руководителям
                else if (documentView == 2 || documentView == 4) {
                    if (lookingAdvisorRoleID == 2 || lookingAdvisorRoleID == 3) {
                        return true;
                    } else {
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

    // Сформировать список документов студентов научного рукводителя
    public List<AdvisorsStudentDocumentView> getAdvisorStudentsDocuments(String token) {
        Integer advisorID = associatedStudentsService.getUserId(token);
        Users advisor;
        try {
            advisor = usersService.findById(advisorID).get();
            List<DocumentView> allDocumentViewList = getAdminDocumentView(advisor);
            List<AdvisorsStudentDocumentView> studentsDocumentsList = new ArrayList<>();
            if (allDocumentViewList != null) {
                for (DocumentView currentView: allDocumentViewList) {
                    if (currentView.getDocumentKind().equals("Задание") ||
                    currentView.getDocumentKind().equals("Отчёт")) {
                        Integer userID = currentView.getSystemCreatorID();
                        UsersRoles usersRole;
                        AssociatedStudents associatedStudents;
                        try {
                            usersRole = usersRolesRepository.findUsersRolesByUserId(userID);
                            if (usersRole.getRoleId() == 1) {
                                associatedStudents = associatedStudentsRepository.
                                        findByScientificAdvisorAndStudent(advisorID, userID);
                                if (associatedStudents != null) {
                                    if (currentView.getDocumentType().equals("Научно-исследовательская работа") && currentView.getDocumentKind().equals("Задание")) {
                                        Document currentNirTaskDocument = documentRepository.findByCreatorAndName(
                                                currentView.getSystemCreatorID(), currentView.getDocumentName());
                                        List<DocumentVersion> currentNirTaskVersions = documentVersionRepository.findByDocument(currentNirTaskDocument.getId());
                                        List<TaskDocumentVersionView> currentTaskVersionsView = new ArrayList<>();
                                        for (DocumentVersion currentNirTaskVersion: currentNirTaskVersions) {
                                            NirTask currentNirTask = nirTaskRepository.findByVersionID(currentNirTaskVersion.getId());
                                            if (currentNirTask.getDocumentStatus().getStatus().equals("Не отправлено") &&
                                                currentNirTaskVersion.getEditor() == advisor.getId()) {
                                                currentTaskVersionsView.add(new TaskDocumentVersionView(currentNirTaskVersion, currentNirTask));
                                            }
                                            if (!currentNirTask.getDocumentStatus().getStatus().equals("Не отправлено")) {
                                                currentTaskVersionsView.add(new TaskDocumentVersionView(currentNirTaskVersion, currentNirTask));
                                            }
                                        }
                                        studentsDocumentsList.add(new AdvisorsStudentDocumentView(currentNirTaskDocument, currentTaskVersionsView, null));
                                    } else if ((currentView.getDocumentType().equals("Научно-исследовательская работа") && currentView.getDocumentKind().equals("Отчёт"))) {
                                        Document currentNirReportDocument = documentRepository.findByCreatorAndName(
                                                currentView.getSystemCreatorID(), currentView.getDocumentName());
                                        List<DocumentVersion> currentNirReportVersions = documentVersionRepository.findByDocument(currentNirReportDocument.getId());
                                        List<ReportVersionDocumentView> currentReportVersionsView = new ArrayList<>();
                                        for (DocumentVersion currentNirReportVersion: currentNirReportVersions) {
                                            NirReport currentNirReport = nirReportRepository.findByVersionID(currentNirReportVersion.getId());
                                            if (currentNirReport.getDocumentStatus().getStatus().equals("Не отправлено")
                                                    && currentNirReportVersion.getEditor() == advisor.getId()) {
                                                currentReportVersionsView.add(new ReportVersionDocumentView(currentNirReportVersion, currentNirReport));
                                            }
                                            else if (!currentNirReport.getDocumentStatus().getStatus().equals("Не отправлено")) {
                                                currentReportVersionsView.add(new ReportVersionDocumentView(currentNirReportVersion, currentNirReport));
                                            }
                                        }
                                        studentsDocumentsList.add(new AdvisorsStudentDocumentView(currentNirReportDocument, null, currentReportVersionsView));
                                    }
                                }
                            }
                        } catch (NullPointerException nullPointerException) {
                            usersRole = null;
                            associatedStudents = null;
                        }
                    }
                }
                return studentsDocumentsList;
            } else {
                return null;
            }
        } catch (NoSuchElementException noSuchElementException) {
            return null;
        }
    }

    // Сформировать список приказов
    public List<DocumentView> getOrders(String token) {
        List<DocumentView> allDocumentViewList = getUserDocumentView(token);
        List<DocumentView> orderList = new ArrayList<>();
        if (allDocumentViewList != null) {
            for (DocumentView currentView: allDocumentViewList) {
                if (currentView.getDocumentKind().equals("Приказ")) {
                    orderList.add(currentView);
                }
            }
        }
        return orderList;
    }

    // Сформировать список шаблонов
    public List<DocumentView> getTemplates(String token) {
        List<DocumentView> allDocumentViewList = getUserDocumentView(token);
        List<DocumentView> templatesList = new ArrayList<>();
        if (allDocumentViewList != null) {
            for (DocumentView currentView: allDocumentViewList) {
                if (currentView.getDocumentKind().equals("Задание")) {
                    Integer headID = currentView.getSystemCreatorID();
                    UsersRoles userRole;
                    try {
                        userRole = usersRolesRepository.findUsersRolesByUserId(headID);
                        if (userRole.getRoleId() == 3) {
                            templatesList.add(currentView);
                        }
                    } catch (NullPointerException nullPointerException) {
                        userRole = null;
                    }
                }
            }
        }
        return templatesList;
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

    // Сформировать список загруженных версий отчёта для студента
    public List<ReportVersionDocumentView> getStudentReportVersions(String token, String taskType) {
        Integer studentID = associatedStudentsService.getUserId(token);
        Integer advisorID = associatedStudentsRepository.findByStudent(studentID).getScientificAdvisor();
        if (studentID != null && advisorID != null) {
            List<ReportVersionDocumentView> reportVersionView = new ArrayList<>();
            Integer intTaskType = documentProcessorService.determineType(taskType);
            List<Document> documentList = documentRepository.findByTypeAndKindAndCreator(
                    intTaskType,
                    3,
                    studentID
            );
            if (documentList.size() == 1) {
                List<DocumentVersion> reportVersions =
                        documentVersionRepository.findByDocument(documentList.get(0).getId());
                for (DocumentVersion taskVersion: reportVersions) {
                    if (intTaskType == 1) {
                        if (taskVersion.getEditor() == studentID) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            taskVersion,
                                            nirReportRepository.findByVersionID(taskVersion.getId())
                                    )
                            );
                        } else if (taskVersion.getEditor() == advisorID &&
                                !taskVersion.getNirReport().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            taskVersion,
                                            nirReportRepository.findByVersionID(taskVersion.getId())
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
                return reportVersionView;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // Сформировать список версий загруженного задания студента для научного руководителя
    public List<ReportVersionDocumentView> getAdvisorStudentReportVersions(String token, String taskType, Integer studentID) {
        Integer advisorID = associatedStudentsService.getUserId(token);
        if (studentID != null && advisorID != null) {
            List<ReportVersionDocumentView> reportVersionView = new ArrayList<>();
            Integer intTaskType = documentProcessorService.determineType(taskType);
            List<Document> documentList = documentRepository.findByTypeAndKindAndCreator(
                    intTaskType,
                    3,
                    studentID
            );
            if (documentList.size() == 1) {
                List<DocumentVersion> reportVersions =
                        documentVersionRepository.findByDocument(documentList.get(0).getId());
                for (DocumentVersion reportVersion: reportVersions) {
                    if (intTaskType == 1) {
                        if (reportVersion.getEditor() == advisorID) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            reportVersion,
                                            nirReportRepository.findByVersionID(reportVersion.getId())
                                    )
                            );
                        } else if (reportVersion.getEditor() == studentID &&
                                !reportVersion.getNirReport().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            reportVersion,
                                            nirReportRepository.findByVersionID(reportVersion.getId())
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
                return reportVersionView;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // Сформировать список представлений загруженных примеров работ научного руководителя
    public List<DocumentView> getAdvisorsLoadedTaskAndReportsTemplates(String token) {
        List<DocumentView> documentViews = getUserDocumentView(token);
        Integer advisorID = associatedStudentsService.getUserId(token);;
        List<DocumentView> advisorsTemplates = new ArrayList<>();
        for (DocumentView documentView: documentViews) {
            if (documentView.getSystemCreatorID() == advisorID) {
                if (documentView.getDocumentKind().equals("Отчёт") || documentView.getDocumentKind().equals("Задание")) {
                    advisorsTemplates.add(documentView);
                }
            }
        }
        return advisorsTemplates;
    }

    // Сформировать список представлений загруженных примеров работ научного руководителя его студенту
    public List<DocumentView> getAdvisorsLoadedTaskAndReportsTemplatesForStudent(String token) {
        Integer studentID = associatedStudentsService.getUserId(token);
        Integer advisorID;
        try {
            advisorID = associatedStudentsRepository.findByStudent(studentID).getScientificAdvisor();
        } catch (NullPointerException nullPointerException) {
            return null;
        }
        List<DocumentView> documentViews = getUserDocumentView(token);
        List<DocumentView> advisorsTemplatesForStudent = new ArrayList<>();
        for (DocumentView documentView: documentViews) {
            if (documentView.getSystemCreatorID() == advisorID) {
                if (documentView.getDocumentKind().equals("Отчёт") || documentView.getDocumentKind().equals("Задание")) {
                    advisorsTemplatesForStudent.add(documentView);
                }
            }
        }
        return advisorsTemplatesForStudent;
    }
}