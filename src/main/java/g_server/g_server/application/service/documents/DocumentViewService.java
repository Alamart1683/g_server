package g_server.g_server.application.service.documents;

import g_server.g_server.application.entity.documents.*;
import g_server.g_server.application.entity.documents.reports.NirReport;
import g_server.g_server.application.entity.documents.reports.PdReport;
import g_server.g_server.application.entity.documents.reports.PpppuiopdReport;
import g_server.g_server.application.entity.documents.reports.VkrReport;
import g_server.g_server.application.entity.documents.tasks.NirTask;
import g_server.g_server.application.entity.documents.tasks.PdTask;
import g_server.g_server.application.entity.documents.tasks.PpppuiopdTask;
import g_server.g_server.application.entity.documents.tasks.VkrTask;
import g_server.g_server.application.entity.documents.vkr_other.VkrAdvisorConclusion;
import g_server.g_server.application.entity.documents.vkr_other.VkrAllowance;
import g_server.g_server.application.entity.documents.vkr_other.VkrAntiplagiat;
import g_server.g_server.application.entity.documents.vkr_other.VkrPresentation;
import g_server.g_server.application.entity.project.OccupiedStudents;
import g_server.g_server.application.entity.project.Project;
import g_server.g_server.application.entity.project.ProjectArea;
import g_server.g_server.application.entity.system_data.Speciality;
import g_server.g_server.application.entity.users.AssociatedStudents;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.entity.users.UsersRoles;
import g_server.g_server.application.entity.view.*;
import g_server.g_server.application.repository.documents.*;
import g_server.g_server.application.repository.project.OccupiedStudentsRepository;
import g_server.g_server.application.repository.project.ProjectAreaRepository;
import g_server.g_server.application.repository.project.ProjectRepository;
import g_server.g_server.application.repository.system_data.SpecialityRepository;
import g_server.g_server.application.repository.users.AssociatedStudentsRepository;
import g_server.g_server.application.repository.users.UsersRolesRepository;
import g_server.g_server.application.service.documents.crud.DocumentService;
import g_server.g_server.application.service.documents.text_processor.Splitter;
import g_server.g_server.application.service.users.AssociatedStudentsService;
import g_server.g_server.application.service.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
// Сервис ответственный за представление
// и разграничение документов пользователям
// TODO Обработать новую, седьмую область видимости
// TODO Доработать видимость проекта если это необходимо
public class DocumentViewService {
    @Value("${external.api.url}")
    private String externalApiUrl;

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

    @Autowired
    private ProjectAreaRepository projectAreaRepository;

    @Autowired
    private OrderPropertiesRepository orderPropertiesRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    @Autowired
    private ViewRightsProjectRepository viewRightsProjectRepository;

    @Autowired
    private PpppuiopdTaskRepository ppppuiopdTaskRepository;

    @Autowired
    private PpppuiopdReportRepository ppppuiopdReportRepository;

    @Autowired
    private PdTaskRepository pdTaskRepository;

    @Autowired
    private PdReportRepository pdReportRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private VkrTaskRepository vkrTaskRepository;

    @Autowired
    private VkrReportRepository vkrReportRepository;

    @Autowired
    private VkrAntiplagiatRepository vkrAntiplagiatRepository;

    @Autowired
    private VkrPresentationRepository vkrPresentationRepository;

    @Autowired
    private VkrConclusionRepository vkrConclusionRepository;

    @Autowired
    private VkrAllowanceRepository vkrAllowanceRepository;


