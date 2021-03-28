package g_server.g_server.application.service.documents;

import com.github.aleksandy.petrovich.Case;
import com.github.aleksandy.petrovich.Gender;
import com.github.aleksandy.petrovich.Petrovich;
import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.documents.*;
import g_server.g_server.application.entity.documents.tasks.NirTask;
import g_server.g_server.application.entity.documents.tasks.PdTask;
import g_server.g_server.application.entity.documents.tasks.PpppuiopdTask;
import g_server.g_server.application.entity.documents.tasks.VkrTask;
import g_server.g_server.application.entity.system_data.EconomyConsultants;
import g_server.g_server.application.entity.system_data.GroupsConsultants;
import g_server.g_server.application.entity.system_data.Speciality;
import g_server.g_server.application.entity.system_data.StudentGroup;
import g_server.g_server.application.entity.users.AssociatedStudents;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.query.response.*;
import g_server.g_server.application.repository.documents.*;
import g_server.g_server.application.repository.system_data.EconomyConsultantsRepository;
import g_server.g_server.application.repository.system_data.GroupsConsultantsRepository;
import g_server.g_server.application.repository.system_data.SpecialityRepository;
import g_server.g_server.application.repository.system_data.StudentGroupRepository;
import g_server.g_server.application.repository.users.AssociatedStudentsRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import g_server.g_server.application.repository.users.UsersRolesRepository;
import g_server.g_server.application.service.documents.text_processor.Splitter;
import g_server.g_server.application.service.users.AssociatedStudentsService;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlOptions;
import org.docx4j.dml.CTBlip;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.*;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DocumentProcessorService {
    @Value("${storage.location}")
    private String storageLocation;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AssociatedStudentsRepository associatedStudentsRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentVersionRepository documentVersionRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    @Autowired
    private OrderPropertiesRepository orderPropertiesRepository;

    @Autowired
    private UsersRolesRepository usersRolesRepository;

    @Autowired
    private AssociatedStudentsService associatedStudentsService;

    @Autowired
    private NirTaskRepository nirTaskRepository;

    @Autowired
    private DocumentUploadService documentUploadService;

    @Autowired
    private PpppuiopdTaskRepository ppppuiopdTaskRepository;

    @Autowired
    private PdTaskRepository pdTaskRepository;

    @Autowired
    private VkrTaskRepository vkrTaskRepository;

    @Autowired
    private StudentGroupRepository studentGroupRepository;

    @Autowired
    private EconomyConsultantsRepository economyConsultantsRepository;

    @Autowired
    private GroupsConsultantsRepository groupsConsultantsRepository;

    // Сгенерировать задание или создать его версию для студента
    public String studentTaskGeneration(String token, ShortTaskDataView shortTaskDataView) throws Exception {
        Integer userID;
        try {
            userID = getUserId(token);
        } catch (Exception e) {
            return "ID студента не найден";
        }
        Users student;
        try {
            student = usersRepository.findById(userID).get();
        } catch (NoSuchElementException noSuchElementException) {
            return "Пользователь не найден";
        }
        if (student != null) {
            AssociatedStudents associatedStudents;
            try {
                associatedStudents = associatedStudentsRepository.findByStudent(student.getId());
            } catch (NullPointerException nullPointerException) {
                associatedStudents = null;
            }
            if (associatedStudents != null) {
                Speciality speciality = specialityRepository.findByPrefix(student.getStudentData()
                        .getStudentGroup().getStudentGroup().substring(0, 4));
                OrderProperties orderProperty;
                Document documentOrder = null;
                try {
                    List<Document> orderList = documentRepository.findByTypeAndKind(determineType(shortTaskDataView.getTaskType()), 1);
                    for (Document order: orderList) {
                        if (order.getOrderProperties().getSpeciality() == speciality.getId()) {
                            documentOrder = order;
                            break;
                        }
                    }
                    if (documentOrder != null) {
                        orderProperty = documentOrder.getOrderProperties();
                    } else {
                        orderProperty = null;
                    }
                } catch (NullPointerException nullPointerException) {
                    orderProperty = null;
                }
                if (orderProperty != null) {
                    Users advisor = usersRepository.findById(associatedStudents.getScientificAdvisor()).get();
                    Users headOfCathedra = usersRepository.findById(
                            usersRolesRepository.findByRoleId(3).getUserId()).get();
                    Integer type = determineType(shortTaskDataView.getTaskType());
                    Integer kind = 5;
                    List<Document> taskList = documentRepository.findByTypeAndKind(
                            type, kind);
                    if (taskList.size() > 0 && taskList.get(0).getTemplateProperties().isApproved()
                            && orderProperty.isApproved()) {
                        TaskDataView taskDataView = fillingTaskDataView(shortTaskDataView, student,
                                advisor, headOfCathedra, documentOrder, orderProperty, speciality);
                        String studentDocumentsPath = storageLocation + File.separator + student.getId();
                        File studentDir = new File(studentDocumentsPath);
                        if (!studentDir.exists()) {
                            studentDir.mkdir();
                        }
                        String fileName = getShortFio(taskDataView.getStudentFio()) + " " +
                                taskDataView.getStudentGroup() + " задание по " + taskDataView.getTaskType() + ".docx";
                        String taskDirPath = studentDocumentsPath + File.separator + fileName;
                        File taskDir = new File(taskDirPath);
                        if (!taskDir.exists()) {
                            taskDir.mkdir();
                        }
                        Document currentTask = taskList.get(taskList.size() - 1);
                        List<DocumentVersion> taskVersions = documentVersionRepository.findByDocument(currentTask.getId());
                        DocumentVersion taskVersion = taskVersions.get(taskVersions.size() - 1);
                        XWPFDocument template = openDocument(taskVersion.getThis_version_document_path());
                        taskProcessing(template, taskDataView, speciality.getSpeciality(), student);
                        String documentVersionPath = taskDirPath + File.separator + "version_" +
                                documentUploadService.getCurrentDate() + ".docx";
                        String response = saveTaskAsDocument(fileName, student, advisor, taskDirPath,
                                taskDataView, documentVersionPath, false);
                        if (!response.equals("Попытка создать версию чужого документа")) {
                            WordReplaceService wordReplaceService = new WordReplaceService(template);
                            wordReplaceService.saveAndGetModdedFile(documentVersionPath);
                        }
                        return response;
                    } else {
                        return "Шаблон задания еще не был загружен";
                    }
                } else {
                    return "Приказ еще не был загружен";
                }
            } else {
                return "Вы еще не были назначены вашему Научному Руководителю";
            }
        } else {
            return "Студент не найден";
        }
    }

    // Сгененрировать или создать версию задания по вкр для студента
    public String studentVkrTaskGeneration(String token, ShortVkrTaskDataView shortVkrTaskDataView) throws Exception {
        Integer userID;
        try {
            userID = getUserId(token);
        } catch (Exception e) {
            return "ID студента не найден";
        }
        Users student;
        try {
            student = usersRepository.findById(userID).get();
        } catch (NoSuchElementException noSuchElementException) {
            return "Пользователь не найден";
        }
        if (student != null) {
            AssociatedStudents associatedStudents;
            try {
                associatedStudents = associatedStudentsRepository.findByStudent(student.getId());
            } catch (NullPointerException nullPointerException) {
                associatedStudents = null;
            }
            if (associatedStudents != null) {
                Speciality speciality = specialityRepository.findByPrefix(student.getStudentData()
                        .getStudentGroup().getStudentGroup().substring(0, 4));
                OrderProperties orderProperty;
                Document documentOrder = null;
                try {
                    List<Document> orderList = documentRepository.findByTypeAndKind(determineType(shortVkrTaskDataView.getTaskType()), 1);
                    for (Document order: orderList) {
                        if (order.getOrderProperties().getSpeciality() == speciality.getId()) {
                            documentOrder = order;
                            break;
                        }
                    }
                    if (documentOrder != null) {
                        orderProperty = documentOrder.getOrderProperties();
                    } else {
                        orderProperty = null;
                    }
                } catch (NullPointerException nullPointerException) {
                    orderProperty = null;
                }
                if (orderProperty != null) {
                    Users advisor = usersRepository.findById(associatedStudents.getScientificAdvisor()).get();
                    Users headOfCathedra = usersRepository.findById(
                            usersRolesRepository.findByRoleId(3).getUserId()).get();
                    Integer type = determineType(shortVkrTaskDataView.getTaskType());
                    Integer kind = 5;
                    List<Document> taskList = documentRepository.findByTypeAndKind(
                            type, kind);
                    if (taskList.size() > 0 && taskList.get(0).getTemplateProperties().isApproved()
                            && orderProperty.isApproved()) {
                        if (type == 4) {
                            VkrTaskDataView vkrTaskDataView = fillingVkrTaskDataView(shortVkrTaskDataView, student,
                                    advisor, headOfCathedra, documentOrder, orderProperty, speciality);
                            String studentDocumentsPath = storageLocation + File.separator + student.getId();
                            File studentDir = new File(studentDocumentsPath);
                            if (!studentDir.exists()) {
                                studentDir.mkdir();
                            }
                            String fileName = getShortFio(vkrTaskDataView.getStudentFio()) + " " +
                                    vkrTaskDataView.getStudentGroup() + " задание по " + vkrTaskDataView.getTaskType() + ".docx";
                            String taskDirPath = studentDocumentsPath + File.separator + fileName;
                            File taskDir = new File(taskDirPath);
                            if (!taskDir.exists()) {
                                taskDir.mkdir();
                            }
                            Document currentTask = taskList.get(taskList.size() - 1);
                            List<DocumentVersion> taskVersions = documentVersionRepository.findByDocument(currentTask.getId());
                            DocumentVersion taskVersion = taskVersions.get(taskVersions.size() - 1);
                            XWPFDocument template = openDocument(taskVersion.getThis_version_document_path());
                            vkrTaskProcessing(template, vkrTaskDataView, speciality.getSpeciality(), student);
                            String documentVersionPath = taskDirPath + File.separator + "version_" +
                                    documentUploadService.getCurrentDate() + ".docx";
                            String response = saveVkrTaskAsDocument(fileName, student, advisor, taskDirPath,
                                    vkrTaskDataView, documentVersionPath, false);
                            if (!response.equals("Попытка создать версию чужого документа")) {
                                WordReplaceService wordReplaceService = new WordReplaceService(template);
                                wordReplaceService.saveAndGetModdedFile(documentVersionPath);
                            }
                            return response;
                        } else {
                            return "Указан несоответствующий ВКР тип задания";
                        }
                    } else {
                        return "Шаблон задания еще не был загружен";
                    }
                } else {
                    return "Приказ еще не был загружен";
                }
            } else {
                return "Вы еще не были назначены вашему Научному Руководителю";
            }
        } else {
            return "Студент не найден";
        }
    }

    // Создать версию задания по вкр студента от его НР
    public String advisorVkrTaskVersionAdd(String token, AdvisorShortVkrTaskDataView shortVkrTaskDataView) throws Exception {
        Integer userID;
        try {
            userID = getUserId(token);
        } catch (Exception e) {
            return "ID научного руководителя не найден";
        }
        Users student;
        Users advisor;
        try {
            student = usersRepository.findById(shortVkrTaskDataView.getStudentID()).get();
            advisor = usersRepository.findById(userID).get();
        } catch (NoSuchElementException noSuchElementException) {
            return "Пользователь не найден";
        }
        if (student != null && advisor != null) {
            AssociatedStudents associatedStudents;
            try {
                associatedStudents = associatedStudentsRepository.findByScientificAdvisorAndStudent(advisor.getId(), student.getId());
            } catch (NullPointerException nullPointerException) {
                associatedStudents = null;
            }
            if (associatedStudents != null) {
                Speciality speciality = specialityRepository.findByPrefix(student.getStudentData()
                        .getStudentGroup().getStudentGroup().substring(0, 4));
                OrderProperties orderProperty;
                Document documentOrder = null;
                try {
                    List<Document> orderList = documentRepository.findByTypeAndKind(determineType(shortVkrTaskDataView.getTaskType()), 1);
                    for (Document order: orderList) {
                        if (order.getOrderProperties().getSpeciality() == speciality.getId()) {
                            documentOrder = order;
                            break;
                        }
                    }
                    if (documentOrder != null) {
                        orderProperty = documentOrder.getOrderProperties();
                    } else {
                        orderProperty = null;
                    }
                } catch (NullPointerException nullPointerException) {
                    orderProperty = null;
                }
                if (orderProperty != null) {
                    Document order = documentRepository.findById(orderProperty.getId()).get();
                    Users headOfCathedra = usersRepository.findById(
                            usersRolesRepository.findByRoleId(3).getUserId()).get();
                    Integer type = determineType(shortVkrTaskDataView.getTaskType());
                    Integer kind = 5;
                    List<Document> taskList = documentRepository.findByTypeAndKind(
                            type, kind);
                    if (taskList.size() > 0) {
                        if (type == 4) {
                            VkrTaskDataView vkrTaskDataView = fillingVkrTaskDataView(shortVkrTaskDataView, student,
                                    advisor, headOfCathedra, order, orderProperty, speciality);
                            String studentDocumentsPath = storageLocation + File.separator + student.getId();
                            File studentDir = new File(studentDocumentsPath);
                            if (!studentDir.exists()) {
                                return "Вы не можете добавлять версии заданию студенту, пока он его не сгенерирует";
                            }
                            String fileName = getShortFio(vkrTaskDataView.getStudentFio()) + " " +
                                    vkrTaskDataView.getStudentGroup() + " задание по " + vkrTaskDataView.getTaskType() + ".docx";
                            String taskDirPath = studentDocumentsPath + File.separator + fileName;
                            File taskDir = new File(taskDirPath);
                            if (!taskDir.exists()) {
                                return "Вы не можете добавлять версии заданию студента, пока он его не сгенерирует";
                            }
                            Document currentTask = taskList.get(taskList.size() - 1);
                            List<DocumentVersion> taskVersions = documentVersionRepository.findByDocument(currentTask.getId());
                            DocumentVersion taskVersion = taskVersions.get(taskVersions.size() - 1);
                            XWPFDocument template = openDocument(taskVersion.getThis_version_document_path());
                            vkrTaskProcessing(template, vkrTaskDataView, speciality.getSpeciality(), student);
                            String documentVersionPath = taskDirPath + File.separator + "version_" +
                                    documentUploadService.getCurrentDate() + ".docx";
                            String response = saveVkrTaskAsDocument(fileName, student, advisor, taskDirPath,
                                    vkrTaskDataView, documentVersionPath, true);
                            if (!response.equals("Попытка создать версию чужого документа")) {
                                WordReplaceService wordReplaceService = new WordReplaceService(template);
                                wordReplaceService.saveAndGetModdedFile(documentVersionPath);
                            }
                            return response;
                        } else {
                            return "Указан несоответствующий ВКР тип задания";
                        }
                    } else {
                        return "Шаблон задания еще не был загружен";
                    }
                } else {
                    return "Приказ еще не был загружен";
                }
            } else {
                return "Запрещено вносить изменения в задания не ваших студентов";
            }
        } else {
            return "Студент не найден";
        }
    }

    // Создать версию задания студента от его НР
    public String advisorTaskVersionAdd(String token, AdvisorShortTaskDataView shortTaskDataView) throws Exception {
        Integer userID;
        try {
            userID = getUserId(token);
        } catch (Exception e) {
            return "ID научного руководителя не найден";
        }
        Users student;
        Users advisor;
        try {
            student = usersRepository.findById(shortTaskDataView.getStudentID()).get();
            advisor = usersRepository.findById(userID).get();
        } catch (NoSuchElementException noSuchElementException) {
            return "Пользователь не найден";
        }
        if (student != null && advisor != null) {
            AssociatedStudents associatedStudents;
            try {
                associatedStudents = associatedStudentsRepository.findByScientificAdvisorAndStudent(advisor.getId(), student.getId());
            } catch (NullPointerException nullPointerException) {
                associatedStudents = null;
            }
            if (associatedStudents != null) {
                Speciality speciality = specialityRepository.findByPrefix(student.getStudentData()
                        .getStudentGroup().getStudentGroup().substring(0, 4));
                OrderProperties orderProperty;
                Document documentOrder = null;
                try {
                    List<Document> orderList = documentRepository.findByTypeAndKind(determineType(shortTaskDataView.getTaskType()), 1);
                    for (Document order: orderList) {
                        if (order.getOrderProperties().getSpeciality() == speciality.getId()) {
                            documentOrder = order;
                            break;
                        }
                    }
                    if (documentOrder != null) {
                        orderProperty = documentOrder.getOrderProperties();
                    } else {
                        orderProperty = null;
                    }
                } catch (NullPointerException nullPointerException) {
                    orderProperty = null;
                }
                if (orderProperty != null) {
                    Document order = documentRepository.findById(orderProperty.getId()).get();
                    Users headOfCathedra = usersRepository.findById(
                            usersRolesRepository.findByRoleId(3).getUserId()).get();
                    Integer type = determineType(shortTaskDataView.getTaskType());
                    Integer kind = 5;
                    List<Document> taskList = documentRepository.findByTypeAndKind(
                            type, kind);
                    if (taskList.size() > 0) {
                        TaskDataView taskDataView = fillingTaskDataView(shortTaskDataView, student,
                                advisor,headOfCathedra, order, orderProperty, speciality);
                        String studentDocumentsPath = storageLocation + File.separator + student.getId();
                        File studentDir = new File(studentDocumentsPath);
                        if (!studentDir.exists()) {
                            return "Вы не можете добавлять версии заданию студенту, пока он его не сгенерирует";
                        }
                        String fileName = getShortFio(taskDataView.getStudentFio()) + " " +
                                taskDataView.getStudentGroup() + " задание по " + taskDataView.getTaskType() + ".docx";
                        String taskDirPath = studentDocumentsPath + File.separator + fileName;
                        File taskDir = new File(taskDirPath);
                        if (!taskDir.exists()) {
                            return "Вы не можете добавлять версии заданию студента, пока он его не сгенерирует";
                        }
                        Document currentTask = taskList.get(taskList.size() - 1);
                        List<DocumentVersion> taskVersions = documentVersionRepository.findByDocument(currentTask.getId());
                        DocumentVersion taskVersion = taskVersions.get(taskVersions.size() - 1);
                        XWPFDocument template = openDocument(taskVersion.getThis_version_document_path());
                        taskProcessing(template, taskDataView, speciality.getSpeciality(), student);
                        String documentVersionPath = taskDirPath + File.separator + "version_" +
                                documentUploadService.getCurrentDate() + ".docx";
                        String response = saveTaskAsDocument(fileName, student, advisor, taskDirPath,
                                taskDataView, documentVersionPath, true);
                        if (!response.equals("Попытка создать версию чужого документа")) {
                            WordReplaceService wordReplaceService = new WordReplaceService(template);
                            wordReplaceService.saveAndGetModdedFile(documentVersionPath);
                        }
                        return response;
                    } else {
                        return "Шаблон задания еще не был загружен";
                    }
                } else {
                    return "Приказ еще не был загружен";
                }
            } else {
                return "Запрещено вносить изменения в задания не ваших студентов";
            }
        } else {
            return "Студент не найден";
        }
    }

    // Заполнить taskDataView
    public TaskDataView fillingTaskDataView(
            ShortTaskDataView shortTaskDataView,
            Users student, Users advisor, Users headOfCathedra,
            Document document, OrderProperties orderProperty,
            Speciality speciality
    ) {
        if (shortTaskDataView.getStudentTheme().length() < 1) {
            shortTaskDataView.setStudentTheme("Введите тему практики");
        }
        if (shortTaskDataView.getToCreate().length() < 1) {
            shortTaskDataView.setToCreate("Создать");
        }
        if (shortTaskDataView.getToExplore().length() < 1) {
            shortTaskDataView.setToExplore("Изучить");
        }
        if (shortTaskDataView.getToFamiliarize().length() < 1) {
            shortTaskDataView.setToFamiliarize("Ознакомиться");
        }
        if (shortTaskDataView.getAdditionalTask().length() < 1) {
            shortTaskDataView.setAdditionalTask("Дополнительное задание");
        }
        TaskDataView taskDataView = new TaskDataView();
        taskDataView.setTaskType(document.getDocumentType().getType());
        taskDataView.setStudentFio(student.getSurname() + " " + student.getName() +
                " " + student.getSecond_name());
        taskDataView.setStudentGroup(student.getStudentData().getStudentGroup().getStudentGroup());
        taskDataView.setStudentTheme(shortTaskDataView.getStudentTheme());
        taskDataView.setAdvisorFio(advisor.getSurname() + " " + advisor.getName() +
                " " + advisor.getSecond_name());
        taskDataView.setHeadFio(headOfCathedra.getSurname() + " " + headOfCathedra.getName() +
                " " + headOfCathedra.getSecond_name());
        taskDataView.setCathedra(student.getStudentData().getCathedras().getCathedraName());
        taskDataView.setOrderNumber(orderProperty.getNumber());
        taskDataView.setOrderDate(associatedStudentsService.convertSQLDateToRussianFormat(orderProperty.getOrderDate()));
        taskDataView.setOrderStartDate(associatedStudentsService.convertSQLDateToRussianFormat(orderProperty.getStartDate()));
        taskDataView.setOrderEndDate(associatedStudentsService.convertSQLDateToRussianFormat(orderProperty.getEndDate()));
        taskDataView.setOrderSpeciality(speciality.getCode());
        taskDataView.setToExplore(shortTaskDataView.getToExplore());
        taskDataView.setToCreate(shortTaskDataView.getToCreate());
        taskDataView.setToFamiliarize(shortTaskDataView.getToFamiliarize());
        taskDataView.setAdditionalTask(shortTaskDataView.getAdditionalTask());
        return taskDataView;
    }

    // Заполнить VkrTaskDataView
    public VkrTaskDataView fillingVkrTaskDataView(ShortVkrTaskDataView shortVkrTaskDataView,
            Users student, Users advisor, Users headOfCathedra,
            Document document, OrderProperties orderProperty,
            Speciality speciality) {
        if (shortVkrTaskDataView.getStudentTheme().length() < 1) {
            shortVkrTaskDataView.setStudentTheme("Введите тему вкр");
        }
        if (shortVkrTaskDataView.getVkrAim().length() < 1) {
            shortVkrTaskDataView.setVkrAim("Цель");
        }
        if (shortVkrTaskDataView.getVkrTasks().length() < 1) {
            shortVkrTaskDataView.setVkrTasks("Задачи");
        }
        if (shortVkrTaskDataView.getVkrDocs().length() < 1) {
            shortVkrTaskDataView.setVkrDocs("Документы и графические материалы");
        }
        VkrTaskDataView vkrTaskDataView = new VkrTaskDataView();
        vkrTaskDataView.setTaskType(document.getDocumentType().getType());
        vkrTaskDataView.setStudentFio(student.getSurname() + " " + student.getName() +
                " " + student.getSecond_name());
        vkrTaskDataView.setStudentGroup(student.getStudentData().getStudentGroup().getStudentGroup());
        vkrTaskDataView.setStudentTheme(shortVkrTaskDataView.getStudentTheme());
        vkrTaskDataView.setAdvisorFio(advisor.getSurname() + " " + advisor.getName() +
                " " + advisor.getSecond_name());
        vkrTaskDataView.setEconomyConsultantFio(advisor.getSurname() + " " + advisor.getName() +
                " " + advisor.getSecond_name());
        vkrTaskDataView.setHeadFio(headOfCathedra.getSurname() + " " + headOfCathedra.getName() +
                " " + headOfCathedra.getSecond_name());
        vkrTaskDataView.setStudentCode(student.getStudentData().getStudentCode());
        vkrTaskDataView.setCathedra(student.getStudentData().getCathedras().getCathedraName());
        vkrTaskDataView.setOrderNumber(orderProperty.getNumber());
        vkrTaskDataView.setOrderDate(associatedStudentsService.convertSQLDateToRussianFormat(orderProperty.getOrderDate()));
        vkrTaskDataView.setOrderStartDate(associatedStudentsService.convertSQLDateToRussianFormat(orderProperty.getStartDate()));
        vkrTaskDataView.setOrderEndDate(associatedStudentsService.convertSQLDateToRussianFormat(orderProperty.getEndDate()));
        vkrTaskDataView.setOrderSpeciality(speciality.getCode());
        vkrTaskDataView.setTaskAim(shortVkrTaskDataView.getVkrAim());
        vkrTaskDataView.setTaskTasks(shortVkrTaskDataView.getVkrTasks());
        vkrTaskDataView.setTaskDocs(shortVkrTaskDataView.getVkrDocs());
        return vkrTaskDataView;
    }

    // Обработать шаблон
    public void taskProcessing(XWPFDocument template, TaskDataView taskDataView, String speciality, Users student)
            throws Exception {
        AssociatedStudents associatedStudents = associatedStudentsRepository.findByStudent(student.getId());
        Users advisor = associatedStudents.getAdvisorUser();
        WordReplaceService wordReplaceService = new WordReplaceService(template);
        String studentTheme = "«" + taskDataView.getStudentTheme() + "»";
        // Заменим слова в тексте документа
        wordReplaceService.replaceWordsInText("Короткая дата начала НИР", taskDataView.getOrderStartDate());
        wordReplaceService.replaceWordsInText("Короткая дата начала ППППУИОПД", taskDataView.getOrderStartDate());
        wordReplaceService.replaceWordsInText("Короткая дата начала ПП", taskDataView.getOrderStartDate());

        wordReplaceService.replaceWordsInText("Короткая дата конца НИР", taskDataView.getOrderEndDate());
        wordReplaceService.replaceWordsInText("Короткая дата конца ППППУИОПД", taskDataView.getOrderEndDate());
        wordReplaceService.replaceWordsInText("Короткая дата конца ПП", taskDataView.getOrderEndDate());

        wordReplaceService.replaceWordsInText("Дата начала НИР без кавычек", getSecondDateType(taskDataView.getOrderStartDate()));
        wordReplaceService.replaceWordsInText("Дата начала ППППУИОПД без кавычек", getSecondDateType(taskDataView.getOrderStartDate()));
        wordReplaceService.replaceWordsInText("Дата начала ПП без кавычек", getSecondDateType(taskDataView.getOrderStartDate()));

        wordReplaceService.replaceWordsInText("Дата конца НИР без кавычек", getSecondDateType(taskDataView.getOrderEndDate()));
        wordReplaceService.replaceWordsInText("Дата конца ППППУИОПД без кавычек", getSecondDateType(taskDataView.getOrderEndDate()));
        wordReplaceService.replaceWordsInText("Дата конца ПП без кавычек", getSecondDateType(taskDataView.getOrderEndDate()));

        wordReplaceService.replaceWordsInText("Согласованное название темы", studentTheme);
        wordReplaceService.replaceWordsInText("Дата выхода приказа", getFirstDateType(taskDataView.getOrderDate()));

        wordReplaceService.replaceWordsInText("Дата начала НИР", getFirstDateType(taskDataView.getOrderStartDate()));
        wordReplaceService.replaceWordsInText("Дата начала ППППУИОПД", getFirstDateType(taskDataView.getOrderStartDate()));
        wordReplaceService.replaceWordsInText("Дата начала ПП", getFirstDateType(taskDataView.getOrderStartDate()));

        wordReplaceService.replaceWordsInText("Дата конца НИР", getFirstDateType(taskDataView.getOrderEndDate()));
        wordReplaceService.replaceWordsInText("Дата конца ППППУИОПД", getFirstDateType(taskDataView.getOrderEndDate()));
        wordReplaceService.replaceWordsInText("Дата конца ПП", getFirstDateType(taskDataView.getOrderEndDate()));

        wordReplaceService.replaceWordsInText("Номер приказа", taskDataView.getOrderNumber());
        wordReplaceService.replaceWordsInText("КАФЕДРА", taskDataView.getCathedra());
        wordReplaceService.replaceWordsInText("ГРУППА", taskDataView.getStudentGroup());
        wordReplaceService.replaceWordsInText("Код специальности", taskDataView.getOrderSpeciality());
        wordReplaceService.replaceWordsInText("Название специальности", speciality);
        wordReplaceService.replaceWordsInText("ФИОРС", getShortFio(getRpFio(student.getSurname(), student.getName(), student.getSecond_name())));
        wordReplaceService.replaceWordsInText("ФИОПД", getDpFio(student.getSurname(), student.getName(), student.getSecond_name()));
        wordReplaceService.replaceWordsInText("ФИОПР", getRpFio(student.getSurname(), student.getName(), student.getSecond_name()));
        wordReplaceService.replaceWordsInText("ФИО С", getShortFio(taskDataView.getStudentFio()));
        wordReplaceService.replaceWordsInText("ФИО НР", getShortFio(taskDataView.getAdvisorFio()));
        wordReplaceService.replaceWordsInText("ФИО ЗВК", getShortFio(taskDataView.getHeadFio()));
        wordReplaceService.replaceWordsInText("ПНР", advisor.getScientificAdvisorData().getPost());
        wordReplaceService.replaceWordsInText("ИЗУЧИТЬ", toLowerCaseFirstSymbol(taskDataView.getToExplore()));
        wordReplaceService.replaceWordsInText("СОЗДАТЬ", toLowerCaseFirstSymbol(taskDataView.getToCreate()));
        wordReplaceService.replaceWordsInText("ОЗНАКОМИТЬСЯ", toLowerCaseFirstSymbol(taskDataView.getToFamiliarize()));
        wordReplaceService.replaceWordsInText("ДОПЗАДАНИЕ", toLowerCaseFirstSymbol(taskDataView.getAdditionalTask()));
        wordReplaceService.replaceWordsInText("ГОД", taskDataView.getOrderStartDate().substring(6, 10));

        // Заменим слова в таблицах документа
        wordReplaceService.replaceWordsInTables("Короткая дата начала НИР", taskDataView.getOrderStartDate());
        wordReplaceService.replaceWordsInTables("Короткая дата начала ППППУИОПД", taskDataView.getOrderStartDate());
        wordReplaceService.replaceWordsInTables("Короткая дата начала ПП", taskDataView.getOrderStartDate());

        wordReplaceService.replaceWordsInTables("Короткая дата конца НИР", taskDataView.getOrderEndDate());
        wordReplaceService.replaceWordsInTables("Короткая дата конца ППППУИОПД", taskDataView.getOrderEndDate());
        wordReplaceService.replaceWordsInTables("Короткая дата конца ПП", taskDataView.getOrderEndDate());

        wordReplaceService.replaceWordsInTables("Дата начала НИР без кавычек", getSecondDateType(taskDataView.getOrderStartDate()));
        wordReplaceService.replaceWordsInTables("Дата начала ППППУИОПД без кавычек", getSecondDateType(taskDataView.getOrderStartDate()));
        wordReplaceService.replaceWordsInTables("Дата начала ПП без кавычек", getSecondDateType(taskDataView.getOrderStartDate()));

        wordReplaceService.replaceWordsInTables("Дата конца НИР без кавычек", getSecondDateType(taskDataView.getOrderEndDate()));
        wordReplaceService.replaceWordsInTables("Дата конца ППППУИОПД без кавычек", getSecondDateType(taskDataView.getOrderEndDate()));
        wordReplaceService.replaceWordsInTables("Дата конца ПП без кавычек", getSecondDateType(taskDataView.getOrderEndDate()));

        wordReplaceService.replaceWordsInTables("Согласованное название темы", studentTheme);
        wordReplaceService.replaceWordsInTables("Дата выхода приказа", getFirstDateType(taskDataView.getOrderDate()));

        wordReplaceService.replaceWordsInTables("Дата начала НИР", getFirstDateType(taskDataView.getOrderStartDate()));
        wordReplaceService.replaceWordsInTables("Дата начала ППППУИОПД", getFirstDateType(taskDataView.getOrderStartDate()));
        wordReplaceService.replaceWordsInTables("Дата начала ПП", getFirstDateType(taskDataView.getOrderStartDate()));

        wordReplaceService.replaceWordsInTables("Дата конца НИР", getFirstDateType(taskDataView.getOrderEndDate()));
        wordReplaceService.replaceWordsInTables("Дата конца ППППУИОПД", getFirstDateType(taskDataView.getOrderEndDate()));
        wordReplaceService.replaceWordsInTables("Дата конца ПП", getFirstDateType(taskDataView.getOrderEndDate()));

        wordReplaceService.replaceWordsInTables("Номер приказа", taskDataView.getOrderNumber());
        wordReplaceService.replaceWordsInTables("КАФЕДРА", taskDataView.getCathedra());
        wordReplaceService.replaceWordsInTables("ГРУППА", taskDataView.getStudentGroup());
        wordReplaceService.replaceWordsInTables("Код специальности", taskDataView.getOrderSpeciality());
        wordReplaceService.replaceWordsInTables("Название специальности", speciality);
        wordReplaceService.replaceWordsInTables("ФИОРС", getShortFio(getRpFio(student.getSurname(), student.getName(), student.getSecond_name())));
        wordReplaceService.replaceWordsInText("ФИОПД", getDpFio(student.getSurname(), student.getName(), student.getSecond_name()));
        wordReplaceService.replaceWordsInText("ФИОПР", getRpFio(student.getSurname(), student.getName(), student.getSecond_name()));
        wordReplaceService.replaceWordsInTables("ФИО С", getShortFio(taskDataView.getStudentFio()));
        wordReplaceService.replaceWordsInTables("ФИО НР", getShortFio(taskDataView.getAdvisorFio()));
        wordReplaceService.replaceWordsInTables("ФИО ЗВК", getShortFio(taskDataView.getHeadFio()));
        wordReplaceService.replaceWordsInTables("ПНР", advisor.getScientificAdvisorData().getPost());
        if (taskDataView.getToExplore().equals("Изучить")) {
            wordReplaceService.replaceWordsInTables("ИЗУЧИТЬ", taskDataView.getToExplore());
        } else {
            wordReplaceService.replaceWordsInTables("ИЗУЧИТЬ", "Изучить " + taskDataView.getToExplore());
        }
        wordReplaceService.replaceWordsInTables("СОЗДАТЬ", toUpperCaseFirstSymbol(taskDataView.getToCreate()));
        if (taskDataView.getToFamiliarize().equals("Ознакомиться")) {
            wordReplaceService.replaceWordsInTables("ОЗНАКОМИТЬСЯ", taskDataView.getToFamiliarize());
        } else {
            wordReplaceService.replaceWordsInTables("ОЗНАКОМИТЬСЯ", "Ознакомиться " + taskDataView.getToFamiliarize());
        }
        wordReplaceService.replaceWordsInTables("ДОПЗАДАНИЕ", toUpperCaseFirstSymbol(taskDataView.getAdditionalTask()));
    }

    // Обработать шаблон задания на ВКР
    public void vkrTaskProcessing(XWPFDocument template, VkrTaskDataView vkrTaskDataView, String speciality, Users student) {
        WordReplaceService wordReplaceService = new WordReplaceService(template);
        AssociatedStudents associatedStudents = associatedStudentsRepository.findByStudent(student.getId());
        Users advisor = associatedStudents.getAdvisorUser();
        String studentTheme = vkrTaskDataView.getStudentTheme();
        String studentGroup = vkrTaskDataView.getStudentGroup();
        StudentGroup entityStudentGroup = studentGroupRepository.getByStudentGroup(studentGroup);
        GroupsConsultants groupsConsultants = groupsConsultantsRepository.findByGroupID(entityStudentGroup.getId());
        EconomyConsultants economyConsultant = economyConsultantsRepository.findById(groupsConsultants.getConsultantID()).get();

        // Заменим слова в тексте документа
        wordReplaceService.replaceWordsInText("Дата выдачи задания", getFirstDateType(vkrTaskDataView.getOrderStartDate()));
        wordReplaceService.replaceWordsInText("Согласованное название темы", studentTheme);
        wordReplaceService.replaceWordsInText("КАФЕДРА", vkrTaskDataView.getCathedra());
        wordReplaceService.replaceWordsInText("ГРУППА", vkrTaskDataView.getStudentGroup());
        wordReplaceService.replaceWordsInText("ШИФР", vkrTaskDataView.getStudentCode());
        wordReplaceService.replaceWordsInText("Код специальности", vkrTaskDataView.getOrderSpeciality());
        wordReplaceService.replaceWordsInText("Название специальности", speciality);
        wordReplaceService.replaceWordsInTables("ФИО СИП", vkrTaskDataView.getStudentFio());
        wordReplaceService.replaceWordsInText("ФИО С", getShortFio(vkrTaskDataView.getStudentFio()));
        wordReplaceService.replaceWordsInText("ФИО НРП", vkrTaskDataView.getAdvisorFio());
        wordReplaceService.replaceWordsInText("ФИО НР", getShortFio(vkrTaskDataView.getAdvisorFio()));
        wordReplaceService.replaceWordsInText("ФИО ЗВК", getShortFio(vkrTaskDataView.getHeadFio()));
        wordReplaceService.replaceWordsInText("ФИО ЭКП", economyConsultant.getFio());
        wordReplaceService.replaceWordsInText("ФИО ЭК", getShortFio(economyConsultant.getFio()));
        wordReplaceService.replaceWordsInText("ЭКДОЛЖНОСТЬ", economyConsultant.getPost());
        wordReplaceService.replaceWordsInText("ПНР", advisor.getScientificAdvisorData().getPost());
        wordReplaceService.replaceWordsInText("ГОД", vkrTaskDataView.getOrderStartDate().substring(6, 10));
        wordReplaceService.replaceWordsInText("ЦЕЛЬВКР", vkrTaskDataView.getTaskAim());
        wordReplaceService.replaceWordsInText("ЗАДАЧИ", vkrTaskDataView.getTaskTasks());
        wordReplaceService.replaceWordsInText("Документы", vkrTaskDataView.getTaskDocs());

        // Заменим слова в таблицах документа
        wordReplaceService.replaceWordsInTables("Дата выдачи задания", getFirstDateType(vkrTaskDataView.getOrderStartDate()));
        wordReplaceService.replaceWordsInTables("Согласованное название темы", studentTheme);
        wordReplaceService.replaceWordsInTables("Номер приказа", vkrTaskDataView.getOrderNumber());
        wordReplaceService.replaceWordsInTables("КАФЕДРА", vkrTaskDataView.getCathedra());
        wordReplaceService.replaceWordsInTables("ГРУППА", vkrTaskDataView.getStudentGroup());
        wordReplaceService.replaceWordsInTables("ШИФР", vkrTaskDataView.getStudentCode());
        wordReplaceService.replaceWordsInTables("Код специальности", vkrTaskDataView.getOrderSpeciality());
        wordReplaceService.replaceWordsInTables("Название специальности", speciality);
        wordReplaceService.replaceWordsInTables("ФИО СИП", vkrTaskDataView.getStudentFio());
        wordReplaceService.replaceWordsInTables("ФИО С", getShortFio(vkrTaskDataView.getStudentFio()));
        wordReplaceService.replaceWordsInTables("ФИО НРП", vkrTaskDataView.getAdvisorFio());
        wordReplaceService.replaceWordsInTables("ФИО НР", getShortFio(vkrTaskDataView.getAdvisorFio()));
        wordReplaceService.replaceWordsInTables("ФИО ЗВК", getShortFio(vkrTaskDataView.getHeadFio()));
        // wordReplaceService.replaceWordsInTables("ФИО ЭКП", economyConsultant.getFio());
        wordReplaceService.replaceWordsInTables("ФИО ЭК", economyConsultant.getFio());
        wordReplaceService.replaceWordsInTables("ЭКДОЛЖНОСТЬ", economyConsultant.getPost());
        wordReplaceService.replaceWordsInTables("ПНР", advisor.getScientificAdvisorData().getPost());
        wordReplaceService.replaceWordsInTables("ЦЕЛЬВКР", vkrTaskDataView.getTaskAim());
        wordReplaceService.replaceWordsInTables("ЗАДАЧИ", vkrTaskDataView.getTaskTasks());
        wordReplaceService.replaceWordsInTables("Документы", vkrTaskDataView.getTaskDocs());
    }

    public void reportProcessing(File templateFile, String detailedContent, String advisorsConclusion) throws Exception {
        InputStream inputStream = new FileInputStream(templateFile);
        XWPFDocument template = new XWPFDocument(inputStream);
        WordReplaceService wordReplaceService = new WordReplaceService(template);
        wordReplaceService.replaceWordsInText("ДСОДЕРЖАНИЕ", detailedContent);
        wordReplaceService.replaceWordsInText("ЗАКЛЮЧЕНИЕ", advisorsConclusion);
        inputStream.close();
        wordReplaceService.saveAndGetModdedFile(templateFile);
    }

    // Сохранить задание как документ
    public String saveTaskAsDocument(String filename, Users student, Users advisor, String studentDocumentsPath,
                                     TaskDataView taskDataView, String documentVersionPath, boolean flag) {
        Document document = documentRepository.findByCreatorAndName(student.getId(), filename);
        if (document == null && !flag) {
            Integer kind = 2;
            Integer type = determineType(taskDataView.getTaskType());
            Document newDocument = new Document(
                    student.getId(),
                    filename,
                    studentDocumentsPath,
                    documentUploadService.convertRussianDateToSqlDate(documentUploadService.getCurrentDate()),
                    type,
                    kind,
                    "Задание по " + taskDataView.getTaskType() + " " + getShortFio(
                            student.getSurname() + " " + student.getName() + " " + student.getSecond_name()) + " " +
                            student.getStudentData().getStudentGroup().getStudentGroup()
                    ,
                    7
            );
            documentRepository.save(newDocument);
            DocumentVersion documentVersion = new DocumentVersion(
                    student.getId(),
                    newDocument.getId(),
                    documentUploadService.convertRussianDateToSqlDateTime(documentUploadService.getCurrentDate()),
                    "Генерация задания по " + taskDataView.getTaskType() + " на сайте",
                    documentVersionPath
            );
            documentVersionRepository.save(documentVersion);
            if (type == 1) {
                NirTask nirTask = new NirTask(
                        documentVersion.getId(), taskDataView.getStudentTheme(), taskDataView.getToExplore(),
                        taskDataView.getToCreate(), taskDataView.getToFamiliarize(), taskDataView.getAdditionalTask(), 1
                );
                nirTaskRepository.save(nirTask);
            } else if (type == 2) {
                PpppuiopdTask ppppuiopdTask = new PpppuiopdTask(
                        documentVersion.getId(), taskDataView.getStudentTheme(), taskDataView.getToExplore(),
                        taskDataView.getToCreate(), taskDataView.getToFamiliarize(), taskDataView.getAdditionalTask(), 1
                );
                ppppuiopdTaskRepository.save(ppppuiopdTask);
            } else if (type == 3) {
                PdTask pdTask = new PdTask(
                        documentVersion.getId(), taskDataView.getStudentTheme(), taskDataView.getToExplore(),
                        taskDataView.getToCreate(), taskDataView.getToFamiliarize(), taskDataView.getAdditionalTask(), 1
                );
                pdTaskRepository.save(pdTask);
            }
            return documentVersion.getId() + "," + getRussianDateTime(documentVersion.getEditionDate());
        } else if (document != null && !flag) {
            Integer type = determineType(taskDataView.getTaskType());
            if (document.getCreator() == student.getId()) {
                DocumentVersion documentVersion = new DocumentVersion(
                        student.getId(),
                        document.getId(),
                        documentUploadService.convertRussianDateToSqlDateTime(documentUploadService.getCurrentDate()),
                        "Добавление новой версии задания по " + taskDataView.getTaskType(),
                        documentVersionPath
                );
                documentVersionRepository.save(documentVersion);
                if (type == 1) {
                    NirTask nirTask = new NirTask(
                            documentVersion.getId(), taskDataView.getStudentTheme(), taskDataView.getToExplore(),
                            taskDataView.getToCreate(), taskDataView.getToFamiliarize(), taskDataView.getAdditionalTask(), 1
                    );
                    nirTaskRepository.save(nirTask);
                } else if (type == 2) {
                    PpppuiopdTask ppppuiopdTask = new PpppuiopdTask(
                            documentVersion.getId(), taskDataView.getStudentTheme(), taskDataView.getToExplore(),
                            taskDataView.getToCreate(), taskDataView.getToFamiliarize(), taskDataView.getAdditionalTask(), 1
                    );
                    ppppuiopdTaskRepository.save(ppppuiopdTask);
                } else if (type == 3) {
                    PdTask pdTask = new PdTask(
                            documentVersion.getId(), taskDataView.getStudentTheme(), taskDataView.getToExplore(),
                            taskDataView.getToCreate(), taskDataView.getToFamiliarize(), taskDataView.getAdditionalTask(), 1
                    );
                    pdTaskRepository.save(pdTask);
                }
                return documentVersion.getId() + "," + getRussianDateTime(documentVersion.getEditionDate());
            } else {
                return "Попытка создать версию чужого документа";
            }
        } else if (document != null && flag) {
            Integer type = determineType(taskDataView.getTaskType());
            DocumentVersion documentVersion = new DocumentVersion(
                    advisor.getId(),
                    document.getId(),
                    documentUploadService.convertRussianDateToSqlDateTime(documentUploadService.getCurrentDate()),
                    "Добавление новой версии задания по " + taskDataView.getTaskType() + " научным руководителем "
                            + getShortFio(advisor.getSurname() + " " + advisor.getName() + " " + advisor.getSecond_name()),
                    documentVersionPath
            );
            documentVersionRepository.save(documentVersion);
            if (type == 1) {
                NirTask nirTask = new NirTask(
                        documentVersion.getId(), taskDataView.getStudentTheme(), taskDataView.getToExplore(),
                        taskDataView.getToCreate(), taskDataView.getToFamiliarize(), taskDataView.getAdditionalTask(), 1
                );
                nirTaskRepository.save(nirTask);
            } else if (type == 2) {
                PpppuiopdTask ppppuiopdTask = new PpppuiopdTask(
                        documentVersion.getId(), taskDataView.getStudentTheme(), taskDataView.getToExplore(),
                        taskDataView.getToCreate(), taskDataView.getToFamiliarize(), taskDataView.getAdditionalTask(), 1
                );
                ppppuiopdTaskRepository.save(ppppuiopdTask);
            } else if (type == 3) {
                PdTask pdTask = new PdTask(
                        documentVersion.getId(), taskDataView.getStudentTheme(), taskDataView.getToExplore(),
                        taskDataView.getToCreate(), taskDataView.getToFamiliarize(), taskDataView.getAdditionalTask(), 1
                );
                pdTaskRepository.save(pdTask);
            }
            return documentVersion.getId() + "," + getRussianDateTime(documentVersion.getEditionDate());
        }
        return "Извините, что-то пошло не так";
    }

    // Сохранить задание на ВКР как документ
    public String saveVkrTaskAsDocument(String filename, Users student, Users advisor, String studentDocumentsPath,
            VkrTaskDataView vkrTaskDataView, String documentVersionPath, boolean flag) {
        Document document = documentRepository.findByCreatorAndName(student.getId(), filename);
        if (document == null && !flag) {
            Integer kind = 2;
            Integer type = determineType(vkrTaskDataView.getTaskType());
            Document newDocument = new Document(
                    student.getId(),
                    filename,
                    studentDocumentsPath,
                    documentUploadService.convertRussianDateToSqlDate(documentUploadService.getCurrentDate()),
                    type,
                    kind,
                    "Задание по " + vkrTaskDataView.getTaskType() + " " + getShortFio(
                            student.getSurname() + " " + student.getName() + " " + student.getSecond_name()) + " " +
                            student.getStudentData().getStudentGroup().getStudentGroup()
                    ,
                    7
            );
            documentRepository.save(newDocument);
            DocumentVersion documentVersion = new DocumentVersion(
                    student.getId(),
                    newDocument.getId(),
                    documentUploadService.convertRussianDateToSqlDateTime(documentUploadService.getCurrentDate()),
                    "Генерация задания по " + vkrTaskDataView.getTaskType() + " на сайте",
                    documentVersionPath
            );
            documentVersionRepository.save(documentVersion);
            if (type == 4) {
                VkrTask vkrTask = new VkrTask(
                        documentVersion.getId(), vkrTaskDataView.getStudentTheme(), vkrTaskDataView.getTaskAim(),
                        vkrTaskDataView.getTaskTasks(), vkrTaskDataView.getTaskDocs() , 1
                );
                vkrTaskRepository.save(vkrTask);
            }
            return documentVersion.getId() + "," + getRussianDateTime(documentVersion.getEditionDate());
        } else if (document != null && !flag) {
            Integer type = determineType(vkrTaskDataView.getTaskType());
            if (document.getCreator() == student.getId()) {
                DocumentVersion documentVersion = new DocumentVersion(
                        student.getId(),
                        document.getId(),
                        documentUploadService.convertRussianDateToSqlDateTime(documentUploadService.getCurrentDate()),
                        "Добавление новой версии задания по " + vkrTaskDataView.getTaskType(),
                        documentVersionPath
                );
                documentVersionRepository.save(documentVersion);
                if (type == 4) {
                    VkrTask vkrTask = new VkrTask(
                            documentVersion.getId(), vkrTaskDataView.getStudentTheme(), vkrTaskDataView.getTaskAim(),
                            vkrTaskDataView.getTaskTasks(), vkrTaskDataView.getTaskDocs() , 1
                    );
                    vkrTaskRepository.save(vkrTask);
                }
                return documentVersion.getId() + "," + getRussianDateTime(documentVersion.getEditionDate());
            } else {
                return "Попытка создать версию чужого документа";
            }
        } else if (document != null && flag) {
            Integer type = determineType(vkrTaskDataView.getTaskType());
            DocumentVersion documentVersion = new DocumentVersion(
                    advisor.getId(),
                    document.getId(),
                    documentUploadService.convertRussianDateToSqlDateTime(documentUploadService.getCurrentDate()),
                    "Добавление новой версии задания по " + vkrTaskDataView.getTaskType() + " научным руководителем "
                            + getShortFio(advisor.getSurname() + " " + advisor.getName() + " " + advisor.getSecond_name()),
                    documentVersionPath
            );
            documentVersionRepository.save(documentVersion);
            if (type == 4) {
                VkrTask vkrTask = new VkrTask(
                        documentVersion.getId(), vkrTaskDataView.getStudentTheme(), vkrTaskDataView.getTaskAim(),
                        vkrTaskDataView.getTaskTasks(), vkrTaskDataView.getTaskDocs() , 1
                );
                vkrTaskRepository.save(vkrTask);
                return documentVersion.getId() + "," + getRussianDateTime(documentVersion.getEditionDate());
            }
        }
        return "Извините, что-то пошло не так";
    }

    @Deprecated
    // Объединить вордовские документы спомощью docx4j
    public void makeUsWhole(File finalReport, File tempReport) throws Docx4JException, JAXBException, FileNotFoundException, IOException {
        InputStream inputStream = new FileInputStream(finalReport);
        XWPFDocument taskToInsertPageBreak = new XWPFDocument(inputStream);
        taskToInsertPageBreak.createParagraph().setPageBreak(true);
        FileOutputStream out = new FileOutputStream(finalReport);
        taskToInsertPageBreak.write(out);
        out.close(); inputStream.close();

        WordprocessingMLPackage f = WordprocessingMLPackage.load(finalReport);
        WordprocessingMLPackage s = WordprocessingMLPackage.load(tempReport);

        List body = s.getMainDocumentPart().getJAXBNodesViaXPath("//w:body", false);
        for(Object b : body){
            List words = ((org.docx4j.wml.Body)b).getContent();
            for(Object k : words)
                f.getMainDocumentPart().addObject(k);
        }

        List<Object> blips = s.getMainDocumentPart().getJAXBNodesViaXPath("//a:blip", false);
        for(Object el : blips){
            try {
                CTBlip blip = (CTBlip) el;
                RelationshipsPart parts = s.getMainDocumentPart().getRelationshipsPart();
                Relationship rel = parts.getRelationshipByID(blip.getEmbed());
                Part part = parts.getPart(rel);
                if(part instanceof ImagePngPart)
                    System.out.println(((ImagePngPart) part).getBytes());
                if(part instanceof ImageJpegPart)
                    System.out.println(((ImageJpegPart) part).getBytes());
                if(part instanceof ImageBmpPart)
                    System.out.println(((ImageBmpPart) part).getBytes());
                if(part instanceof ImageGifPart)
                    System.out.println(((ImageGifPart) part).getBytes());
                if(part instanceof ImageEpsPart)
                    System.out.println(((ImageEpsPart) part).getBytes());
                if(part instanceof ImageTiffPart)
                    System.out.println(((ImageTiffPart) part).getBytes());
                Relationship newrel = f.getMainDocumentPart().addTargetPart(part, RelationshipsPart.AddPartBehaviour.RENAME_IF_NAME_EXISTS);
                blip.setEmbed(newrel.getId());
                f.getMainDocumentPart().addTargetPart(s.getParts().getParts().get(new PartName("/word/"+rel.getTarget())));
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
        f.save(finalReport);
    }

    @Deprecated
    // Объединить задание с отчётом
    public void makeUsWhole(InputStream taskVersion, InputStream reportVersion, OutputStream destination) throws Exception {
        OPCPackage taskPackage = OPCPackage.open(taskVersion);
        OPCPackage reportPackage = OPCPackage.open(reportVersion);
        XWPFDocument taskDocument = new XWPFDocument(taskPackage);
        CTBody taskBody = taskDocument.getDocument().getBody();
        XWPFDocument reportDocument = new XWPFDocument(reportPackage);
        CTBody reportBody = reportDocument.getDocument().getBody();
        appendBody(taskBody, reportBody);
        taskDocument.write(destination);
    }

    // Метод добавления тела документу
    private static void appendBody(CTBody src, CTBody append) throws Exception {
        XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setSaveOuter();
        String appendString = append.xmlText(optionsOuter);
        String srcString = src.xmlText();
        String prefix = srcString.substring(0,srcString.indexOf(">")+1);
        String mainPart = srcString.substring(srcString.indexOf(">")+1,srcString.lastIndexOf("<"));
        String sufix = srcString.substring( srcString.lastIndexOf("<") );
        String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));
        CTBody makeBody = CTBody.Factory.parse(prefix+mainPart+addPart+sufix);
        src.set(makeBody);
    }

    // Выделить только текст задания
    public File getThreePages(Integer versionID) throws Exception {
        DocumentVersion documentVersion;
        File downloadTask = null;
        if (documentVersionRepository.findById(versionID).isPresent()) {
            Document document;
            documentVersion = documentVersionRepository.findById(versionID).get();
            if (documentRepository.findById(documentVersion.getDocument()).isPresent()) {
                document = documentRepository.findById(documentVersion.getDocument()).get();
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
                String tempCutTaskPath = document.getDocument_path() + File.separator + "temp_task_download.docx";
                cutTask.save(tempCutTaskPath);
                downloadTask = new File(tempCutTaskPath);
            }
        }
        return downloadTask;
    }

    // Сгенерировать отчёт по успеваемости всех студентов
    @Deprecated
    public File generateReportAboutAllActiveStudents(String studentKey, String token, Integer stagesKey) throws Exception {
        List<AssociatedStudentView> allActiveStudents;
        if (studentKey.equals("for my students")) {
            allActiveStudents = associatedStudentsService.getActiveStudents(token);
        } else {
            allActiveStudents = associatedStudentsService.getAllActiveStudents(studentKey);
        }
        if (stagesKey == 0) {
            XSSFWorkbook report = new XSSFWorkbook();
            XSSFSheet reportSheet = report.createSheet("Успеваемость студентов");
            // Настройка ширины колонок
            reportSheet.setColumnWidth(0, 5000);
            reportSheet.setColumnWidth(1, 3000);
            reportSheet.setColumnWidth(2, 5500);
            reportSheet.setColumnWidth(3, 5500);
            reportSheet.setColumnWidth(4, 5500);
            reportSheet.setColumnWidth(5, 5500);
            reportSheet.setColumnWidth(6, 5500);
            reportSheet.setColumnWidth(7, 5500);
            reportSheet.setColumnWidth(8, 5500);
            reportSheet.setColumnWidth(9, 5500);
            reportSheet.setColumnWidth(10, 5500);
            reportSheet.setColumnWidth(11, 5500);
            reportSheet.setColumnWidth(12, 5500);
            reportSheet.setColumnWidth(13, 5500);
            reportSheet.setColumnWidth(14, 5500);
            // Создание заголовка таблицы
            XSSFRow reportRow = reportSheet.createRow(0);
            // ФИО студента
            XSSFCell studentFioCell = reportRow.createCell(0);
            studentFioCell.setCellType(CellType.STRING);
            studentFioCell.setCellValue("Студент");
            // Группа
            XSSFCell studentGroupCell = reportRow.createCell(1);
            studentGroupCell.setCellType(CellType.STRING);
            studentGroupCell.setCellValue("Группа");
            // Научный руководитель
            XSSFCell advisorGroupCell = reportRow.createCell(2);
            advisorGroupCell.setCellType(CellType.STRING);
            advisorGroupCell.setCellValue("Научный руководитель");
            // Задание на НИР
            XSSFCell NirTaskGroupCell = reportRow.createCell(3);
            NirTaskGroupCell.setCellType(CellType.STRING);
            NirTaskGroupCell.setCellValue("Задание на НИР");
            // Отчёт по НИР
            XSSFCell NirReportGroupCell = reportRow.createCell(4);
            NirReportGroupCell.setCellType(CellType.STRING);
            NirReportGroupCell.setCellValue("Отчёт по НИР");
            // Задание на ПпППУиОПД
            XSSFCell PpppuiopdTaskGroupCell = reportRow.createCell(5);
            PpppuiopdTaskGroupCell.setCellType(CellType.STRING);
            PpppuiopdTaskGroupCell.setCellValue("Задание на ПпППУиОПД");
            // Отчёт по ПпППУиОПД
            XSSFCell PpppuiopdReportGroupCell = reportRow.createCell(6);
            PpppuiopdReportGroupCell.setCellType(CellType.STRING);
            PpppuiopdReportGroupCell.setCellValue("Отчёт по ПпППУиОПД");
            // Задание на ПП
            XSSFCell PpTaskGroupCell = reportRow.createCell(7);
            PpTaskGroupCell.setCellType(CellType.STRING);
            PpTaskGroupCell.setCellValue("Задание на ПП");
            // Отчёт по ПП
            XSSFCell PpReportGroupCell = reportRow.createCell(8);
            PpReportGroupCell.setCellType(CellType.STRING);
            PpReportGroupCell.setCellValue("Отчёт по ПП");
            // Презентация по ВКР
            XSSFCell PresentationGroupCell = reportRow.createCell(9);
            PresentationGroupCell.setCellType(CellType.STRING);
            PresentationGroupCell.setCellValue("Презентация по ВКР");
            // Допуск по ВКР
            XSSFCell AllowanceGroupCell = reportRow.createCell(10);
            AllowanceGroupCell.setCellType(CellType.STRING);
            AllowanceGroupCell.setCellValue("Допуск на защиту ВКР");
            // Отзыв руководителя
            XSSFCell FeedbackGroupCell = reportRow.createCell(11);
            FeedbackGroupCell.setCellType(CellType.STRING);
            FeedbackGroupCell.setCellValue("Отзыв руководителя на ВКР");
            // Антиплагиат
            XSSFCell antiplagiatGroupCell = reportRow.createCell(12);
            antiplagiatGroupCell.setCellType(CellType.STRING);
            antiplagiatGroupCell.setCellValue("Антиплагиат на ВКР");
            // Задание на ВКР
            XSSFCell VkrTaskGroupCell = reportRow.createCell(13);
            VkrTaskGroupCell.setCellType(CellType.STRING);
            VkrTaskGroupCell.setCellValue("Задание на ВКР");
            // РПЗ по ВКР
            XSSFCell VkrReportGroupCell = reportRow.createCell(14);
            VkrReportGroupCell.setCellType(CellType.STRING);
            VkrReportGroupCell.setCellValue("РПЗ по ВКР");

            int rowIndex = 1;
            // Заполнение данных в цикле
            for (AssociatedStudentView activeStudent: allActiveStudents) {
                // Создание текущей строки таблицы
                XSSFRow currentReportRow = reportSheet.createRow(rowIndex);

                // ФИО студента
                XSSFCell currentStudentFioCell = currentReportRow.createCell(0);
                currentStudentFioCell.setCellType(CellType.STRING);
                currentStudentFioCell.setCellValue(getShortFio(activeStudent.getFIO()));

                // Группа
                XSSFCell currentStudentGroupCell = currentReportRow.createCell(1);
                currentStudentGroupCell.setCellType(CellType.STRING);
                currentStudentGroupCell.setCellValue(activeStudent.getGroup());

                // Научный руководитель
                XSSFCell currentAdvisorGroupCell = currentReportRow.createCell(2);
                currentAdvisorGroupCell.setCellType(CellType.STRING);
                AssociatedStudents associatedStudent =
                        associatedStudentsRepository.findByStudent(activeStudent.getSystemStudentID());
                Users advisor = associatedStudent.getAdvisorUser();
                currentAdvisorGroupCell.setCellValue(getShortFio(advisor.getSurname() + " " + advisor.getName() +
                        " " + advisor.getSecond_name()));

                // Задание на НИР
                XSSFCell currentNirTaskGroupCell = currentReportRow.createCell(3);
                currentNirTaskGroupCell.setCellType(CellType.STRING);
                if (activeStudent.getStudentDocumentsStatusView().getNirTaskStatus() == 1) {
                    currentNirTaskGroupCell.setCellValue("Одобрено");
                } else {
                    currentNirTaskGroupCell.setCellValue("");
                }

                // Отчёт по НИР
                XSSFCell currentNirReportGroupCell = currentReportRow.createCell(4);
                currentNirReportGroupCell.setCellType(CellType.STRING);
                if (activeStudent.getStudentDocumentsStatusView().getNirReportStatus() == 2) {
                    currentNirReportGroupCell.setCellValue("Неудовлетворительно");
                } else if (activeStudent.getStudentDocumentsStatusView().getNirReportStatus() == 3) {
                    currentNirReportGroupCell.setCellValue("Удовлетворительно");
                } else if (activeStudent.getStudentDocumentsStatusView().getNirReportStatus() == 4) {
                    currentNirReportGroupCell.setCellValue("Хорошо");
                } else if (activeStudent.getStudentDocumentsStatusView().getNirReportStatus() == 5) {
                    currentNirReportGroupCell.setCellValue("Отлично");
                } else {
                    currentNirReportGroupCell.setCellValue("");
                }

                // Задание на ПпППУиОПД
                XSSFCell currentPpppuiopdTaskGroupCell = currentReportRow.createCell(5);
                currentPpppuiopdTaskGroupCell.setCellType(CellType.STRING);
                if (activeStudent.getStudentDocumentsStatusView().getPpppuipdTaskStatus() == 1) {
                    currentPpppuiopdTaskGroupCell.setCellValue("Одобрено");
                } else {
                    currentPpppuiopdTaskGroupCell.setCellValue("");
                }

                // Отчёт по ПпППУиОПД
                XSSFCell currentPpppuiopdReportGroupCell = currentReportRow.createCell(6);
                currentPpppuiopdReportGroupCell.setCellType(CellType.STRING);
                if (activeStudent.getStudentDocumentsStatusView().getPpppuipdReportStatus() == 2) {
                    currentPpppuiopdReportGroupCell.setCellValue("Неудовлетворительно");
                } else if (activeStudent.getStudentDocumentsStatusView().getPpppuipdReportStatus() == 3) {
                    currentPpppuiopdReportGroupCell.setCellValue("Удовлетворительно");
                } else if (activeStudent.getStudentDocumentsStatusView().getPpppuipdReportStatus() == 4) {
                    currentPpppuiopdReportGroupCell.setCellValue("Хорошо");
                } else if (activeStudent.getStudentDocumentsStatusView().getPpppuipdReportStatus() == 5) {
                    currentPpppuiopdReportGroupCell.setCellValue("Отлично");
                } else {
                    currentPpppuiopdReportGroupCell.setCellValue("");
                }

                // Задание на ПП
                XSSFCell currentPpTaskGroupCell = currentReportRow.createCell(7);
                currentPpTaskGroupCell.setCellType(CellType.STRING);
                if (activeStudent.getStudentDocumentsStatusView().getPpTaskStatus() == 1) {
                    currentPpTaskGroupCell.setCellValue("Одобрено");
                } else {
                    currentPpTaskGroupCell.setCellValue("");
                }

                // Отчёт по ПП
                XSSFCell currentPpReportGroupCell = currentReportRow.createCell(8);
                currentPpReportGroupCell.setCellType(CellType.STRING);
                if (activeStudent.getStudentDocumentsStatusView().getPpReportStatus() == 2) {
                    currentPpReportGroupCell.setCellValue("Неудовлетворительно");
                } else if (activeStudent.getStudentDocumentsStatusView().getPpReportStatus() == 3) {
                    currentPpReportGroupCell.setCellValue("Удовлетворительно");
                } else if (activeStudent.getStudentDocumentsStatusView().getPpReportStatus() == 4) {
                    currentPpReportGroupCell.setCellValue("Хорошо");
                } else if (activeStudent.getStudentDocumentsStatusView().getPpReportStatus() == 5) {
                    currentPpReportGroupCell.setCellValue("Отлично");
                } else {
                    currentPpReportGroupCell.setCellValue("");
                }

                // Презентация по ВКР
                XSSFCell currentPresentationGroupCell = currentReportRow.createCell(9);
                currentPresentationGroupCell.setCellType(CellType.STRING);
                if (activeStudent.getStudentDocumentsStatusView().getVkrPresentation() == 1) {
                    currentPresentationGroupCell.setCellValue("Одобрено");
                } else {
                    currentPresentationGroupCell.setCellValue("");
                }

                // Допуск по ВКР
                XSSFCell currentAllowanceGroupCell = currentReportRow.createCell(10);
                currentAllowanceGroupCell.setCellType(CellType.STRING);
                if (activeStudent.getStudentDocumentsStatusView().getVkrAllowance() == 1) {
                    currentAllowanceGroupCell.setCellValue("Одобрено");
                } else {
                    currentAllowanceGroupCell.setCellValue("");
                }

                // Отзыв руководителя
                XSSFCell currentFeedbackGroupCell = currentReportRow.createCell(11);
                currentFeedbackGroupCell.setCellType(CellType.STRING);
                if (activeStudent.getStudentDocumentsStatusView().getVkrAdvisorFeedback() == 1) {
                    currentFeedbackGroupCell.setCellValue("Одобрено");
                } else {
                    currentFeedbackGroupCell.setCellValue("");
                }

                // Антиплагиат
                XSSFCell currentAntiplagiatGroupCell = currentReportRow.createCell(12);
                currentAntiplagiatGroupCell.setCellType(CellType.STRING);
                if (activeStudent.getStudentDocumentsStatusView().getVkrAntiplagiat() == 1) {
                    currentAntiplagiatGroupCell.setCellValue("Одобрено");
                } else {
                    currentAntiplagiatGroupCell.setCellValue("");
                }

                // Задание на ВКР
                XSSFCell currentVkrTaskGroupCell = currentReportRow.createCell(13);
                currentVkrTaskGroupCell.setCellType(CellType.STRING);
                if (activeStudent.getStudentDocumentsStatusView().getVkrTask() == 1) {
                    currentVkrTaskGroupCell.setCellValue("Одобрено");
                } else {
                    currentVkrTaskGroupCell.setCellValue("");
                }

                // РПЗ по ВКР
                XSSFCell currentVkrReportGroupCell = currentReportRow.createCell(14);
                currentVkrReportGroupCell.setCellType(CellType.STRING);
                if (activeStudent.getStudentDocumentsStatusView().getVkrRPZ() == 2) {
                    currentVkrReportGroupCell.setCellValue("Неудовлетворительно");
                } else if (activeStudent.getStudentDocumentsStatusView().getVkrRPZ() == 3) {
                    currentVkrReportGroupCell.setCellValue("Удовлетворительно");
                } else if (activeStudent.getStudentDocumentsStatusView().getVkrRPZ() == 4) {
                    currentVkrReportGroupCell.setCellValue("Хорошо");
                } else if (activeStudent.getStudentDocumentsStatusView().getVkrRPZ() == 5) {
                    currentVkrReportGroupCell.setCellValue("Отлично");
                } else {
                    currentVkrReportGroupCell.setCellValue("");
                }
                rowIndex++;
            }
            File file = new File(storageLocation + File.separator
                    + "temp" + File.separator + "temp_report_" + Instant.now().toString()
                    .replace(':', ' ').replace('.', ' ') + ".xlsx");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            report.write(fileOutputStream);
            return file;
        } else {
            return generateReportByStage(allActiveStudents, stagesKey);
        }
    }

    @Deprecated
    public File generateReportByStage(List<AssociatedStudentView> allActiveStudents, Integer stagesKey) throws IOException {
        XSSFWorkbook report = new XSSFWorkbook();
        File file;
        int rowIndex = 1;
        FileOutputStream fileOutputStream;
        XSSFSheet reportSheet = report.createSheet("Успеваемость студентов");
        // Настройка ширины колонок
        reportSheet.setColumnWidth(0, 5000);
        reportSheet.setColumnWidth(1, 3000);
        reportSheet.setColumnWidth(2, 5500);
        reportSheet.setColumnWidth(3, 5500);
        reportSheet.setColumnWidth(4, 5500);
        // Создание заголовка таблицы
        XSSFRow reportRow = reportSheet.createRow(0);
        // ФИО студента
        XSSFCell studentFioCell = reportRow.createCell(0);
        studentFioCell.setCellType(CellType.STRING);
        studentFioCell.setCellValue("Студент");
        // Группа
        XSSFCell studentGroupCell = reportRow.createCell(1);
        studentGroupCell.setCellType(CellType.STRING);
        studentGroupCell.setCellValue("Группа");
        // Научный руководитель
        XSSFCell advisorGroupCell = reportRow.createCell(2);
        advisorGroupCell.setCellType(CellType.STRING);
        advisorGroupCell.setCellValue("Научный руководитель");
        switch (stagesKey) {
            case 1:
                // Задание на НИР
                XSSFCell NirTaskGroupCell = reportRow.createCell(3);
                NirTaskGroupCell.setCellType(CellType.STRING);
                NirTaskGroupCell.setCellValue("Задание на НИР");
                // Отчёт по НИР
                XSSFCell NirReportGroupCell = reportRow.createCell(4);
                NirReportGroupCell.setCellType(CellType.STRING);
                NirReportGroupCell.setCellValue("Отчёт по НИР");
                rowIndex = 1;
                // Заполнение данных в цикле
                for (AssociatedStudentView activeStudent: allActiveStudents) {
                    // Создание текущей строки таблицы
                    XSSFRow currentReportRow = reportSheet.createRow(rowIndex);

                    // ФИО студента
                    XSSFCell currentStudentFioCell = currentReportRow.createCell(0);
                    currentStudentFioCell.setCellType(CellType.STRING);
                    currentStudentFioCell.setCellValue(getShortFio(activeStudent.getFIO()));

                    // Группа
                    XSSFCell currentStudentGroupCell = currentReportRow.createCell(1);
                    currentStudentGroupCell.setCellType(CellType.STRING);
                    currentStudentGroupCell.setCellValue(activeStudent.getGroup());

                    // Научный руководитель
                    XSSFCell currentAdvisorGroupCell = currentReportRow.createCell(2);
                    currentAdvisorGroupCell.setCellType(CellType.STRING);
                    AssociatedStudents associatedStudent =
                            associatedStudentsRepository.findByStudent(activeStudent.getSystemStudentID());
                    Users advisor = associatedStudent.getAdvisorUser();
                    currentAdvisorGroupCell.setCellValue(getShortFio(advisor.getSurname() + " " + advisor.getName() +
                            " " + advisor.getSecond_name()));

                    // Задание на НИР
                    XSSFCell currentNirTaskGroupCell = currentReportRow.createCell(3);
                    currentNirTaskGroupCell.setCellType(CellType.STRING);
                    if (activeStudent.getStudentDocumentsStatusView().getNirTaskStatus() == 1) {
                        currentNirTaskGroupCell.setCellValue("Одобрено");
                    } else {
                        currentNirTaskGroupCell.setCellValue("");
                    }

                    // Отчёт по НИР
                    XSSFCell currentNirReportGroupCell = currentReportRow.createCell(4);
                    currentNirReportGroupCell.setCellType(CellType.STRING);
                    if (activeStudent.getStudentDocumentsStatusView().getNirReportStatus() == 2) {
                        currentNirReportGroupCell.setCellValue("Неудовлетворительно");
                    } else if (activeStudent.getStudentDocumentsStatusView().getNirReportStatus() == 3) {
                        currentNirReportGroupCell.setCellValue("Удовлетворительно");
                    } else if (activeStudent.getStudentDocumentsStatusView().getNirReportStatus() == 4) {
                        currentNirReportGroupCell.setCellValue("Хорошо");
                    } else if (activeStudent.getStudentDocumentsStatusView().getNirReportStatus() == 5) {
                        currentNirReportGroupCell.setCellValue("Отлично");
                    } else {
                        currentNirReportGroupCell.setCellValue("");
                    }
                    rowIndex++;
                }
                file = new File(storageLocation + File.separator
                        + "temp" + File.separator + "temp_report_" + Instant.now().toString()
                        .replace(':', ' ').replace('.', ' ') + ".xlsx");
                fileOutputStream = new FileOutputStream(file);
                report.write(fileOutputStream);
                return file;
            case 2:
                // Задание на ПпППУиОПД
                XSSFCell PpppuiopdTaskGroupCell = reportRow.createCell(3);
                PpppuiopdTaskGroupCell.setCellType(CellType.STRING);
                PpppuiopdTaskGroupCell.setCellValue("Задание на ПпППУиОПД");
                // Отчёт по ПпППУиОПД
                XSSFCell PpppuiopdReportGroupCell = reportRow.createCell(4);
                PpppuiopdReportGroupCell.setCellType(CellType.STRING);
                PpppuiopdReportGroupCell.setCellValue("Отчёт по ПпППУиОПД");
                rowIndex = 1;
                // Заполнение данных в цикле
                for (AssociatedStudentView activeStudent: allActiveStudents) {
                    // Создание текущей строки таблицы
                    XSSFRow currentReportRow = reportSheet.createRow(rowIndex);

                    // ФИО студента
                    XSSFCell currentStudentFioCell = currentReportRow.createCell(0);
                    currentStudentFioCell.setCellType(CellType.STRING);
                    currentStudentFioCell.setCellValue(getShortFio(activeStudent.getFIO()));

                    // Группа
                    XSSFCell currentStudentGroupCell = currentReportRow.createCell(1);
                    currentStudentGroupCell.setCellType(CellType.STRING);
                    currentStudentGroupCell.setCellValue(activeStudent.getGroup());

                    // Научный руководитель
                    XSSFCell currentAdvisorGroupCell = currentReportRow.createCell(2);
                    currentAdvisorGroupCell.setCellType(CellType.STRING);
                    AssociatedStudents associatedStudent =
                            associatedStudentsRepository.findByStudent(activeStudent.getSystemStudentID());
                    Users advisor = associatedStudent.getAdvisorUser();
                    currentAdvisorGroupCell.setCellValue(getShortFio(advisor.getSurname() + " " + advisor.getName() +
                            " " + advisor.getSecond_name()));

                    // Задание на ПпППУиОПД
                    XSSFCell currentPpppuiopdTaskGroupCell = currentReportRow.createCell(3);
                    currentPpppuiopdTaskGroupCell.setCellType(CellType.STRING);
                    if (activeStudent.getStudentDocumentsStatusView().getPpppuipdTaskStatus() == 1) {
                        currentPpppuiopdTaskGroupCell.setCellValue("Одобрено");
                    } else {
                        currentPpppuiopdTaskGroupCell.setCellValue("");
                    }

                    // Отчёт по ПпППУиОПД
                    XSSFCell currentPpppuiopdReportGroupCell = currentReportRow.createCell(4);
                    currentPpppuiopdReportGroupCell.setCellType(CellType.STRING);
                    if (activeStudent.getStudentDocumentsStatusView().getPpppuipdReportStatus() == 2) {
                        currentPpppuiopdReportGroupCell.setCellValue("Неудовлетворительно");
                    } else if (activeStudent.getStudentDocumentsStatusView().getPpppuipdReportStatus() == 3) {
                        currentPpppuiopdReportGroupCell.setCellValue("Удовлетворительно");
                    } else if (activeStudent.getStudentDocumentsStatusView().getPpppuipdReportStatus() == 4) {
                        currentPpppuiopdReportGroupCell.setCellValue("Хорошо");
                    } else if (activeStudent.getStudentDocumentsStatusView().getPpppuipdReportStatus() == 5) {
                        currentPpppuiopdReportGroupCell.setCellValue("Отлично");
                    } else {
                        currentPpppuiopdReportGroupCell.setCellValue("");
                    }
                    rowIndex++;
                }
                file = new File(storageLocation + File.separator
                        + "temp" + File.separator + "temp_report_" + Instant.now().toString()
                        .replace(':', ' ').replace('.', ' ') + ".xlsx");
                fileOutputStream = new FileOutputStream(file);
                report.write(fileOutputStream);
                return file;
            case 3:
                // Задание на ПП
                XSSFCell PpTaskGroupCell = reportRow.createCell(3);
                PpTaskGroupCell.setCellType(CellType.STRING);
                PpTaskGroupCell.setCellValue("Задание на ПП");
                // Отчёт по ПП
                XSSFCell PpReportGroupCell = reportRow.createCell(4);
                PpReportGroupCell.setCellType(CellType.STRING);
                PpReportGroupCell.setCellValue("Отчёт по ПП");
                rowIndex = 1;
                // Заполнение данных в цикле
                for (AssociatedStudentView activeStudent: allActiveStudents) {
                    // Создание текущей строки таблицы
                    XSSFRow currentReportRow = reportSheet.createRow(rowIndex);

                    // ФИО студента
                    XSSFCell currentStudentFioCell = currentReportRow.createCell(0);
                    currentStudentFioCell.setCellType(CellType.STRING);
                    currentStudentFioCell.setCellValue(getShortFio(activeStudent.getFIO()));

                    // Группа
                    XSSFCell currentStudentGroupCell = currentReportRow.createCell(1);
                    currentStudentGroupCell.setCellType(CellType.STRING);
                    currentStudentGroupCell.setCellValue(activeStudent.getGroup());

                    // Научный руководитель
                    XSSFCell currentAdvisorGroupCell = currentReportRow.createCell(2);
                    currentAdvisorGroupCell.setCellType(CellType.STRING);
                    AssociatedStudents associatedStudent =
                            associatedStudentsRepository.findByStudent(activeStudent.getSystemStudentID());
                    Users advisor = associatedStudent.getAdvisorUser();
                    currentAdvisorGroupCell.setCellValue(getShortFio(advisor.getSurname() + " " + advisor.getName() +
                            " " + advisor.getSecond_name()));

                    // Задание на ПП
                    XSSFCell currentPpTaskGroupCell = currentReportRow.createCell(3);
                    currentPpTaskGroupCell.setCellType(CellType.STRING);
                    if (activeStudent.getStudentDocumentsStatusView().getPpTaskStatus() == 1) {
                        currentPpTaskGroupCell.setCellValue("Одобрено");
                    } else {
                        currentPpTaskGroupCell.setCellValue("");
                    }

                    // Отчёт по ПП
                    XSSFCell currentPpReportGroupCell = currentReportRow.createCell(4);
                    currentPpReportGroupCell.setCellType(CellType.STRING);
                    if (activeStudent.getStudentDocumentsStatusView().getPpReportStatus() == 2) {
                        currentPpReportGroupCell.setCellValue("Неудовлетворительно");
                    } else if (activeStudent.getStudentDocumentsStatusView().getPpReportStatus() == 3) {
                        currentPpReportGroupCell.setCellValue("Удовлетворительно");
                    } else if (activeStudent.getStudentDocumentsStatusView().getPpReportStatus() == 4) {
                        currentPpReportGroupCell.setCellValue("Хорошо");
                    } else if (activeStudent.getStudentDocumentsStatusView().getPpReportStatus() == 5) {
                        currentPpReportGroupCell.setCellValue("Отлично");
                    } else {
                        currentPpReportGroupCell.setCellValue("");
                    }
                    rowIndex++;
                }
                file = new File(storageLocation + File.separator
                        + "temp" + File.separator + "temp_report_" + Instant.now().toString()
                        .replace(':', ' ').replace('.', ' ') + ".xlsx");
                fileOutputStream = new FileOutputStream(file);
                report.write(fileOutputStream);
                return file;
            case 4:
                reportSheet.setColumnWidth(5, 5500);
                reportSheet.setColumnWidth(6, 5500);
                reportSheet.setColumnWidth(7, 5500);
                reportSheet.setColumnWidth(8, 5500);
                // Презентация по ВКР
                XSSFCell PresentationGroupCell = reportRow.createCell(3);
                PresentationGroupCell.setCellType(CellType.STRING);
                PresentationGroupCell.setCellValue("Презентация по ВКР");
                // Допуск по ВКР
                XSSFCell AllowanceGroupCell = reportRow.createCell(4);
                AllowanceGroupCell.setCellType(CellType.STRING);
                AllowanceGroupCell.setCellValue("Допуск на защиту ВКР");
                // Отзыв руководителя
                XSSFCell FeedbackGroupCell = reportRow.createCell(5);
                FeedbackGroupCell.setCellType(CellType.STRING);
                FeedbackGroupCell.setCellValue("Отзыв руководителя на ВКР");
                // Антиплагиат
                XSSFCell antiplagiatGroupCell = reportRow.createCell(6);
                antiplagiatGroupCell.setCellType(CellType.STRING);
                antiplagiatGroupCell.setCellValue("Антиплагиат на ВКР");
                // Задание на ВКР
                XSSFCell VkrTaskGroupCell = reportRow.createCell(7);
                VkrTaskGroupCell.setCellType(CellType.STRING);
                VkrTaskGroupCell.setCellValue("Задание на ВКР");
                // РПЗ по ВКР
                XSSFCell VkrReportGroupCell = reportRow.createCell(8);
                VkrReportGroupCell.setCellType(CellType.STRING);
                VkrReportGroupCell.setCellValue("РПЗ по ВКР");
                rowIndex = 1;
                // Заполнение данных в цикле
                for (AssociatedStudentView activeStudent: allActiveStudents) {
                    // Создание текущей строки таблицы
                    XSSFRow currentReportRow = reportSheet.createRow(rowIndex);

                    // ФИО студента
                    XSSFCell currentStudentFioCell = currentReportRow.createCell(0);
                    currentStudentFioCell.setCellType(CellType.STRING);
                    currentStudentFioCell.setCellValue(getShortFio(activeStudent.getFIO()));

                    // Группа
                    XSSFCell currentStudentGroupCell = currentReportRow.createCell(1);
                    currentStudentGroupCell.setCellType(CellType.STRING);
                    currentStudentGroupCell.setCellValue(activeStudent.getGroup());

                    // Научный руководитель
                    XSSFCell currentAdvisorGroupCell = currentReportRow.createCell(2);
                    currentAdvisorGroupCell.setCellType(CellType.STRING);
                    AssociatedStudents associatedStudent =
                            associatedStudentsRepository.findByStudent(activeStudent.getSystemStudentID());
                    Users advisor = associatedStudent.getAdvisorUser();
                    currentAdvisorGroupCell.setCellValue(getShortFio(advisor.getSurname() + " " + advisor.getName() +
                            " " + advisor.getSecond_name()));

                    // Презентация по ВКР
                    XSSFCell currentPresentationGroupCell = currentReportRow.createCell(3);
                    currentPresentationGroupCell.setCellType(CellType.STRING);
                    if (activeStudent.getStudentDocumentsStatusView().getVkrPresentation() == 1) {
                        currentPresentationGroupCell.setCellValue("Одобрено");
                    } else {
                        currentPresentationGroupCell.setCellValue("");
                    }

                    // Допуск по ВКР
                    XSSFCell currentAllowanceGroupCell = currentReportRow.createCell(4);
                    currentAllowanceGroupCell.setCellType(CellType.STRING);
                    if (activeStudent.getStudentDocumentsStatusView().getVkrAllowance() == 1) {
                        currentAllowanceGroupCell.setCellValue("Одобрено");
                    } else {
                        currentAllowanceGroupCell.setCellValue("");
                    }

                    // Отзыв руководителя
                    XSSFCell currentFeedbackGroupCell = currentReportRow.createCell(5);
                    currentFeedbackGroupCell.setCellType(CellType.STRING);
                    if (activeStudent.getStudentDocumentsStatusView().getVkrAdvisorFeedback() == 1) {
                        currentFeedbackGroupCell.setCellValue("Одобрено");
                    } else {
                        currentFeedbackGroupCell.setCellValue("");
                    }

                    // Антиплагиат
                    XSSFCell currentAntiplagiatGroupCell = currentReportRow.createCell(6);
                    currentAntiplagiatGroupCell.setCellType(CellType.STRING);
                    if (activeStudent.getStudentDocumentsStatusView().getVkrAntiplagiat() == 1) {
                        currentAntiplagiatGroupCell.setCellValue("Одобрено");
                    } else {
                        currentAntiplagiatGroupCell.setCellValue("");
                    }

                    // Задание на ВКР
                    XSSFCell currentVkrTaskGroupCell = currentReportRow.createCell(7);
                    currentVkrTaskGroupCell.setCellType(CellType.STRING);
                    if (activeStudent.getStudentDocumentsStatusView().getVkrTask() == 1) {
                        currentVkrTaskGroupCell.setCellValue("Одобрено");
                    } else {
                        currentVkrTaskGroupCell.setCellValue("");
                    }

                    // РПЗ по ВКР
                    XSSFCell currentVkrReportGroupCell = currentReportRow.createCell(8);
                    currentVkrReportGroupCell.setCellType(CellType.STRING);
                    if (activeStudent.getStudentDocumentsStatusView().getVkrRPZ() == 2) {
                        currentVkrReportGroupCell.setCellValue("Неудовлетворительно");
                    } else if (activeStudent.getStudentDocumentsStatusView().getVkrRPZ() == 3) {
                        currentVkrReportGroupCell.setCellValue("Удовлетворительно");
                    } else if (activeStudent.getStudentDocumentsStatusView().getVkrRPZ() == 4) {
                        currentVkrReportGroupCell.setCellValue("Хорошо");
                    } else if (activeStudent.getStudentDocumentsStatusView().getVkrRPZ() == 5) {
                        currentVkrReportGroupCell.setCellValue("Отлично");
                    } else {
                        currentVkrReportGroupCell.setCellValue("");
                    }
                    rowIndex++;
                }
                file = new File(storageLocation + File.separator
                        + "temp" + File.separator + "temp_report_" + Instant.now().toString()
                        .replace(':', ' ').replace('.', ' ') + ".xlsx");
                fileOutputStream = new FileOutputStream(file);
                report.write(fileOutputStream);
                return file;
            default:
                return null;
        }
    }

    // Сгенерировать отчёт по успеваемости на основе динамической формы
    public File generateReportFromDynamicForm(AcademicRecordDynamicForm dynamicForm) throws Exception {
        XSSFWorkbook dynamicReport = new XSSFWorkbook();
        XSSFSheet reportSheet = dynamicReport.createSheet("Успеваемость студентов");
        // Создадим столбец заголовка
        XSSFRow headersRow = reportSheet.createRow(0);
        // Выставим длину столбцов и установим их заголовки
        for (int i = 0; i < dynamicForm.getColumnsHeaders().size(); i++) {
            reportSheet.setColumnWidth(i, 5500);
            XSSFCell currentCell = headersRow.createCell(i);
            currentCell.setCellType(CellType.STRING);
            currentCell.setCellValue(dynamicForm.getColumnsHeaders().get(i));
        }
        // Заполним таблицу данными
        for (int i = 0; i < dynamicForm.getRowsContent().size(); i++) {
            XSSFRow currentContentRow = reportSheet.createRow(i + 1); // Надо учитывать смещение числа строк на 1 из-за заголовка
            // Запишем в цикле строку данных
            for (int j = 0; j < dynamicForm.getRowsContent().get(i).size(); j++) {
                XSSFCell currentContentCell = currentContentRow.createCell(j);
                String currentData = dynamicForm.getRowsContent().get(i).get(j);
                switch (currentData) {
                    case "0":
                        currentData = "";
                        break;
                    case "1":
                        currentData = "Одобрено";
                        break;
                    case "2":
                        currentData = "Неудовлетворительно";
                        break;
                    case "3":
                        currentData = "Удовлетворительно";
                        break;
                    case "4":
                        currentData = "Хорошо";
                        break;
                    case "5":
                        currentData = "Отлично";
                        break;
                    default:
                        break;
                }
                currentContentCell.setCellType(CellType.STRING);
                currentContentCell.setCellValue(currentData);
            }
        }
        File file = new File(storageLocation + File.separator
                + "temp" + File.separator + "temp_dynamic_report_" + Instant.now().toString()
                .replace(':', ' ').replace('.', ' ') + ".xlsx");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        dynamicReport.write(fileOutputStream);
        return file;
    }

    // Преобразование даты вида ДД.ММ.ГГГГ к виду «XX» месяца YYYY
    public String getFirstDateType(String russianDate) {
        String day = russianDate.substring(0, 2);
        String month = russianDate.substring(3, 5);
        String year = russianDate.substring(6, 10);
        String monthWord = getMonthWord(month);
        String date = "«" + day + "»" + " " + monthWord + " " + year;
        return date;
    }

    // Преобразование даты вида ДД.ММ.ГГГГ к виду XX месяца YYYY
    public String getSecondDateType(String russianDate) {
        String day = russianDate.substring(0, 2);
        String month = russianDate.substring(3, 5);
        String year = russianDate.substring(6, 10);
        String monthWord = getMonthWord(month);
        String date = day + " " + monthWord + " " + year;
        return date;
    }

    // Преобразование ФИО к укороченному варианту
    public String getShortFio(String Fio) {
        String[] words = Fio.split(" ");
        String shortFio = words[0] + " " + words[1].substring(0, 1) + "." + words[2].substring(0, 1) + ".";
        return shortFio;
    }

    // Преобразование ФИО к дательному падежу
    public String getDpFio(String Surname, String Name, String Second_name) throws Exception {
        Petrovich petrovich = new Petrovich();
        Petrovich.Names names = new Petrovich.Names(Surname, Name, Second_name, Gender.detect(Second_name));
        Petrovich.Names complete = petrovich.inflectTo(names, Case.DATIVE);
        return complete.lastName + " " + complete.firstName + " " + complete.middleName;
    }

    // Преобразование ФИО к родительному падежу
    public String getRpFio(String Surname, String Name, String Second_name) throws Exception {
        Petrovich petrovich = new Petrovich();
        Petrovich.Names names = new Petrovich.Names(Surname, Name, Second_name, Gender.detect(Second_name));
        Petrovich.Names complete = petrovich.inflectTo(names, Case.GENITIVE);
        return complete.lastName + " " + complete.firstName + " " + complete.middleName;
    }

    // Повысить регистр первой буквы допзадания в таблице
    public String toUpperCaseFirstSymbol(String additionalTask) {
        String firstSymbol = additionalTask.substring(0, 1).toUpperCase();
        additionalTask = firstSymbol + additionalTask.substring(1);
        return additionalTask;
    }

    // Повысить регистр первой буквы допзадания в таблице
    public String toLowerCaseFirstSymbol(String additionalTask) {
        String firstSymbol = additionalTask.substring(0, 1).toLowerCase();
        additionalTask = firstSymbol + additionalTask.substring(1);
        return additionalTask;
    }

    // Получить слово месяца
    public String getMonthWord(String month) {
        // Определим месяц
        String monthWord = "";
        switch (month) {
            case "01":
                monthWord = "января";
                return monthWord;
            case "02":
                monthWord = "февраля";
                return monthWord;
            case "03":
                monthWord = "марта";
                return monthWord;
            case "04":
                monthWord = "апреля";
                return monthWord;
            case "05":
                monthWord = "мая";
                return monthWord;
            case "06":
                monthWord = "июня";
                return monthWord;
            case "07":
                monthWord = "июля";
                return monthWord;
            case "08":
                monthWord = "августа";
                return monthWord;
            case "09":
                monthWord = "сентября";
                return monthWord;
            case "10":
                monthWord = "октября";
                return monthWord;
            case "11":
                monthWord = "ноября";
                return monthWord;
            case "12":
                monthWord = "декабря";
                return monthWord;
            default:
                monthWord = "ошибка";
                return monthWord;
        }
    }

    // Откроем документ
    public XWPFDocument openDocument(String file) throws Exception {
        XWPFDocument document = null;
        InputStream is = new FileInputStream(file);
        document = new XWPFDocument(is);
        return document;
    }

    public Integer determineType(String stringType) {
        Integer type;
        switch (stringType) {
            case "Научно-исследовательская работа":
                type = 1;
                break;
            case "Практика по получению знаний и умений":
                type = 2;
                break;
            case "Преддипломная практика":
                type = 3;
                break;
            case "ВКР":
                type = 4;
                break;
            default:
                type = 0;
        }
        return type;
    }

    public Integer determineKind(String stringKind) {
        Integer type;
        switch (stringKind) {
            case "Допуск":
                type = 6;
                break;
            case "Отзыв":
                type = 7;
                break;
            case "Антиплагиат":
                type = 8;
                break;
            case "Презентация":
                type = 9;
                break;
            default:
                type = 0;
        }
        return type;
    }

    // Красиво отобразить дату загрузки новой версии документа
    public String getRussianDateTime(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        String russianDate = day + "." + month + "." + year;
        String russianDateTime = russianDate + date.substring(10);
        return russianDateTime;
    }

    public Integer getUserId(String token) {
        String email = jwtProvider.getEmailFromToken(token);
        Users user = usersRepository.findByEmail(email);
        if (user != null) {
            return user.getId();
        }
        else {
            return null;
        }
    }
}