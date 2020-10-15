package g_server.g_server.application.service.documents;

import com.github.aleksandy.petrovich.Case;
import com.github.aleksandy.petrovich.Gender;
import com.github.aleksandy.petrovich.Petrovich;
import com.github.aleksandy.petrovich.rules.RulesProvider;
import com.github.aleksandy.petrovich.rules.data.Rules;
import com.github.aleksandy.petrovich.rules.loader.RulesLoader;
import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.entity.documents.OrderProperties;
import g_server.g_server.application.entity.system_data.Speciality;
import g_server.g_server.application.entity.users.AssociatedStudents;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.entity.view.ShortTaskDataView;
import g_server.g_server.application.entity.view.TaskDataView;
import g_server.g_server.application.repository.documents.DocumentRepository;
import g_server.g_server.application.repository.documents.DocumentVersionRepository;
import g_server.g_server.application.repository.documents.OrderPropertiesRepository;
import g_server.g_server.application.repository.system_data.SpecialityRepository;
import g_server.g_server.application.repository.users.AssociatedStudentsRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import g_server.g_server.application.repository.users.UsersRolesRepository;
import g_server.g_server.application.service.users.AssociatedStudentsService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DocumentProcessorService {

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

    // Обработать шаблон задания для студента
    public File studentTaskProcessing(String token, TaskDataView taskDataView) throws Exception {
        Integer userID;
        try {
            userID = getUserId(token);
        } catch (Exception e) {
            return null;
        }
        Users student;
        try {
            student = usersRepository.findById(userID).get();
        } catch (NoSuchElementException noSuchElementException) {
            return null;
        }
        if (student != null) {
            AssociatedStudents associatedStudents;
            try {
                associatedStudents = associatedStudentsRepository.findByStudent(student.getId());
            } catch (NullPointerException nullPointerException) {
                associatedStudents = null;
            }
            if (associatedStudents != null) {
                Integer type;
                Integer kind;
                switch (taskDataView.getTaskType()) {
                    case "Научно-исследовательская работа":
                        type = 1;
                        kind = 2;
                        break;
                    case "Практика по получению знаний и умений":
                        type = 2;
                        kind = 2;
                        break;
                    case "Преддипломная практика":
                        type = 3;
                        kind = 2;
                        break;
                    case "ВКР":
                        type = 4;
                        kind = 2;
                        break;
                    default:
                        type = 0;
                        kind = 0;
                }
                List<Document> taskList = documentRepository.findByTypeAndKind(type, kind);
                if (taskList.size() > 0) {
                    String specialityName = "";
                    Speciality speciality = null;
                    try {
                        speciality = specialityRepository.findByCode(taskDataView.getOrderSpeciality());
                    } catch (NullPointerException nullPointerException){
                        specialityName = "Введите название специальности";
                    }
                    if (speciality != null) {
                        specialityName = speciality.getSpeciality();
                    }
                    String studentDocumentsPath = "src" + File.separator + "main" +
                            File.separator + "resources" + File.separator + "users_documents" +
                            File.separator + student.getId();
                    File studentDir = new File(studentDocumentsPath);
                    if (!studentDir.exists()) {
                        studentDir.mkdir();
                    }
                    Document currentTask = taskList.get(taskList.size() - 1);
                    List<DocumentVersion> taskVersions = documentVersionRepository.findByDocument(currentTask.getId());
                    DocumentVersion taskVersion = taskVersions.get(taskVersions.size() - 1);
                    XWPFDocument template = openDocument(taskVersion.getThis_version_document_path());
                    return taskProcessing(template, taskDataView, specialityName, studentDocumentsPath, student);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // Обработать укороченный шаблон задания для студента
    public File studentShortTaskProcessing(String token, ShortTaskDataView shortTaskDataView) throws Exception {
        Integer userID;
        try {
            userID = getUserId(token);
        } catch (Exception e) {
            return null;
        }
        Users student;
        try {
            student = usersRepository.findById(userID).get();
        } catch (NoSuchElementException noSuchElementException) {
            return null;
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
                try {
                    orderProperty = orderPropertiesRepository.findBySpeciality(speciality.getId());
                } catch (NullPointerException nullPointerException) {
                    orderProperty = null;
                }
                if (orderProperty != null) {
                    Document document = documentRepository.findById(orderProperty.getId()).get();
                    Users advisor = usersRepository.findById(associatedStudents.getScientificAdvisor()).get();
                    Users headOfCathedra = usersRepository.findById(
                            usersRolesRepository.findByRoleId(3).getUserId()).get();
                    Integer type;
                    Integer kind;
                    switch (shortTaskDataView.getTaskType()) {
                        case "Научно-исследовательская работа":
                            type = 1;
                            kind = 2;
                            break;
                        case "Практика по получению знаний и умений":
                            type = 2;
                            kind = 2;
                            break;
                        case "Преддипломная практика":
                            type = 3;
                            kind = 2;
                            break;
                        case "ВКР":
                            type = 4;
                            kind = 2;
                            break;
                        default:
                            type = 0;
                            kind = 0;
                    }
                    List<Document> taskList = documentRepository.findByTypeAndKind(type, kind);
                    if (taskList.size() > 0) {
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
                        String studentDocumentsPath = "src" + File.separator + "main" +
                                File.separator + "resources" + File.separator + "users_documents" +
                                File.separator + student.getId();
                        File studentDir = new File(studentDocumentsPath);
                        if (!studentDir.exists()) {
                            studentDir.mkdir();
                        }
                        Document currentTask = taskList.get(taskList.size() - 1);
                        List<DocumentVersion> taskVersions = documentVersionRepository.findByDocument(currentTask.getId());
                        DocumentVersion taskVersion = taskVersions.get(taskVersions.size() - 1);
                        XWPFDocument template = openDocument(taskVersion.getThis_version_document_path());
                        return taskProcessing(template, taskDataView,
                                speciality.getSpeciality(), studentDocumentsPath, student);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // Обработать шаблон
    public File taskProcessing(XWPFDocument template, TaskDataView taskDataView,
                               String speciality, String studentDocumentsPath, Users student) throws Exception {
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
        wordReplaceService.replaceWordsInText("ФИОПД", getDpFio(student.getSurname(), student.getName(), student.getSecond_name()));
        wordReplaceService.replaceWordsInText("ФИОПР", getRpFio(student.getSurname(), student.getName(), student.getSecond_name()));
        wordReplaceService.replaceWordsInText("ФИО С", getShortFio(taskDataView.getStudentFio()));
        wordReplaceService.replaceWordsInText("ФИО НР", getShortFio(taskDataView.getAdvisorFio()));
        wordReplaceService.replaceWordsInText("ФИО ЗВК", getShortFio(taskDataView.getHeadFio()));
        wordReplaceService.replaceWordsInText("ИЗУЧИТЬ", taskDataView.getToExplore());
        wordReplaceService.replaceWordsInText("СОЗДАТЬ", taskDataView.getToCreate());
        wordReplaceService.replaceWordsInText("ОЗНАКОМИТЬСЯ", taskDataView.getToFamiliarize());
        wordReplaceService.replaceWordsInText("ДОПЗАДАНИЕ", taskDataView.getAdditionalTask());
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
        wordReplaceService.replaceWordsInText("ФИОПД", getDpFio(student.getSurname(), student.getName(), student.getSecond_name()));
        wordReplaceService.replaceWordsInText("ФИОПР", getRpFio(student.getSurname(), student.getName(), student.getSecond_name()));
        wordReplaceService.replaceWordsInTables("ФИО С", getShortFio(taskDataView.getStudentFio()));
        wordReplaceService.replaceWordsInTables("ФИО НР", getShortFio(taskDataView.getAdvisorFio()));
        wordReplaceService.replaceWordsInTables("ФИО ЗВК", getShortFio(taskDataView.getHeadFio()));
        wordReplaceService.replaceWordsInTables("ИЗУЧИТЬ", "Изучить " + taskDataView.getToExplore());
        wordReplaceService.replaceWordsInTables("СОЗДАТЬ", toUpperCaseFirstSymbol(taskDataView.getToCreate()));
        wordReplaceService.replaceWordsInTables("ОЗНАКОМИТЬСЯ", "Ознакомиться" + taskDataView.getToFamiliarize());
        wordReplaceService.replaceWordsInTables("ДОПЗАДАНИЕ", toUpperCaseFirstSymbol(taskDataView.getAdditionalTask()));
        File file = wordReplaceService.saveAndGetModdedFile(studentDocumentsPath + File.separator + "temp.docx");
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