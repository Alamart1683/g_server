package g_server.g_server.application.service.documents;

import com.aspose.words.FindReplaceOptions;
import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.entity.documents.DocumentVersion;
import g_server.g_server.application.entity.system_data.Speciality;
import g_server.g_server.application.entity.users.AssociatedStudents;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.entity.view.TaskDataView;
import g_server.g_server.application.repository.documents.DocumentRepository;
import g_server.g_server.application.repository.documents.DocumentVersionRepository;
import g_server.g_server.application.repository.system_data.SpecialityRepository;
import g_server.g_server.application.repository.users.AssociatedStudentsRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

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
                List<Document> taskList = documentRepository.findByTypeAndKind(1, 2);
                if (taskList.size() > 0) {
                    String studentDocumentsPath = "src" + File.separator + "main" +
                            File.separator + "resources" + File.separator + "users_documents" +
                            File.separator + student.getId();
                    File studentDir = new File(studentDocumentsPath);
                    if (!studentDir.exists()) {
                        studentDir.mkdir();
                    }
                    Document currentTask = taskList.get(taskList.size() - 1);
                    List<DocumentVersion> taskVersions = documentVersionRepository.findByDocument(currentTask.getId());
                    com.aspose.words.Document template =
                            new com.aspose.words.Document(taskVersions.get(taskVersions.size() - 1)
                            .getThis_version_document_path()
                    );
                    FindReplaceOptions options = new FindReplaceOptions();
                    // Заменим тему проекта на полученную
                    template.getRange().replace(Pattern.compile("Согласованное название темы"), taskDataView.getStudentTheme(), options);

                    // Заменим дату выхода приказа вида «XX» месяца YYYY
                    template.getRange().replace(Pattern.compile("o«XX» месяца YYYY"), getFirstDateType(taskDataView.getOrderDate()), options);
                    // Заменим дату начала НИРа вида «XX» месяца YYYY
                    template.getRange().replace(Pattern.compile("s«XX» месяца YYYY"), getFirstDateType(taskDataView.getOrderStartDate()), options);
                    // Заменим дату окончания НИРа вида «XX» месяца YYYY
                    template.getRange().replace(Pattern.compile("e«XX» месяца YYYY"), getFirstDateType(taskDataView.getOrderStartDate()), options);
                    // Заменим дату начала НИРа вида XX месяца YYYY
                    template.getRange().replace(Pattern.compile("sXX месяца YYYY"), getSecondDateType(taskDataView.getOrderStartDate()), options);
                    // Заменим дату окончания НИРа вида XX месяца YYYY
                    template.getRange().replace(Pattern.compile("eXX месяца YYYY"), getSecondDateType(taskDataView.getOrderEndDate()), options);
                    // Заменим дату начала НИРа вида XX.YY.ZZZZ
                    template.getRange().replace(Pattern.compile("sXX\\.YY\\.ZZ"), taskDataView.getOrderStartDate(), options);
                    // Заменим дату окончания НИРа вида XX.YY.ZZZZ
                    template.getRange().replace(Pattern.compile("eXX\\.YY\\.ZZ"), taskDataView.getOrderStartDate(), options);

                    // Заменим номер приказа на полученный
                    template.getRange().replace(Pattern.compile("Номер приказа"), taskDataView.getOrderNumber(), options);

                    // Заменим название кафедры на полученное
                    template.getRange().replace(Pattern.compile("sКАФЕДРА"), taskDataView.getCathedra(), options);

                    // Заменим группу студента на полученную
                    template.getRange().replace(Pattern.compile("gXXXX-YY-ZZ"), taskDataView.getStudentGroup(), options);

                    // Заменим код специальности
                    template.getRange().replace(Pattern.compile("cКод специальности"), taskDataView.getOrderSpeciality(), options);

                    // Заменим название специальности
                    Speciality speciality;
                    try {
                       speciality = specialityRepository.findByCode(taskDataView.getOrderSpeciality());
                    } catch (NullPointerException nullPointerException){
                        speciality = null;
                    }
                    if (speciality != null) {
                        // Заменим название специальности
                        template.getRange().replace(Pattern.compile("sНазвание специальности"), speciality.getSpeciality(), options);
                    }

                    // Заменим ФИО студента на укороченную версию вида Иванов И.И.
                    template.getRange().replace(Pattern.compile("ФИО Студента"), getShortFio(taskDataView.getStudentFio()), options);
                    // Заменим ФИО руководителя на укороченную версию вида Иванов И.И.
                    template.getRange().replace(Pattern.compile("ФИО Руководителя"), getShortFio(taskDataView.getAdvisorFio()), options);
                    // Заменим ФИО завкафа на укороченную версию вида Иванов И.И.
                    template.getRange().replace(Pattern.compile("ФИО Зав\\. кафедрой"), getShortFio(taskDataView.getHeadFio()), options);
                    // Заменим ФИО студента на фио в дательном падеже
                    template.getRange().replace(Pattern.compile("Полное ФИО студента в Д\\.П\\."), getShortFio(taskDataView.getStudentFio()), options);

                    template.save(studentDocumentsPath + File.separator + "temp.docx");
                    File file = new File(studentDocumentsPath + File.separator + "temp.docx");
                    return file;
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
    public String getDpFio(String Fio) {
        // TODO Сделать это когда-нибудь
        return "";
    }

    // Преобразование ФИО к родительному падежу
    public String getRpFio(String Fio) {
        // TODO Сделать это когда-нибудь
        return "";
    }

    // Получить слово месяца
    public String getMonthWord(String month) {
        // Определим месяц
        String monthWord;
        switch (month) {
            case "01":
                monthWord = "января";
            case "02":
                monthWord = "февраля";
            case "03":
                monthWord = "марта";
            case "04":
                monthWord = "апреля";
            case "05":
                monthWord = "мая";
            case "06":
                monthWord = "июня";
            case "07":
                monthWord = "июля";
            case "08":
                monthWord = "августа";
            case "09":
                monthWord = "сентября";
            case "10":
                monthWord = "октября";
            case "11":
                monthWord = "ноября";
            case "12":
                monthWord = "декабря";
            default:
                monthWord = "ошибка";
        }
        return monthWord;
    }

    // Необходимо получить id пользователя-создателя документа из токена
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