    // Проверить, может ли студент видеть данный документ
    private boolean checkStudentDocumentView(Users student, Users documentCreator, Document document) {
        // TODO Внимание, метод проверки работает только под текущий вариант зон видимости и ролей,
        // TODO иначе его придется переделывать
        // Определим уровень видимости документа
        Integer documentView = document.getView_rights();
        // Проверим соответствие ролей
        Integer documentCreatorRoleID = usersRolesRepository.findUsersRolesByUserId(documentCreator.getId()).getRoleId();
        Integer studentRoleID = usersRolesRepository.findUsersRolesByUserId(student.getId()).getRoleId();
        if (studentRoleID == 1 && (documentCreatorRoleID > 0 && documentCreatorRoleID < 6)) {
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
                else if (documentView == 8) {
                    OccupiedStudents occupiedStudent = occupiedStudentsRepository.findByStudentID(student.getId());
                    if (occupiedStudent != null) {
                        ViewRightsProject viewRightsProject = viewRightsProjectRepository.findByDocument(document.getId());
                        if (viewRightsProject != null) {
                            if (viewRightsProject.getProject() == occupiedStudent.getProjectID()) {
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
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
                (documentCreatorRoleID > 0 || documentCreatorRoleID < 6)) {
            // Если зона видимости документа только для создателя и других НР или для всех
            if (documentView > 0 || documentView < 9) {
                // Если документ может видеть только его создатель или документ привязан к проекту и
                // его может видеть создатель проектной области
                if (documentView == 1 || documentView == 6 || documentView == 8) {
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

    // Сформировать список документов студентов научного руководителя
    public List<AdvisorsStudentDocumentView> getAdvisorStudentsDocuments(String token) {
        Integer advisorID = associatedStudentsService.getUserId(token);
        Users advisor;
        try {
            advisor = usersService.findById(advisorID).get();
            List<DocumentView> allDocumentViewList = getAdvisorDocumentView(advisor);
            List<AdvisorsStudentDocumentView> studentsDocumentsList = new ArrayList<>();
            if (allDocumentViewList != null) {
                for (DocumentView currentView: allDocumentViewList) {
                    if (currentView.getDocumentKind().equals("Задание") ||
                    currentView.getDocumentKind().equals("Отчёт") ||
                    currentView.getDocumentKind().equals("Отзыв") ||
                    currentView.getDocumentKind().equals("Допуск") ||
                    currentView.getDocumentKind().equals("Антиплагиат") ||
                    currentView.getDocumentKind().equals("Презентация")) {
                        Integer userID = currentView.getSystemCreatorID();
                        UsersRoles usersRole;
                        AssociatedStudents associatedStudents;
                        try {
                            usersRole = usersRolesRepository.findUsersRolesByUserId(userID);
                            if (usersRole.getRoleId() == 1) {
                                associatedStudents = associatedStudentsRepository.
                                        findByScientificAdvisorAndStudent(advisorID, userID);
                                if (associatedStudents != null) {
                                    // НИР
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
                                        studentsDocumentsList.add(new AdvisorsStudentDocumentView(currentNirTaskDocument, currentTaskVersionsView,
                                                null, null, null));
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
                                        studentsDocumentsList.add(new AdvisorsStudentDocumentView(currentNirReportDocument, null,
                                                currentReportVersionsView, null, null));
                                    // ППППУиОПД
                                    } else if (currentView.getDocumentType().equals("Практика по получению знаний и умений") && currentView.getDocumentKind().equals("Задание")) {
                                        Document currentPpppuiopdTaskDocument = documentRepository.findByCreatorAndName(
                                                currentView.getSystemCreatorID(), currentView.getDocumentName());
                                        List<DocumentVersion> currentPpppuiopdTaskVersions = documentVersionRepository.findByDocument(currentPpppuiopdTaskDocument.getId());
                                        List<TaskDocumentVersionView> currentTaskVersionsView = new ArrayList<>();
                                        for (DocumentVersion currentPpppuiopdTaskVersion: currentPpppuiopdTaskVersions) {
                                            PpppuiopdTask currentPpppuiopdTask = ppppuiopdTaskRepository.findByVersionID(currentPpppuiopdTaskVersion.getId());
                                            if (currentPpppuiopdTask.getDocumentStatus().getStatus().equals("Не отправлено") &&
                                                    currentPpppuiopdTaskVersion.getEditor() == advisor.getId()) {
                                                currentTaskVersionsView.add(new TaskDocumentVersionView(currentPpppuiopdTaskVersion, currentPpppuiopdTask));
                                            }
                                            if (!currentPpppuiopdTask.getDocumentStatus().getStatus().equals("Не отправлено")) {
                                                currentTaskVersionsView.add(new TaskDocumentVersionView(currentPpppuiopdTaskVersion, currentPpppuiopdTask));
                                            }
                                        }
                                        studentsDocumentsList.add(new AdvisorsStudentDocumentView(currentPpppuiopdTaskDocument,
                                                currentTaskVersionsView, null, null, null));
                                    } else if ((currentView.getDocumentType().equals("Практика по получению знаний и умений") && currentView.getDocumentKind().equals("Отчёт"))) {
                                        Document currentPpppuiopdReportDocument = documentRepository.findByCreatorAndName(
                                                currentView.getSystemCreatorID(), currentView.getDocumentName());
                                        List<DocumentVersion> currentPpppuiopdReportVersions = documentVersionRepository.findByDocument(currentPpppuiopdReportDocument.getId());
                                        List<ReportVersionDocumentView> currentReportVersionsView = new ArrayList<>();
                                        for (DocumentVersion currentPpppuiopdReportVersion: currentPpppuiopdReportVersions) {
                                            PpppuiopdReport currentPpppuiopdReport = ppppuiopdReportRepository.findByVersionID(currentPpppuiopdReportVersion.getId());
                                            if (currentPpppuiopdReport.getDocumentStatus().getStatus().equals("Не отправлено")
                                                    && currentPpppuiopdReportVersion.getEditor() == advisor.getId()) {
                                                currentReportVersionsView.add(new ReportVersionDocumentView(currentPpppuiopdReportVersion, currentPpppuiopdReport));
                                            }
                                            else if (!currentPpppuiopdReport.getDocumentStatus().getStatus().equals("Не отправлено")) {
                                                currentReportVersionsView.add(new ReportVersionDocumentView(currentPpppuiopdReportVersion, currentPpppuiopdReport));
                                            }
                                        }
                                        studentsDocumentsList.add(new AdvisorsStudentDocumentView(currentPpppuiopdReportDocument,
                                                null, currentReportVersionsView, null, null));
                                    }
                                    // ПП
                                    else if (currentView.getDocumentType().equals("Преддипломная практика") && currentView.getDocumentKind().equals("Задание")) {
                                        Document currentPdTaskDocument = documentRepository.findByCreatorAndName(
                                                currentView.getSystemCreatorID(), currentView.getDocumentName());
                                        List<DocumentVersion> currentPdTaskVersions = documentVersionRepository.findByDocument(currentPdTaskDocument.getId());
                                        List<TaskDocumentVersionView> currentTaskVersionsView = new ArrayList<>();
                                        for (DocumentVersion currentPdTaskVersion: currentPdTaskVersions) {
                                            PdTask currentPdTask = pdTaskRepository.findByVersionID(currentPdTaskVersion.getId());
                                            if (currentPdTask.getDocumentStatus().getStatus().equals("Не отправлено") &&
                                                    currentPdTaskVersion.getEditor() == advisor.getId()) {
                                                currentTaskVersionsView.add(new TaskDocumentVersionView(currentPdTaskVersion, currentPdTask));
                                            }
                                            if (!currentPdTask.getDocumentStatus().getStatus().equals("Не отправлено")) {
                                                currentTaskVersionsView.add(new TaskDocumentVersionView(currentPdTaskVersion, currentPdTask));
                                            }
                                        }
                                        studentsDocumentsList.add(new AdvisorsStudentDocumentView(currentPdTaskDocument,
                                                currentTaskVersionsView, null, null, null));
                                    } else if ((currentView.getDocumentType().equals("Преддипломная практика") && currentView.getDocumentKind().equals("Отчёт"))) {
                                        Document currentPdReportDocument = documentRepository.findByCreatorAndName(
                                                currentView.getSystemCreatorID(), currentView.getDocumentName());
                                        List<DocumentVersion> currentPdReportVersions = documentVersionRepository.findByDocument(currentPdReportDocument.getId());
                                        List<ReportVersionDocumentView> currentReportVersionsView = new ArrayList<>();
                                        for (DocumentVersion currentPdReportVersion: currentPdReportVersions) {
                                            PdReport currentPdReport = pdReportRepository.findByVersionID(currentPdReportVersion.getId());
                                            if (currentPdReport.getDocumentStatus().getStatus().equals("Не отправлено")
                                                    && currentPdReportVersion.getEditor() == advisor.getId()) {
                                                currentReportVersionsView.add(new ReportVersionDocumentView(currentPdReportVersion, currentPdReport));
                                            }
                                            else if (!currentPdReport.getDocumentStatus().getStatus().equals("Не отправлено")) {
                                                currentReportVersionsView.add(new ReportVersionDocumentView(currentPdReportVersion, currentPdReport));
                                            }
                                        }
                                        studentsDocumentsList.add(new AdvisorsStudentDocumentView(currentPdReportDocument,
                                                null, currentReportVersionsView, null, null));
                                    }
                                    // TODO ВКР
                                    else if ((currentView.getDocumentType().equals("ВКР")) && // Задание на ВКР
                                            currentView.getDocumentKind().equals("Задание")) {
                                        Document currentVkrTaskDocument = documentRepository.findByCreatorAndName(
                                                currentView.getSystemCreatorID(), currentView.getDocumentName());
                                        List<DocumentVersion> currentVkrTaskVersions = documentVersionRepository.findByDocument(currentVkrTaskDocument.getId());
                                        List<TaskDocumentVersionView> currentTaskVersionsView = new ArrayList<>();
                                        for (DocumentVersion currentVkrTaskVersion: currentVkrTaskVersions) {
                                            VkrTask currentVkrTask = vkrTaskRepository.findByVersionID(currentVkrTaskVersion.getId());
                                            if (currentVkrTask.getDocumentStatus().getStatus().equals("Не отправлено") &&
                                                    currentVkrTaskVersion.getEditor() == advisor.getId()) {
                                                currentTaskVersionsView.add(new TaskDocumentVersionView(currentVkrTaskVersion, currentVkrTask));
                                            }
                                            if (!currentVkrTask.getDocumentStatus().getStatus().equals("Не отправлено")) {
                                                currentTaskVersionsView.add(new TaskDocumentVersionView(currentVkrTaskVersion, currentVkrTask));
                                            }
                                        }
                                        studentsDocumentsList.add(new AdvisorsStudentDocumentView(currentVkrTaskDocument,
                                                currentTaskVersionsView, null, null, null));
                                    }
                                    else if ((currentView.getDocumentType().equals("ВКР")) && // РПЗ по ВКР
                                            currentView.getDocumentKind().equals("Отчёт")) {
                                        Document currentVkrReportDocument = documentRepository.findByCreatorAndName(
                                                currentView.getSystemCreatorID(), currentView.getDocumentName());
                                        List<DocumentVersion> currentVkrReportVersions = documentVersionRepository.findByDocument(currentVkrReportDocument.getId());
                                        List<ReportVersionDocumentView> currentReportVersionsView = new ArrayList<>();
                                        for (DocumentVersion currentVkrReportVersion: currentVkrReportVersions) {
                                            VkrReport currentVkrReport = vkrReportRepository.findByVersionID(currentVkrReportVersion.getId());
                                            if (currentVkrReport.getDocumentStatus().getStatus().equals("Не отправлено")
                                                    && currentVkrReportVersion.getEditor() == advisor.getId()) {
                                                currentReportVersionsView.add(new ReportVersionDocumentView(currentVkrReportVersion, currentVkrReport));
                                            }
                                            else if (!currentVkrReport.getDocumentStatus().getStatus().equals("Не отправлено")) {
                                                currentReportVersionsView.add(new ReportVersionDocumentView(currentVkrReportVersion, currentVkrReport));
                                            }
                                        }
                                        studentsDocumentsList.add(new AdvisorsStudentDocumentView(currentVkrReportDocument,
                                                null, currentReportVersionsView, null, null));
                                    }
                                    else if ((currentView.getDocumentType().equals("ВКР")) && // Допуск по ВКР
                                            currentView.getDocumentKind().equals("Допуск")) {
                                        Document currentAllowanceDocument = documentRepository.findByCreatorAndName(
                                                currentView.getSystemCreatorID(), currentView.getDocumentName());
                                        List<DocumentVersion> allowanceVersions = documentVersionRepository.findByDocument(currentAllowanceDocument.getId());
                                        List<VkrStuffVersionView> allowanceVersionsViews = new ArrayList<>();
                                        for (DocumentVersion documentVersion: allowanceVersions) {
                                            VkrAllowance currentAllowance = vkrAllowanceRepository.findByVersionID(documentVersion.getId());
                                            if (currentAllowance.getDocumentStatus().getStatus().equals("Не отправлено")
                                                && documentVersion.getEditor() == advisor.getId()) {
                                                allowanceVersionsViews.add(new VkrStuffVersionView(documentVersion,
                                                        currentAllowance.getDocumentStatus().getStatus()));
                                            } else if (!currentAllowance.getDocumentStatus().getStatus().equals("Не отправлено")) {
                                                allowanceVersionsViews.add(new VkrStuffVersionView(documentVersion,
                                                        currentAllowance.getDocumentStatus().getStatus()));
                                            }
                                        }
                                        studentsDocumentsList.add(new AdvisorsStudentDocumentView(currentAllowanceDocument,
                                                null, null, null, allowanceVersionsViews));
                                    }
                                    else if ((currentView.getDocumentType().equals("ВКР")) && // Отзыв на ВКР
                                            currentView.getDocumentKind().equals("Отзыв")) {
                                        Document currentConclusionDocument = documentRepository.findByCreatorAndName(
                                                currentView.getSystemCreatorID(), currentView.getDocumentName());
                                        List<DocumentVersion> conclusionVersions = documentVersionRepository.findByDocument(currentConclusionDocument.getId());
                                        List<VkrStuffVersionView> documentVersionViews = new ArrayList<>();
                                        for (DocumentVersion documentVersion: conclusionVersions) {
                                            VkrAdvisorConclusion currentConclusion = vkrConclusionRepository.findByVersionID(documentVersion.getId());
                                            if (currentConclusion.getDocumentStatus().getStatus().equals("Не отправлено")
                                                    && documentVersion.getEditor() == advisor.getId()) {
                                                documentVersionViews.add(new VkrStuffVersionView(documentVersion,
                                                        currentConclusion.getDocumentStatus().getStatus()));
                                            } else if (!currentConclusion.getDocumentStatus().getStatus().equals("Не отправлено")) {
                                                documentVersionViews.add(new VkrStuffVersionView(documentVersion,
                                                        currentConclusion.getDocumentStatus().getStatus()));
                                            }
                                        }
                                        studentsDocumentsList.add(new AdvisorsStudentDocumentView(currentConclusionDocument,
                                                null, null, null, documentVersionViews));
                                    }
                                    else if ((currentView.getDocumentType().equals("ВКР")) && // Антиплагиат на ВКР
                                            currentView.getDocumentKind().equals("Антиплагиат")) {
                                        Document currentAntiplagiatDocument = documentRepository.findByCreatorAndName(
                                                currentView.getSystemCreatorID(), currentView.getDocumentName());
                                        List<DocumentVersion> antiplagiatVersions = documentVersionRepository.findByDocument(currentAntiplagiatDocument.getId());
                                        List<VkrStuffVersionView> antiplagiatVersionViews = new ArrayList<>();
                                        for (DocumentVersion documentVersion: antiplagiatVersions) {
                                            VkrAntiplagiat currentAntiplagiat = vkrAntiplagiatRepository.findByVersionID(documentVersion.getId());
                                            if (currentAntiplagiat.getDocumentStatus().getStatus().equals("Не отправлено")
                                                    && documentVersion.getEditor() == advisor.getId()) {
                                                antiplagiatVersionViews.add(new VkrStuffVersionView(documentVersion,
                                                        currentAntiplagiat.getDocumentStatus().getStatus()));
                                            } else if (!currentAntiplagiat.getDocumentStatus().getStatus().equals("Не отправлено")) {
                                                antiplagiatVersionViews.add(new VkrStuffVersionView(documentVersion,
                                                        currentAntiplagiat.getDocumentStatus().getStatus()));
                                            }
                                        }
                                        studentsDocumentsList.add(new AdvisorsStudentDocumentView(currentAntiplagiatDocument,
                                                null, null, null, antiplagiatVersionViews));
                                    }
                                    else if ((currentView.getDocumentType().equals("ВКР")) && // Презентация на ВКР
                                            currentView.getDocumentKind().equals("Презентация")) {
                                        Document currentPresentationDocument = documentRepository.findByCreatorAndName(
                                                currentView.getSystemCreatorID(), currentView.getDocumentName());
                                        List<DocumentVersion> presentationVersions = documentVersionRepository.findByDocument(currentPresentationDocument.getId());
                                        List<VkrStuffVersionView> presentationVersionViews = new ArrayList<>();
                                        for (DocumentVersion documentVersion: presentationVersions) {
                                            VkrPresentation currentPresentation = vkrPresentationRepository.findByVersionID(documentVersion.getId());
                                            if (currentPresentation.getDocumentStatus().getStatus().equals("Не отправлено")
                                                    && documentVersion.getEditor() == advisor.getId()) {
                                                presentationVersionViews.add(new VkrStuffVersionView(documentVersion,
                                                        currentPresentation.getDocumentStatus().getStatus()));
                                            } else if (!currentPresentation.getDocumentStatus().getStatus().equals("Не отправлено")) {
                                                presentationVersionViews.add(new VkrStuffVersionView(documentVersion,
                                                        currentPresentation.getDocumentStatus().getStatus()));
                                            }
                                        }
                                        studentsDocumentsList.add(new AdvisorsStudentDocumentView(currentPresentationDocument,
                                                null, null, null, presentationVersionViews));
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
    public List<DocumentViewOrder> getOrders(String token) {
        List<DocumentView> allDocumentViewList = getUserDocumentView(token);
        List<DocumentViewOrder> orderList = new ArrayList<>();
        Integer userID = associatedStudentsService.getUserId(token);
        Integer userRoleID = usersRolesRepository.findUsersRolesByUserId(userID).getRoleId();
        if (allDocumentViewList != null) {
            for (DocumentView currentView: allDocumentViewList) {
                if (currentView.getDocumentKind().equals("Приказ")) {
                    Document order = documentRepository.findByCreatorAndName(currentView.getSystemCreatorID(),
                            currentView.getDocumentName());
                    OrderProperties orderProperties;
                    if (orderPropertiesRepository.findById(order.getId()).isPresent()) {
                        orderProperties = orderPropertiesRepository.findById(order.getId()).get();
                        Speciality speciality;
                        // Завкафедрой видит все приказы, в том числе и неодобренные, остальные только одобренные
                        if (specialityRepository.findById(orderProperties.getSpeciality()).isPresent()
                                && (orderProperties.isApproved() || userRoleID >= 3)) {
                            speciality = specialityRepository.findById(orderProperties.getSpeciality()).get();
                            orderList.add(
                                new DocumentViewOrder(
                                    order,
                                    currentView.getDocumentVersions(),
                                    currentView.getRussianDate(orderProperties.getOrderDate()),
                                    currentView.getRussianDate(orderProperties.getStartDate()),
                                    currentView.getRussianDate(orderProperties.getEndDate()),
                                    orderProperties.getNumber(),
                                    speciality.getSpeciality(),
                                    speciality.getCode(),
                                    orderProperties.isApproved()
                                )
                            );
                        }
                    }
                }
            }
        }
        return orderList;
    }

    // Сформировать список шаблонов
    public List<DocumentViewTemplate> getTemplates(String token) {
        List<DocumentView> allDocumentViewList = getUserDocumentView(token);
        List<DocumentViewTemplate> templatesList = new ArrayList<>();
        Integer userID = associatedStudentsService.getUserId(token);
        Integer userRoleID = usersRolesRepository.findUsersRolesByUserId(userID).getRoleId();
        if (allDocumentViewList != null) {
            for (DocumentView currentView: allDocumentViewList) {
                if (currentView.getDocumentKind().equals("Шаблон")) {
                    Integer headID = currentView.getSystemCreatorID();
                    UsersRoles userRole;
                    Document template = documentRepository.findByCreatorAndName(
                            currentView.getSystemCreatorID(), currentView.getDocumentName());
                    try {
                        userRole = usersRolesRepository.findUsersRolesByUserId(userID);
                        if (userRole.getRoleId() >= 3 || (userRoleID < 3 && template.getTemplateProperties().isApproved())) {
                            templatesList.add(
                                    new DocumentViewTemplate(
                                            template,
                                            currentView.getDocumentVersions(),
                                            template.getTemplateProperties().isApproved()
                                    )
                            );
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
                        if (taskVersion.getEditor() == studentID) {
                            taskVersionView.add(
                                    new TaskDocumentVersionView(
                                            taskVersion,
                                            ppppuiopdTaskRepository.findByVersionID(taskVersion.getId())
                                    )
                            );
                        } else if (taskVersion.getEditor() == advisorID &&
                                !taskVersion.getPpppuiopdTask().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            taskVersionView.add(
                                    new TaskDocumentVersionView(
                                            taskVersion,
                                            ppppuiopdTaskRepository.findByVersionID(taskVersion.getId())
                                    )
                            );
                        }
                    } else if (intTaskType == 3) {
                        if (taskVersion.getEditor() == studentID) {
                            taskVersionView.add(
                                    new TaskDocumentVersionView(
                                            taskVersion,
                                            pdTaskRepository.findByVersionID(taskVersion.getId())
                                    )
                            );
                        } else if (taskVersion.getEditor() == advisorID &&
                                !taskVersion.getPdTask().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            taskVersionView.add(
                                    new TaskDocumentVersionView(
                                            taskVersion,
                                            pdTaskRepository.findByVersionID(taskVersion.getId())
                                    )
                            );
                        }
                    } else if (intTaskType == 4) {
                        if (taskVersion.getEditor() == studentID) {
                            taskVersionView.add(
                                    new TaskDocumentVersionView(
                                            taskVersion,
                                            vkrTaskRepository.findByVersionID(taskVersion.getId())
                                    )
                            );
                        } else if (taskVersion.getEditor() == advisorID &&
                                !taskVersion.getVkrTask().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            taskVersionView.add(
                                    new TaskDocumentVersionView(
                                            taskVersion,
                                            vkrTaskRepository.findByVersionID(taskVersion.getId())
                                    )
                            );
                        }
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
                        if (taskVersion.getEditor() == advisorID) {
                            taskVersionView.add(
                                    new TaskDocumentVersionView(
                                            taskVersion,
                                            ppppuiopdTaskRepository.findByVersionID(taskVersion.getId())
                                    )
                            );
                        } else if (taskVersion.getEditor() == studentID &&
                                !taskVersion.getPpppuiopdTask().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            taskVersionView.add(
                                    new TaskDocumentVersionView(
                                            taskVersion,
                                            ppppuiopdTaskRepository.findByVersionID(taskVersion.getId())
                                    )
                            );
                        }
                    } else if (intTaskType == 3) {
                        if (taskVersion.getEditor() == advisorID) {
                            taskVersionView.add(
                                    new TaskDocumentVersionView(
                                            taskVersion,
                                            pdTaskRepository.findByVersionID(taskVersion.getId())
                                    )
                            );
                        } else if (taskVersion.getEditor() == studentID &&
                                !taskVersion.getPdTask().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            taskVersionView.add(
                                    new TaskDocumentVersionView(
                                            taskVersion,
                                            pdTaskRepository.findByVersionID(taskVersion.getId())
                                    )
                            );
                        }
                    } else if (intTaskType == 4) {
                        if (taskVersion.getEditor() == advisorID) {
                            taskVersionView.add(
                                    new TaskDocumentVersionView(
                                            taskVersion,
                                            vkrTaskRepository.findByVersionID(taskVersion.getId())
                                    )
                            );
                        } else if (taskVersion.getEditor() == studentID &&
                                !taskVersion.getVkrTask().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            taskVersionView.add(
                                    new TaskDocumentVersionView(
                                            taskVersion,
                                            vkrTaskRepository.findByVersionID(taskVersion.getId())
                                    )
                            );
                        }
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
                for (DocumentVersion reportVersion: reportVersions) {
                    if (intTaskType == 1) {
                        if (reportVersion.getEditor() == studentID) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            reportVersion,
                                            nirReportRepository.findByVersionID(reportVersion.getId())
                                    )
                            );
                        } else if (reportVersion.getEditor() == advisorID &&
                                !reportVersion.getNirReport().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            reportVersion,
                                            nirReportRepository.findByVersionID(reportVersion.getId())
                                    )
                            );
                        }
                    } else if (intTaskType == 2) {
                        if (reportVersion.getEditor() == studentID) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            reportVersion,
                                            ppppuiopdReportRepository.findByVersionID(reportVersion.getId())
                                    )
                            );
                        } else if (reportVersion.getEditor() == advisorID &&
                                !reportVersion.getPpppuiopdReport().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            reportVersion,
                                            ppppuiopdReportRepository.findByVersionID(reportVersion.getId())
                                    )
                            );
                        }
                    } else if (intTaskType == 3) {
                        if (reportVersion.getEditor() == studentID) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            reportVersion,
                                            pdReportRepository.findByVersionID(reportVersion.getId())
                                    )
                            );
                        } else if (reportVersion.getEditor() == advisorID &&
                                !reportVersion.getPdReport().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            reportVersion,
                                            pdReportRepository.findByVersionID(reportVersion.getId())
                                    )
                            );
                        }
                    } else if (intTaskType == 4) {
                        if (reportVersion.getEditor() == studentID) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            reportVersion,
                                            vkrReportRepository.findByVersionID(reportVersion.getId())
                                    )
                            );
                        } else if (reportVersion.getEditor() == advisorID &&
                                !reportVersion.getVkrReport().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            reportVersion,
                                            vkrReportRepository.findByVersionID(reportVersion.getId())
                                    )
                            );
                        }
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

    // Сформировать список версий загруженного отчета студента для научного руководителя
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
                    // НИР
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
                    // ППППУиОПД
                    } else if (intTaskType == 2) {
                        if (reportVersion.getEditor() == advisorID) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            reportVersion,
                                            ppppuiopdReportRepository.findByVersionID(reportVersion.getId())
                                    )
                            );
                        } else if (reportVersion.getEditor() == studentID &&
                                !reportVersion.getPpppuiopdReport().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            reportVersion,
                                            ppppuiopdReportRepository.findByVersionID(reportVersion.getId())
                                    )
                            );
                        }
                    // ПП
                    } else if (intTaskType == 3) {
                        if (reportVersion.getEditor() == advisorID) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            reportVersion,
                                            pdReportRepository.findByVersionID(reportVersion.getId())
                                    )
                            );
                        } else if (reportVersion.getEditor() == studentID &&
                                !reportVersion.getPdReport().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            reportVersion,
                                            pdReportRepository.findByVersionID(reportVersion.getId())
                                    )
                            );
                        }
                    } else if (intTaskType == 4) {
                        if (reportVersion.getEditor() == advisorID) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            reportVersion,
                                            vkrReportRepository.findByVersionID(reportVersion.getId())
                                    )
                            );
                        } else if (reportVersion.getEditor() == studentID &&
                                !reportVersion.getVkrReport().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            reportVersionView.add(
                                    new ReportVersionDocumentView(
                                            reportVersion,
                                            vkrReportRepository.findByVersionID(reportVersion.getId())
                                    )
                            );
                        }
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

    // Сформировать список версий загруженного вкрного стаффа для студента
    public List<VkrStuffVersionView> getStudentVkrStuffVersions(String token, String stuffKind) {
        Integer studentID = associatedStudentsService.getUserId(token);
        Integer advisorID = associatedStudentsRepository.findByStudent(studentID).getScientificAdvisor();
        if (studentID != null && advisorID != null) {
            List<VkrStuffVersionView> vkrStuffVersionView = new ArrayList<>();
            Integer kind = documentProcessorService.determineKind(stuffKind);
            List<Document> documentList = documentRepository.findByTypeAndKindAndCreator(
                    4,
                    kind,
                    studentID
            );
            if (documentList.size() == 1) {
                List<DocumentVersion> stuffVersions =
                        documentVersionRepository.findByDocument(documentList.get(0).getId());
                for (DocumentVersion stuffVersion: stuffVersions) {
                    if (kind == 6) {
                        if (stuffVersion.getEditor() == studentID) {
                            vkrStuffVersionView.add(
                                    new VkrStuffVersionView(
                                            stuffVersion,
                                            vkrAllowanceRepository.findByVersionID(stuffVersion.getId()).getDocumentStatus().getStatus()
                                    )
                            );
                        } else if (stuffVersion.getEditor() == advisorID &&
                                !stuffVersion.getVkrAllowance().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            vkrStuffVersionView.add(
                                    new VkrStuffVersionView(
                                            stuffVersion,
                                            vkrAllowanceRepository.findByVersionID(stuffVersion.getId()).getDocumentStatus().getStatus()
                                    )
                            );
                        }
                    } else if (kind == 7) {
                        if (stuffVersion.getEditor() == studentID) {
                            vkrStuffVersionView.add(
                                    new VkrStuffVersionView(
                                            stuffVersion,
                                            vkrConclusionRepository.findByVersionID(stuffVersion.getId()).getDocumentStatus().getStatus()
                                    )
                            );
                        } else if (stuffVersion.getEditor() == advisorID &&
                                !stuffVersion.getAdvisorConclusion().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            vkrStuffVersionView.add(
                                    new VkrStuffVersionView(
                                            stuffVersion,
                                            vkrConclusionRepository.findByVersionID(stuffVersion.getId()).getDocumentStatus().getStatus()
                                    )
                            );
                        }
                    } else if (kind == 8) {
                        if (stuffVersion.getEditor() == studentID) {
                            vkrStuffVersionView.add(
                                    new VkrStuffVersionView(
                                            stuffVersion,
                                            vkrAntiplagiatRepository.findByVersionID(stuffVersion.getId()).getDocumentStatus().getStatus()
                                    )
                            );
                        } else if (stuffVersion.getEditor() == advisorID &&
                                !stuffVersion.getVkrAntiplagiat().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            vkrStuffVersionView.add(
                                    new VkrStuffVersionView(
                                            stuffVersion,
                                            vkrAntiplagiatRepository.findByVersionID(stuffVersion.getId()).getDocumentStatus().getStatus()
                                    )
                            );
                        }
                    } else if (kind == 9) {
                        if (stuffVersion.getEditor() == studentID) {
                            vkrStuffVersionView.add(
                                    new VkrStuffVersionView(
                                            stuffVersion,
                                            vkrPresentationRepository.findByVersionID(stuffVersion.getId()).getDocumentStatus().getStatus()
                                    )
                            );
                        } else if (stuffVersion.getEditor() == advisorID &&
                                !stuffVersion.getVkrPresentation().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            vkrStuffVersionView.add(
                                    new VkrStuffVersionView(
                                            stuffVersion,
                                            vkrPresentationRepository.findByVersionID(stuffVersion.getId()).getDocumentStatus().getStatus()
                                    )
                            );
                        }
                    }
                }
                return vkrStuffVersionView;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // Сформировать список версий загруженного вкрного стаффа студента для научного руководителя
    public List<VkrStuffVersionView> getAdvisorStudentVkrStuffVersions(String token, String taskKind, Integer studentID) {
        Integer advisorID = associatedStudentsService.getUserId(token);
        if (studentID != null && advisorID != null) {
            List<VkrStuffVersionView> vkrStuffVersionView = new ArrayList<>();
            Integer stuffKind = documentProcessorService.determineKind(taskKind);
            List<Document> documentList = documentRepository.findByTypeAndKindAndCreator(
                    4,
                    stuffKind,
                    studentID
            );
            if (documentList.size() == 1) {
                List<DocumentVersion> vkrStuffVersions =
                        documentVersionRepository.findByDocument(documentList.get(0).getId());
                for (DocumentVersion vkrStuffVersion: vkrStuffVersions) {
                    // НИР
                    if (stuffKind == 6) {
                        if (vkrStuffVersion.getEditor() == advisorID) {
                            vkrStuffVersionView.add(
                                    new VkrStuffVersionView(
                                            vkrStuffVersion,
                                            vkrAllowanceRepository.findByVersionID(vkrStuffVersion.getId()).getDocumentStatus().getStatus()
                                    )
                            );
                        } else if (vkrStuffVersion.getEditor() == studentID &&
                                !vkrStuffVersion.getVkrAllowance().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            vkrStuffVersionView.add(
                                    new VkrStuffVersionView(
                                            vkrStuffVersion,
                                            vkrAllowanceRepository.findByVersionID(vkrStuffVersion.getId()).getDocumentStatus().getStatus()
                                    )
                            );
                        }
                        // ППППУиОПД
                    } else if (stuffKind == 7) {
                        if (vkrStuffVersion.getEditor() == advisorID) {
                            vkrStuffVersionView.add(
                                    new VkrStuffVersionView(
                                            vkrStuffVersion,
                                            vkrConclusionRepository.findByVersionID(vkrStuffVersion.getId()).getDocumentStatus().getStatus()
                                    )
                            );
                        } else if (vkrStuffVersion.getEditor() == studentID &&
                                !vkrStuffVersion.getAdvisorConclusion().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            vkrStuffVersionView.add(
                                    new VkrStuffVersionView(
                                            vkrStuffVersion,
                                            vkrConclusionRepository.findByVersionID(vkrStuffVersion.getId()).getDocumentStatus().getStatus()
                                    )
                            );
                        }
                        // ПП
                    } else if (stuffKind == 8) {
                        if (vkrStuffVersion.getEditor() == advisorID) {
                            vkrStuffVersionView.add(
                                    new VkrStuffVersionView(
                                            vkrStuffVersion,
                                            vkrAntiplagiatRepository.findByVersionID(vkrStuffVersion.getId()).getDocumentStatus().getStatus()
                                    )
                            );
                        } else if (vkrStuffVersion.getEditor() == studentID &&
                                !vkrStuffVersion.getVkrAntiplagiat().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            vkrStuffVersionView.add(
                                    new VkrStuffVersionView(
                                            vkrStuffVersion,
                                            vkrAntiplagiatRepository.findByVersionID(vkrStuffVersion.getId()).getDocumentStatus().getStatus()
                                    )
                            );
                        }
                    } else if (stuffKind == 9) {
                        if (vkrStuffVersion.getEditor() == advisorID) {
                            vkrStuffVersionView.add(
                                    new VkrStuffVersionView(
                                            vkrStuffVersion,
                                            vkrPresentationRepository.findByVersionID(vkrStuffVersion.getId()).getDocumentStatus().getStatus()
                                    )
                            );
                        } else if (vkrStuffVersion.getEditor() == studentID &&
                                !vkrStuffVersion.getVkrPresentation().getDocumentStatus().getStatus().equals("Не отправлено")) {
                            vkrStuffVersionView.add(
                                    new VkrStuffVersionView(
                                            vkrStuffVersion,
                                            vkrPresentationRepository.findByVersionID(vkrStuffVersion.getId()).getDocumentStatus().getStatus()
                                    )
                            );
                        }
                    }
                }
                return vkrStuffVersionView;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // Сформировать список представлений загруженных примеров работ научного руководителя
    public List<AdvisorsTemplateView> getAdvisorsLoadedTaskAndReportsTemplates(String token) {
        List<DocumentView> documentViews = getUserDocumentView(token);
        Integer advisorID = associatedStudentsService.getUserId(token);;
        List<AdvisorsTemplateView> advisorsTemplates = new ArrayList<>();
        for (DocumentView documentView: documentViews) {
            if (documentView.getSystemCreatorID() == advisorID) {
                if (documentView.getDocumentKind().equals("Образец")) {
                    Document document = documentRepository.findByCreatorAndName(documentView.getSystemCreatorID(),
                            documentView.getDocumentName());
                    if (document.getView_rights() == 6) {
                        try {
                            ViewRightsArea viewRightsArea = viewRightsAreaRepository.findByDocument(document.getId());
                            ProjectArea projectArea = projectAreaRepository.findById(viewRightsArea.getArea()).get();
                            advisorsTemplates.add(new AdvisorsTemplateView(document, documentView.getDocumentVersions(),
                                    projectArea.getId(), projectArea.getArea()));
                        } catch (NullPointerException nullPointerException) {
                            advisorsTemplates.add(new AdvisorsTemplateView(document, documentView.getDocumentVersions(),
                                    0, "Программа проектов не назначена"));
                        }
                    }
                    else if (document.getView_rights() == 8) {
                        try {
                            ViewRightsProject viewRightsProject = viewRightsProjectRepository.findByDocument(document.getId());
                            Project project = projectRepository.findById(viewRightsProject.getProject()).get();
                            ViewRightsArea viewRightsArea = viewRightsAreaRepository.findByDocument(document.getId());
                            ProjectArea projectArea = projectAreaRepository.findById(project.getProjectArea().getId()).get();
                            AdvisorsTemplateView currentView = new AdvisorsTemplateView(document, documentView.getDocumentVersions(),
                                    project.getId(), project.getName(), true);
                            currentView.setSystemAreaID(projectArea.getId());
                            currentView.setArea(projectArea.getArea());
                            advisorsTemplates.add(currentView);
                        } catch (NullPointerException nullPointerException) {
                            advisorsTemplates.add(new AdvisorsTemplateView(document, documentView.getDocumentVersions(),
                                    0, "Проект не назначен", true));
                        } catch (NoSuchElementException noSuchElementException) {
                            advisorsTemplates.add(new AdvisorsTemplateView(document, documentView.getDocumentVersions(),
                                    0, "Проект не назначен", true));
                        }
                    }
                    else {
                        advisorsTemplates.add(new AdvisorsTemplateView(document, documentView.getDocumentVersions(),
                                0, "Программа проектов не назначена"));
                    }
                }
            }
        }
        return advisorsTemplates;
    }

    // Сформировать список представлений загруженных примеров работ научного руководителя его студенту
    public List<AdvisorsTemplateView> getAdvisorsLoadedTaskAndReportsTemplatesForStudent(String token) {
        Integer studentID = associatedStudentsService.getUserId(token);
        Integer advisorID;
        try {
            advisorID = associatedStudentsRepository.findByStudent(studentID).getScientificAdvisor();
        } catch (NullPointerException nullPointerException) {
            return null;
        }
        List<DocumentView> documentViews = getUserDocumentView(token);
        List<AdvisorsTemplateView> advisorsTemplatesForStudent = new ArrayList<>();
        for (DocumentView documentView: documentViews) {
            if (documentView.getSystemCreatorID() == advisorID) {
                if (documentView.getDocumentKind().equals("Образец")) {
                    Document document = documentRepository.findByCreatorAndName(documentView.getSystemCreatorID(),
                            documentView.getDocumentName());
                    try {
                        ViewRightsArea viewRightsArea = viewRightsAreaRepository.findByDocument(document.getId());
                        ProjectArea projectArea = projectAreaRepository.findById(viewRightsArea.getArea()).get();
                        advisorsTemplatesForStudent.add(new AdvisorsTemplateView(document, null,
                                projectArea.getId(), projectArea.getArea()));
                    } catch (NullPointerException nullPointerException) {
                        advisorsTemplatesForStudent.add(new AdvisorsTemplateView(document, null,
                                0, "Не назначена"));
                    }
                }
            }
        }
        return advisorsTemplatesForStudent;
    }

    // Получить внешнюю ссылку на документ
    public String generateOuterLinkForFile(String token, Integer versionID) throws Exception {
        DocumentVersion documentVersion;
        Document document;
        if (documentVersionRepository.findById(versionID).isPresent()) {
            documentVersion = documentVersionRepository.findById(versionID).get();
            document = documentRepository.findById(documentVersion.getDocument()).get();
            String documentString = document.getCreator() + File.separator + document.getName();
            boolean isEnabled = false;
            List<DocumentView> enabledDocuments = getUserDocumentView(token);
            for (DocumentView documentView : enabledDocuments) {
                String viewString = documentView.getSystemCreatorID() + File.separator + documentView.getDocumentName();
                if (documentString.equals(viewString)) {
                    isEnabled = true;
                    break;
                }
            }
            if (isEnabled) {
                if (document.getKind() != 2) {
                    String documentVersionName = documentVersion.getThis_version_document_path()
                            .substring(documentVersion.getThis_version_document_path().lastIndexOf(File.separator) + 1);
                    String outerAccessString = externalApiUrl + document.getCreator() + "/" + document.getName()
                            + "/" + documentVersionName;
                    return outerAccessString;
                } else if (document.getKind() == 2 && document.getType() != 4) {
                    File viewTaskDirectory = new File(".\\view_tasks");
                    if (!viewTaskDirectory.exists()) {
                        viewTaskDirectory.mkdir();
                    }
                    List<File> viewTaskList = Arrays.asList(viewTaskDirectory.listFiles());
                    for (File file: viewTaskList) {
                        long fileModified = file.lastModified();
                        ZonedDateTime zonedDateTime = ZonedDateTime.now();
                        long currentTime = zonedDateTime.toInstant().toEpochMilli();
                        if (currentTime - fileModified > 120000) {
                            file.delete();
                        }
                    }
                    File viewTask = getThreeViewPages(versionID);
                    String outerAccessString = externalApiUrl + viewTask.getName();
                    return outerAccessString;
                }
            } else {
                return "Данный документ вам не доступен";
            }
        }
        return "Ошибка: не удается найти документ";
    }

    // Получить внешнюю ссылку на документ у которого только одна версия
    public String generateOuterLinkForFileWithOneVersion(String token, Integer creatorID, String documentName) {
        Document document = documentRepository.findByCreatorAndName(creatorID, documentName);
        if (document != null) {
            String documentString = document.getCreator() + File.separator + document.getName();
            boolean isEnabled = false;
            List<DocumentView> enabledDocuments = getUserDocumentView(token);
            for (DocumentView documentView: enabledDocuments) {
                String viewString = documentView.getSystemCreatorID() + File.separator + documentView.getDocumentName();
                if (documentString.equals(viewString)) {
                    isEnabled = true;
                    break;
                }
            }
            if (isEnabled) {
                DocumentVersion documentVersion = documentVersionRepository.findByDocument(document.getId()).get(0);
                String documentVersionName = documentVersion.getThis_version_document_path()
                        .substring(documentVersion.getThis_version_document_path().lastIndexOf(File.separator) + 1);
                String outerAccessString = externalApiUrl + document.getCreator() + "/" + document.getName()
                        + "/" + documentVersionName;
                return outerAccessString;
            } else {
                return "Данный документ вам не доступен";
            }
        } else {
            return "Ошибка: не удается найти документ";
        }
    }

    // Выделить только текст задания
    public File getThreeViewPages(Integer versionID) throws Exception {
        DocumentVersion documentVersion;
        File viewTask = null;
        if (documentVersionRepository.findById(versionID).isPresent()) {
            documentVersion = documentVersionRepository.findById(versionID).get();
            if (documentRepository.findById(documentVersion.getDocument()).isPresent()) {
                com.aspose.words.Document task = new com.aspose.words.Document(documentVersion.getThis_version_document_path());
                com.aspose.words.LayoutCollector layoutCollector = new com.aspose.words.LayoutCollector(task);
                Splitter splitter = new Splitter(layoutCollector);
                int page = 1;
                while (!splitter.getDocText(splitter.getDocumentOfPage(page)).contains("ИНДИВИДУАЛЬНОЕ ЗАДАНИЕ НА ПРОИЗВОДСТВЕННУЮ ПРАКТИКУ")) {
                    page += 1;
                }
                com.aspose.words.Document cutTask = splitter.getDocumentOfPage(page);
                page += 1;
                while (!splitter.getDocText(splitter.getDocumentOfPage(page)).contains("по производственной практике")) {
                    cutTask.appendDocument(splitter.getDocumentOfPage(page), com.aspose.words.ImportFormatMode.KEEP_SOURCE_FORMATTING);
                    page += 1;
                }
                String timeName = ZonedDateTime.now().toString();
                timeName = timeName.replaceAll("\\D", "");
                String tempCutTaskPath = ".\\view_tasks" + File.separator + timeName + documentVersion.getEditor() + ".docx";
                cutTask.save(tempCutTaskPath);
                viewTask = new File(tempCutTaskPath);
            }
        }
        return viewTask;
    }
}

