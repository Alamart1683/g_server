package g_server.g_server.application.service.documents;

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
import g_server.g_server.application.service.documents.TextReplace.DocxTextReplaceService;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                    DocumentVersion taskVersion = taskVersions.get(taskVersions.size() - 1);
                    XWPFDocument template = openDocument(taskVersion.getThis_version_document_path());

                    // Заменим тему проекта на полученную
                    // docxTextReplaceService.setSearchValue("Согласованное название темы");
                    // docxTextReplaceService.setReplacement(taskDataView.getStudentTheme());
                    // docxTextReplaceService.replace(template);

                    replace(template, "Согласованное название темы", taskDataView.getStudentTheme());

                    // Заменим дату выхода приказа вида «XX» месяца YYYY
                    //docxTextReplaceService.setSearchValue("o«XX» месяца YYYY");
                    //docxTextReplaceService.setReplacement(getFirstDateType(taskDataView.getOrderDate()));
                    //docxTextReplaceService.replace(template);

                    replace(template, "o«XX» месяца YYYY", getFirstDateType(taskDataView.getOrderDate()));

                    // Заменим дату начала НИРа вида «XX» месяца YYYY
                    //docxTextReplaceService.setSearchValue("s«XX» месяца YYYY");
                    //docxTextReplaceService.setReplacement(getFirstDateType(taskDataView.getOrderStartDate()));
                    //docxTextReplaceService.replace(template);

                    replace(template, "b«XX» месяца YYYY", getFirstDateType(taskDataView.getOrderStartDate()));

                    // Заменим дату окончания НИРа вида «XX» месяца YYYY
                    //docxTextReplaceService.setSearchValue("e«XX» месяца YYYY");
                    //docxTextReplaceService.setReplacement(getFirstDateType(taskDataView.getOrderEndDate()));
                    //docxTextReplaceService.replace(template);

                    replace(template, "e«XX» месяца YYYY", getFirstDateType(taskDataView.getOrderEndDate()));

                    // Заменим дату начала НИРа вида XX месяца YYYY
                    //docxTextReplaceService.setSearchValue("sXX месяца YYYY");
                    //docxTextReplaceService.setReplacement(getFirstDateType(taskDataView.getOrderStartDate()));
                    //docxTextReplaceService.replace(template);

                    replace(template, "bXX месяца YYYY", getFirstDateType(taskDataView.getOrderStartDate()));

                    // Заменим дату окончания НИРа вида XX месяца YYYY
                    //docxTextReplaceService.setSearchValue("eXX месяца YYYY");
                    //docxTextReplaceService.setReplacement(taskDataView.getOrderEndDate());
                    //docxTextReplaceService.replace(template);

                    replace(template, "eXX месяца YYYY", taskDataView.getOrderEndDate());

                    // Заменим дату начала НИРа вида XX.YY.ZZZZ
                    //docxTextReplaceService.setSearchValue("sXX.YY.ZZ");
                    //docxTextReplaceService.setReplacement(taskDataView.getOrderStartDate());
                    //docxTextReplaceService.replace(template);

                    replace(template, "bXX.YY.ZZ", taskDataView.getOrderStartDate());

                    // Заменим дату окончания НИРа вида XX.YY.ZZZZ
                    //docxTextReplaceService.setSearchValue("eXX.YY.ZZ");
                    //docxTextReplaceService.setReplacement(taskDataView.getOrderEndDate());
                    //docxTextReplaceService.replace(template);

                    replace(template, "eXX.YY.ZZ", taskDataView.getOrderEndDate());

                    // Заменим номер приказа на полученный
                    //docxTextReplaceService.setSearchValue("Номер приказа");
                    //docxTextReplaceService.setReplacement( taskDataView.getOrderNumber());
                    //docxTextReplaceService.replace(template);

                    replace(template, "Номер приказа", taskDataView.getOrderNumber());

                    // Заменим название кафедры на полученное
                    //docxTextReplaceService.setSearchValue("sКАФЕДРА");
                    //docxTextReplaceService.setReplacement(taskDataView.getCathedra());
                    //docxTextReplaceService.replace(template);

                    replace(template, "nКАФЕДРА", taskDataView.getCathedra());

                    // Заменим группу студента на полученную
                    //docxTextReplaceService.setSearchValue("gXXXX-YY-ZZ");
                    //docxTextReplaceService.setReplacement(taskDataView.getStudentGroup());
                    //docxTextReplaceService.replace(template);

                    replace(template, "XXXX-YY-ZZ", taskDataView.getStudentGroup());

                    // Заменим код специальности
                    //docxTextReplaceService.setSearchValue("cКод специальности");
                    //docxTextReplaceService.setReplacement(taskDataView.getOrderSpeciality());
                    //docxTextReplaceService.replace(template);

                    replace(template, "cКод специальности", taskDataView.getOrderSpeciality());

                    // Заменим название специальности
                    Speciality speciality;
                    try {
                        speciality = specialityRepository.findByCode(taskDataView.getOrderSpeciality());
                    } catch (NullPointerException nullPointerException){
                        speciality = null;
                    }
                    if (speciality != null) {
                        // Заменим название специальности
                        //docxTextReplaceService.setSearchValue("sНазвание специальности");
                        //docxTextReplaceService.setReplacement(speciality.getSpeciality());
                        //docxTextReplaceService.replace(template);

                        replace(template, "nНазвание специальности", speciality.getSpeciality());
                    }

                    // Заменим ФИО студента на укороченную версию вида Иванов И.И.
                    //docxTextReplaceService.setSearchValue("ФИО Студента");
                    //docxTextReplaceService.setReplacement( getShortFio(taskDataView.getStudentFio()));
                    //docxTextReplaceService.replace(template);

                    replace(template, "ФИОСтудента", getShortFio(taskDataView.getStudentFio()));

                    // Заменим ФИО руководителя на укороченную версию вида Иванов И.И.
                    //docxTextReplaceService.setSearchValue("ФИО Руководителя");
                    //docxTextReplaceService.setReplacement(getShortFio(taskDataView.getAdvisorFio()));
                    //docxTextReplaceService.replace(template);

                    replace(template, "ФИОРуководителя", getShortFio(taskDataView.getAdvisorFio()));

                    // Заменим ФИО завкафа на укороченную версию вида Иванов И.И.
                    //docxTextReplaceService.setSearchValue("ФИО Зав. кафедрой");
                    //docxTextReplaceService.setReplacement(getShortFio(taskDataView.getHeadFio()));
                    //docxTextReplaceService.replace(template);

                    replace(template, "ФИОЗавкафедрой", getShortFio(taskDataView.getHeadFio()));

                    // Заменим ФИО студента на фио в дательном падеже
                    //docxTextReplaceService.setSearchValue("Полное ФИО студента в Д.П.");
                    //docxTextReplaceService.setReplacement(taskDataView.getStudentFio());
                    //docxTextReplaceService.replace(template);

                    replace(template, "Полное ФИО студента в Д.П.", taskDataView.getStudentFio());

                    // Заменим ФИО студента на фио в родительном падеже
                    //docxTextReplaceService.setSearchValue("Полное ФИО студента в Р.П.");
                    //docxTextReplaceService.setReplacement(taskDataView.getStudentFio());
                    //docxTextReplaceService.replace(template);

                    replace(template, "Полное ФИО студента в Р.П.", taskDataView.getStudentFio());

                    saveDocument(template, studentDocumentsPath + File.separator + "temp.docx");
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

    private static Map<Integer, XWPFRun> getPosToRuns(XWPFParagraph paragraph) {
        int pos = 0;
        Map<Integer, XWPFRun> map = new HashMap<Integer, XWPFRun>(10);
        for (XWPFRun run : paragraph.getRuns()) {
            String runText = run.text();
            if (runText != null) {
                for (int i = 0; i < runText.length(); i++) {
                    map.put(pos + i, run);
                }
                pos += runText.length();
            }
        }
        return (map);
    }

    public static <V> void replace(XWPFDocument document, Map<String, V> map) {
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            replace(paragraph, map);
        }
    }

    public static <V> void replace(XWPFDocument document, String searchText, V replacement) {
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            replace(paragraph, searchText, replacement);
        }
    }

    private static <V> void replace(XWPFParagraph paragraph, Map<String, V> map) {
        for (Map.Entry<String, V> entry : map.entrySet()) {
            replace(paragraph, entry.getKey(), entry.getValue());
        }
    }

    public static <V> void replace(XWPFParagraph paragraph, String searchText, V replacement) {
        boolean found = true;
        while (found) {
            found = false;
            int pos = paragraph.getText().indexOf(searchText);
            if (pos >= 0) {
                found = true;
                Map<Integer, XWPFRun> posToRuns = getPosToRuns(paragraph);
                XWPFRun run = posToRuns.get(pos);
                XWPFRun lastRun = posToRuns.get(pos + searchText.length() - 1);
                int runNum = paragraph.getRuns().indexOf(run);
                int lastRunNum = paragraph.getRuns().indexOf(lastRun);
                String texts[] = replacement.toString().split("\n");
                run.setText(texts[0], 0);
                XWPFRun newRun = run;
                for (int i = 1; i < texts.length; i++) {
                    newRun.addCarriageReturn();
                    newRun = paragraph.insertNewRun(runNum + i);
                /*
                    We should copy all style attributes
                    to the newRun from run
                    also from background color, ...
                    Here we duplicate only the simple attributes...
                 */
                    newRun.setText(texts[i]);
                    newRun.setBold(run.isBold());
                    newRun.setCapitalized(run.isCapitalized());
                    // newRun.setCharacterSpacing(run.getCharacterSpacing());
                    newRun.setColor(run.getColor());
                    newRun.setDoubleStrikethrough(run.isDoubleStrikeThrough());
                    newRun.setEmbossed(run.isEmbossed());
                    newRun.setFontFamily(run.getFontFamily());
                    newRun.setFontSize(run.getFontSize());
                    newRun.setImprinted(run.isImprinted());
                    newRun.setItalic(run.isItalic());
                    newRun.setKerning(run.getKerning());
                    newRun.setShadow(run.isShadowed());
                    newRun.setSmallCaps(run.isSmallCaps());
                    newRun.setStrikeThrough(run.isStrikeThrough());
                    newRun.setSubscript(run.getSubscript());
                    newRun.setUnderline(run.getUnderline());
                }
                for (int i = lastRunNum + texts.length - 1; i > runNum + texts.length - 1; i--) {
                    paragraph.removeRun(i);
                }
            }
        }
    }

    public XWPFDocument openDocument(String file) throws Exception {
        XWPFDocument document = null;
        InputStream is = new FileInputStream(file);
        document = new XWPFDocument(is);
        return document;
    }

    // Сохранить документ
    public void saveDocument(XWPFDocument doc, String file) {
        try (FileOutputStream out = new FileOutputStream(file)) {
            doc.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
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