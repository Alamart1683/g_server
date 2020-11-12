package g_server.g_server.application.service.documents;

import com.github.aleksandy.petrovich.Case;
import com.github.aleksandy.petrovich.Gender;
import com.github.aleksandy.petrovich.Petrovich;
import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.entity.documents.NirTask;
import g_server.g_server.application.entity.documents.OrderProperties;
import g_server.g_server.application.entity.system_data.Speciality;
import g_server.g_server.application.entity.users.AssociatedStudents;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.entity.view.AdvisorShortTaskDataView;
import g_server.g_server.application.entity.view.ShortTaskDataView;
import g_server.g_server.application.entity.view.TaskDataView;
import g_server.g_server.application.repository.documents.DocumentRepository;
import g_server.g_server.application.repository.documents.DocumentVersionRepository;
import g_server.g_server.application.repository.documents.NirTaskRepository;
import g_server.g_server.application.repository.documents.OrderPropertiesRepository;
import g_server.g_server.application.repository.system_data.SpecialityRepository;
import g_server.g_server.application.repository.users.AssociatedStudentsRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import g_server.g_server.application.repository.users.UsersRolesRepository;
import g_server.g_server.application.service.users.AssociatedStudentsService;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.XmlOptions;
import org.docx4j.dml.CTBlip;
import org.docx4j.model.styles.StyleTree;
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

    // Сгенерировать шаблон задания или создать его версию для студента
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
                                advisor,headOfCathedra, documentOrder, orderProperty, speciality);
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
                        String response = saveNirTaskAsDocument(fileName, student, advisor, taskDirPath,
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
                try {
                    orderProperty = orderPropertiesRepository.findBySpeciality(speciality.getId());
                } catch (NullPointerException nullPointerException) {
                    orderProperty = null;
                }
                if (orderProperty != null) {
                    Document document = documentRepository.findById(orderProperty.getId()).get();
                    Users headOfCathedra = usersRepository.findById(
                            usersRolesRepository.findByRoleId(3).getUserId()).get();
                    Integer type = determineType(shortTaskDataView.getTaskType());
                    Integer kind = 2;
                    List<Document> taskList = documentRepository.findByTypeAndKindAndCreator(
                            type, kind, headOfCathedra.getId());
                    if (taskList.size() > 0) {
                        TaskDataView taskDataView = fillingTaskDataView(shortTaskDataView, student,
                                advisor,headOfCathedra, document, orderProperty, speciality);
                        String studentDocumentsPath = "src" + File.separator + "main" +
                                File.separator + "resources" + File.separator + "users_documents" +
                                File.separator + student.getId();
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
                        String response = saveNirTaskAsDocument(fileName, student, advisor, taskDirPath,
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
            shortTaskDataView.setStudentTheme("Введите тему НИР");
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

    // Обработать шаблон
    public void taskProcessing(XWPFDocument template, TaskDataView taskDataView, String speciality, Users student)
            throws Exception {
        WordReplaceService wordReplaceService = new WordReplaceService(template);
        String studentTheme = "«" + taskDataView.getStudentTheme() + "»";
        // Заменим слова в тексте документа
        wordReplaceService.replaceWordsInText("Короткая дата начала НИР", taskDataView.getOrderStartDate());
        wordReplaceService.replaceWordsInText("Короткая дата конца НИР", taskDataView.getOrderEndDate());
        wordReplaceService.replaceWordsInText("Дата начала НИР без кавычек", getSecondDateType(taskDataView.getOrderStartDate()));
        wordReplaceService.replaceWordsInText("Дата конца НИР без кавычек", getSecondDateType(taskDataView.getOrderEndDate()));
        wordReplaceService.replaceWordsInText("Согласованное название темы", studentTheme);
        wordReplaceService.replaceWordsInText("Дата выхода приказа", getFirstDateType(taskDataView.getOrderDate()));
        wordReplaceService.replaceWordsInText("Дата начала НИР", getFirstDateType(taskDataView.getOrderStartDate()));
        wordReplaceService.replaceWordsInText("Дата конца НИР", getFirstDateType(taskDataView.getOrderEndDate()));
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
        wordReplaceService.replaceWordsInText("ИЗУЧИТЬ", toLowerCaseFirstSymbol(taskDataView.getToExplore()));
        wordReplaceService.replaceWordsInText("СОЗДАТЬ", toLowerCaseFirstSymbol(taskDataView.getToCreate()));
        wordReplaceService.replaceWordsInText("ОЗНАКОМИТЬСЯ", toLowerCaseFirstSymbol(taskDataView.getToFamiliarize()));
        wordReplaceService.replaceWordsInText("ДОПЗАДАНИЕ", toLowerCaseFirstSymbol(taskDataView.getAdditionalTask()));
        wordReplaceService.replaceWordsInText("ГОД", taskDataView.getOrderStartDate().substring(6, 10));
        // Заменим слова в таблицах документа
        wordReplaceService.replaceWordsInTables("Короткая дата начала НИР", taskDataView.getOrderStartDate());
        wordReplaceService.replaceWordsInTables("Короткая дата конца НИР", taskDataView.getOrderEndDate());
        wordReplaceService.replaceWordsInTables("Дата начала НИР без кавычек", getSecondDateType(taskDataView.getOrderStartDate()));
        wordReplaceService.replaceWordsInTables("Дата конца НИР без кавычек", getSecondDateType(taskDataView.getOrderEndDate()));
        wordReplaceService.replaceWordsInTables("Согласованное название темы", studentTheme);
        wordReplaceService.replaceWordsInTables("Дата выхода приказа", getFirstDateType(taskDataView.getOrderDate()));
        wordReplaceService.replaceWordsInTables("Дата начала НИР", getFirstDateType(taskDataView.getOrderStartDate()));
        wordReplaceService.replaceWordsInTables("Дата конца НИР", getFirstDateType(taskDataView.getOrderEndDate()));
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

    public void reportProcessing(File templateFile, String detailedContent, String advisorsConclusion) throws Exception {
        InputStream inputStream = new FileInputStream(templateFile);
        XWPFDocument template = new XWPFDocument(inputStream);
        WordReplaceService wordReplaceService = new WordReplaceService(template);
        wordReplaceService.replaceWordsInText("СОДЕРЖАНИЕ", detailedContent);
        wordReplaceService.replaceWordsInText("ЗАКЛЮЧЕНИЕ", advisorsConclusion);
        inputStream.close();
        wordReplaceService.saveAndGetModdedFile(templateFile);
    }

    // Сохранить задание на НИР как документ
    public String saveNirTaskAsDocument(String filename, Users student, Users advisor, String studentDocumentsPath,
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
            NirTask nirTask = new NirTask(
                    documentVersion.getId(), taskDataView.getStudentTheme(), taskDataView.getToExplore(),
                    taskDataView.getToCreate(), taskDataView.getToFamiliarize(), taskDataView.getAdditionalTask(), 1
            );
            nirTaskRepository.save(nirTask);
            return "Задание по " + taskDataView.getTaskType() + " успешно сгенерировано!";
        } else if (document != null && !flag) {
            if (document.getCreator() == student.getId()) {
                DocumentVersion documentVersion = new DocumentVersion(
                        student.getId(),
                        document.getId(),
                        documentUploadService.convertRussianDateToSqlDateTime(documentUploadService.getCurrentDate()),
                        "Добавление новой версии задания по " + taskDataView.getTaskType(),
                        documentVersionPath
                );
                documentVersionRepository.save(documentVersion);
                NirTask nirTask = new NirTask(
                        documentVersion.getId(), taskDataView.getStudentTheme(), taskDataView.getToExplore(),
                        taskDataView.getToCreate(), taskDataView.getToFamiliarize(), taskDataView.getAdditionalTask(), 1
                );
                nirTaskRepository.save(nirTask);
                return "Версия задания по " + taskDataView.getTaskType() + " успешно добавлена!";
            } else {
                return "Попытка создать версию чужого документа";
            }
        } else if (document != null && flag) {
            DocumentVersion documentVersion = new DocumentVersion(
                    advisor.getId(),
                    document.getId(),
                    documentUploadService.convertRussianDateToSqlDateTime(documentUploadService.getCurrentDate()),
                    "Добавление новой версии задания по " + taskDataView.getTaskType() + " научным руководителем "
                            + getShortFio(advisor.getSurname() + " " + advisor.getName() + " " + advisor.getSecond_name()),
                    documentVersionPath
            );
            documentVersionRepository.save(documentVersion);
            NirTask nirTask = new NirTask(
                    documentVersion.getId(), taskDataView.getStudentTheme(), taskDataView.getToExplore(),
                    taskDataView.getToCreate(), taskDataView.getToFamiliarize(), taskDataView.getAdditionalTask(), 1
            );
            nirTaskRepository.save(nirTask);
            return "Версия задания по " + taskDataView.getTaskType() + " успешно добавлена!";
        }
        return "Извините, что-то пошло не так";
    }

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