package g_server.g_server.application.service.users;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.documents.Document;
import g_server.g_server.application.entity.forms.AutomaticRegistrationForm;
import g_server.g_server.application.entity.forms.ScientificAdvisorForm;
import g_server.g_server.application.entity.forms.StudentForm;
import g_server.g_server.application.entity.project.Project;
import g_server.g_server.application.entity.users.*;
import g_server.g_server.application.entity.users.passwords.PasswordGenerator;
import g_server.g_server.application.entity.view.PersonalAdvisorView;
import g_server.g_server.application.entity.view.PersonalStudentView;
import g_server.g_server.application.entity.view.StagesDatesView;
import g_server.g_server.application.entity.view.StudentAdvisorView;
import g_server.g_server.application.repository.documents.DocumentRepository;
import g_server.g_server.application.repository.project.OccupiedStudentsRepository;
import g_server.g_server.application.repository.project.ProjectRepository;
import g_server.g_server.application.repository.system_data.CathedrasRepository;
import g_server.g_server.application.repository.system_data.StudentGroupRepository;
import g_server.g_server.application.repository.system_data.StudentTypeRepository;
import g_server.g_server.application.repository.users.*;
import g_server.g_server.application.service.documents.DocumentUploadService;
import g_server.g_server.application.service.mail.MailService;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class UsersService implements UserDetailsService {
    @Value("${storage.location}")
    private String storageLocation;

    @Value("${test.auth}")
    private String userTestStorage;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private StudentDataRepository studentDataRepository;

    @Autowired
    private ScientificAdvisorDataRepository scientificAdvisorDataRepository;

    @Autowired
    private CathedrasRepository cathedrasRepository;

    @Autowired
    private StudentGroupRepository studentGroupRepository;

    @Autowired
    private StudentTypeRepository studentTypeRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UsersRolesRepository usersRolesRepository;

    @Autowired
    private AssociatedStudentsRepository associatedStudentsRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private OccupiedStudentsRepository occupiedStudentsRepository;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentUploadService documentUploadService;

    @Autowired
    private MailService mailService;

    private static Integer passwordChangeCode;

    @Override
    // Загрузить пользователя по email
    public UserDetails loadUserByUsername(String email) {
        Users user = usersRepository.findByEmail(email);
        if (user == null)
            return null;
        return user;
    }

    // Загрузить пользователя по email и паролю
    public Users loadUserByEmailAndPassword(String email, String password) {
        Users user = usersRepository.findByEmail(email);
        if (user != null)
            if (bCryptPasswordEncoder.matches(password, user.getPassword()))
                return user;
        return null;
    }

    // Сохранить студента
    public boolean saveStudent(Users user, String student_type, String student_group, String cathedra_name) {
        Users userFromDB = usersRepository.findByEmail(user.getEmail());
        if (isUserExists(userFromDB)) {
            return false;
        }
        else {
            String password = user.getPassword();
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setRoles(Collections.singleton(new Roles(1, "ROLE_STUDENT")));
            usersRepository.save(user);
            StudentData studentData = new StudentData(
                    user.getId(),
                    studentGroupRepository.getByStudentGroup(student_group).getId(),
                    cathedrasRepository.getCathedrasByCathedraName(cathedra_name).getId(),
                    studentTypeRepository.getByStudentType(student_type).getId()
            );
            studentDataRepository.save(studentData);
            // Отправка письма студенту
            mailService.sendLoginEmailAndPassword(user.getEmail(), password, "студента");
            testUserToFile(password, user);
            return true;
        }
    }

    // Сохранить НР
    public boolean saveScientificAdvisor(Users user, String cathedra_name, int places) {
        Users userFromDB = usersRepository.findByEmail(user.getEmail());
        if (isUserExists(userFromDB)) {
            return false;
        }
        else {
            String password = generatePassword();
            user.setPassword(bCryptPasswordEncoder.encode(password));
            user.setRoles(Collections.singleton(new Roles(2, "ROLE_SCIENTIFIC_ADVISOR")));
            usersRepository.save(user);
            ScientificAdvisorData scientificAdvisorData = new ScientificAdvisorData(user.getId(),
                    cathedrasRepository.getCathedrasByCathedraName(cathedra_name).getId(), places);
            scientificAdvisorDataRepository.save(scientificAdvisorData);
            // Отправка письма науч. руководителю
            mailService.sendLoginEmailAndPassword(user.getEmail(), password, "научного руководителя");
            testUserToFile(password, user);
            return true;
        }
    }

    // Сохранить зав. кафедрой
    public boolean saveHeadOfCathedra(Users user, String cathedra_name, int places) {
        Users userFromDB = usersRepository.findByEmail(user.getEmail());
        if (isUserExists(userFromDB)) {
            return false;
        }
        else {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setRoles(Collections.singleton(new Roles(3, "ROLE_HEAD_OF_CATHEDRA")));
            String password = generatePassword();
            user.setPassword(bCryptPasswordEncoder.encode(password));
            usersRepository.save(user);
            ScientificAdvisorData scientificAdvisorData = new ScientificAdvisorData(user.getId(),
                    cathedrasRepository.getCathedrasByCathedraName(cathedra_name).getId(), places);
            scientificAdvisorDataRepository.save(scientificAdvisorData);
            // Отправка письма зав. кафедры
            mailService.sendLoginEmailAndPassword(user.getEmail(), password, "заведующего кафедрой");
            testUserToFile(password, user);
            return true;
        }
    }

    // Сохранить админа
    public boolean saveAdmin(Users user) {
        Users userFromDB = usersRepository.findByEmail(user.getEmail());
        if (isUserExists(userFromDB)) {
            return false;
        }
        else {
            String password = generatePassword();
            user.setPassword(bCryptPasswordEncoder.encode(password));
            user.setRoles(Collections.singleton(new Roles(4, "ROLE_ADMIN")));
            usersRepository.save(user);
            // Отправка письма администратору
            mailService.sendLoginEmailAndPassword(user.getEmail(), password, "администратора");
            testUserToFile(password, user);
            return true;
        }
    }

    // Существует ли пользователь
    public boolean isUserExists(Users user) {
        if (user == null)
            return false;
        else
            return true;
    }

    // Существует ли пользователь в базе данных
    public boolean isUserExistsInDB(Users user) {
        Users testUser = usersRepository.findByEmail(user.getEmail());
        if (testUser == null) {
            return false;
        } else {
            return true;
        }
    }

    // Существует ли кафедра для формы студента
    public boolean isCathedraExist(StudentForm studentForm) {
        try {
            Integer cathedraTest = cathedrasRepository.getCathedrasByCathedraName(studentForm.getCathedra()).getId();
        }
        catch (NullPointerException exception) {
            return false;
        }
        return true;
    }

    // Существует ли кафедра для формы НР
    public boolean isCathedraExist(ScientificAdvisorForm scientificAdvisorForm) {
        try {
            Integer cathedraTest = cathedrasRepository.getCathedrasByCathedraName(scientificAdvisorForm.getCathedra()).getId();
        }
        catch (NullPointerException exception) {
            return false;
        }
        return true;
    }

    // Существует ли группа
    public boolean isGroupExist(StudentForm studentForm) {
        try {
            Integer groupTest = studentGroupRepository.getByStudentGroup(studentForm.getStudent_group()).getId();
        }
        catch (NullPointerException exception) {
            return false;
        }
        return true;
    }

    // Существует ли тип студента
    public boolean isStudentTypeExist(StudentForm studentForm) {
        try {
            Integer typeTest = studentTypeRepository.getByStudentType(studentForm.getStudent_type()).getId();
        }
        catch (NullPointerException exception) {
            return false;
        }
        return true;
    }

    // Существует ли email
    public boolean isEmailExist(String email) {
        Users userFromDB = usersRepository.findByEmail(email);

        if (userFromDB == null)
            return false;
        else
            return true;
    }

    // Найти пользователя по id
    public Optional<Users> findById(int id) {
        return usersRepository.findById(id);
    }

    // Найти всех пользователей
    public List<Users> findAll() {
        return usersRepository.findAll();
    }

    // Сохранить пользователя
    public void save(Users users) {
        usersRepository.save(users);
    }

    // Удалить пользователя по ID
    public void delete(int id) {
        usersRepository.deleteById(id);
    }

    // Метод проверки уполномоченности админа удалять пользователя
    public boolean checkUsersRoles(Integer adminId, Integer userToDeleteId) {
        if (adminId != null && userToDeleteId != null) {
            Users admin = usersRepository.findById(adminId).get();
            Users userToDelete = null;
            try { userToDelete = usersRepository.findById(userToDeleteId).get(); } catch (Exception e) { }
            if (admin != null && userToDelete != null) {
                Integer userToDeleteRoleId = usersRolesRepository.findUsersRolesByUserId(userToDeleteId).getRoleId();
                Integer adminRoleId = usersRolesRepository.findUsersRolesByUserId(adminId).getRoleId();
                if (userToDeleteRoleId != null && adminRoleId != null) {
                    if (userToDeleteRoleId == adminRoleId)
                        return false;
                    else if (userToDeleteRoleId > adminRoleId)
                        return false;
                    else if (userToDeleteRoleId < adminRoleId)
                        return true;
                }
            }
            else {
                return false;
            }
        }
        return false;
    }

    // Получить айди из токена
    public Integer getUserId(String token) {
        // Проверка токена
        if (token == null) {
            return null;
        }
        if (token.equals("")) {
            return null;
        }
        String email = jwtProvider.getEmailFromToken(token);
        Users user = usersRepository.findByEmail(email);
        if (user != null) {
            return user.getId();
        }
        else {
            return null;
        }
    }

    // Сформировать личный кабинет студента
    public PersonalStudentView getPersonalStudentView(String token) {
        Integer studentID = getUserId(token);
        if (studentID == null) {
            return null;
        }
        else {
            Users student;
            Users advisor;
            Project project;
            String advisorName = "Нет научного руководителя";
            String projectName = "Проект не назначен";
            try {
                student = usersRepository.findById(studentID).get();
            } catch (NoSuchElementException noSuchElementException) {
                return null;
            }
            Integer advisorID;
            try {
                advisorID = associatedStudentsRepository.findByStudent(studentID).getScientificAdvisor();
            } catch (NullPointerException nullPointerException) {
                advisorID = null;
            }
            if (advisorID != null) {
                try {
                    advisor = usersRepository.findById(advisorID).get();
                } catch (NoSuchElementException noSuchElementException) {
                    advisor = null;
                }
                if (advisor != null) {
                    advisorName = advisor.getSurname() + " " + advisor.getName() + " " + advisor.getSecond_name();
                }
            }
            try {
                project = projectRepository.findById(
                        occupiedStudentsRepository.findByStudentID(studentID).getProjectID()
                ).get();
            } catch (Exception e) {
                project = null;
            }
            if (project != null) {
                projectName = project.getName();
            }
            PersonalStudentView personalStudentView = new PersonalStudentView(student, advisorName, projectName);
            return personalStudentView;
        }
    }

    public String getUserRoleByRoleID(int userID) {
        UsersRoles userRole = usersRolesRepository.findUsersRolesByUserId(userID);
        String role;
        try {
            role = rolesService.findById(userRole.getRoleId()).get().getRole();
        } catch (NoSuchElementException noSuchElementException) {
            return "";
        }
        return role;
    }

    public String getExpirationDate(ZonedDateTime dateTime) {
        String day = Integer.toString(dateTime.getDayOfMonth());
        if (dateTime.getDayOfMonth() < 10) {
            day = "0" + day;
        }
        String month = Integer.toString(dateTime.getMonth().getValue());
        if (dateTime.getMonth().getValue() < 10) {
            month = "0" + month;
        }
        String year = Integer.toString(dateTime.getYear());
        return  day + "." + month + "." + year + " " + "00:00:00";
    }

    // Сформировать личный кабинет научного руководителя или заведующего кафедрой
    public PersonalAdvisorView getPersonalAdvisorView(String token) {
        Integer advisorID = getUserId(token);
        if (advisorID == null) {
            return null;
        }
        else {
            Users advisor;
            if (advisorID != null) {
                try {
                    advisor = usersRepository.findById(advisorID).get();
                    UsersRoles usersRoles = usersRolesRepository.findUsersRolesByUserId(advisor.getId());
                    if (usersRoles.getRoleId() == 2) {
                        return new PersonalAdvisorView(advisor, "Научный руководитель");
                    } else if (usersRoles.getRoleId() == 3) {
                        return new PersonalAdvisorView(advisor, "Заведующий кафедрой");
                    }
                } catch (NoSuchElementException noSuchElementException) {
                    advisor = null;
                }
            }
            return null;
        }
    }

    // TODO Сформировать личный кабинет администратора/рута

    // Зарегистрировать студентов автоматически на базе *.xls файла
    public String studentAutomaticRegistration(AutomaticRegistrationForm automaticRegistrationForm) throws IOException {
        documentUploadService.createDocumentRootDirIfIsNotExist();
        MultipartFile multipartFile = automaticRegistrationForm.getStudentData();
        String cathedra = automaticRegistrationForm.getCathedra();
        String type = automaticRegistrationForm.getType();
        String tempPath = storageLocation + File.separator + "temp";
        File temp = new File(tempPath);
        if (!temp.exists()) {
            temp.mkdir();
        }
        if (!documentUploadService.getFileExtension(multipartFile).equals("xls")) {
            return "Поддреживается только формат xls!";
        }
        // Загрузим xls-файл в систему
        try (OutputStream os = Files.newOutputStream(Paths.get(tempPath + File.separator +
                "studentData.xls"))) {
            os.write(multipartFile.getBytes());
            os.close();
            HSSFWorkbook excelStudentData = new HSSFWorkbook(
                    new FileInputStream(new File(tempPath + File.separator + "studentData.xls")));
            File deleteFile = new File(tempPath + File.separator + "studentData.xls");
            HSSFSheet studentSheet = excelStudentData.getSheetAt(0);
            // Теперь последовательно зарегестрируем студентов
            try {
                List<Users> studentList = new ArrayList<>(); // Список пользователей-студентов
                List<StudentData> studentDataList = new ArrayList<>(); // Список данных студентов
                List<String> decodePasswords = new ArrayList<>();
                Iterator rowIter = studentSheet.rowIterator();
                // Переведем данные о студентах из таблицы в вид, поддерживаемый системой
                while (rowIter.hasNext()) {
                    HSSFRow hssfRow = (HSSFRow) rowIter.next();
                    // Проверим что это не первая строка с легендой
                    if (hssfRow.getRowNum() > 0) {
                        Users student = new Users();
                        StudentData studentData = new StudentData();

                        // Определим ФИО студента
                        String fio = hssfRow.getCell(1).getRichStringCellValue().getString();
                        String[] names = fio.split(" ");
                        if (!names[0].equals("")) {
                            student.setSurname(names[0]);
                        } else {
                            deleteFile.delete();
                            return "Ошибка чтения ФИО";
                        }
                        if (!names[1].equals("")) {
                            student.setName(names[1]);
                        } else {
                            deleteFile.delete();
                            return "Ошибка чтения ФИО";
                        }
                        if (!names[2].equals("")) {
                            student.setSecond_name(names[2]);
                        } else {
                            deleteFile.delete();
                            return "Ошибка чтения ФИО";
                        }

                        // Определим его группу и прочие данные
                        String group = hssfRow.getCell(6).getRichStringCellValue().getString();
                        if (group.equals("")) {
                            deleteFile.delete();
                            return "Ошибка чтения группы";
                        }
                        Integer groupId = studentGroupRepository.getByStudentGroup(group).getId();
                        studentData.setStudent_group(groupId);
                        studentData.setCathedra(cathedrasRepository.getCathedrasByCathedraName(cathedra).getId());
                        studentData.setType(studentTypeRepository.getByStudentType(type).getId());
                        studentDataList.add(studentData);

                        // Определим телефон студента
                        String phone = hssfRow.getCell(7).getRichStringCellValue().getString();
                        if (!phone.equals("")) {
                            student.setPhone(getNormalPhone(phone));
                        } else if (phone.equals("")) {
                            phone = hssfRow.getCell(9).getRichStringCellValue().getString();
                            if (!phone.equals("")) {
                                student.setPhone(getNormalPhone(phone));
                            }
                        } else {
                            student.setPhone("Не указан");
                        }

                        // Определим почту студента
                        String email = hssfRow.getCell(10).getRichStringCellValue().getString();
                        if (!email.equals("")) {
                            student.setEmail(email);
                        } else if (email.equals("")) {
                            email = hssfRow.getCell(11).getRichStringCellValue().getString();
                            if (!email.equals("")) {
                                student.setEmail(email);
                            } else {
                                deleteFile.delete();
                                return "Не у всех студентов удалось найти почту, операция отменена";
                            }
                        }

                        // Установим сгенерированный пароль студенту
                        String password = generatePassword();
                        decodePasswords.add(password);
                        student.setPassword(bCryptPasswordEncoder.encode(password));

                        // Укажем, что аккаунт не подтвержден и согласен на почтовую рассылку
                        student.setConfirmed(false);
                        student.setSendMailAccepted(true);

                        studentList.add(student);
                    }
                }
                // Теперь сохраним студентов в базе, тем самым их зарегистрировав
                if (studentList.size() != studentDataList.size()) {
                    deleteFile.delete();
                    return "Ошибка чтения файла, количество данных студентов и студентов не совпадает!";
                }
                for (int i = 0; i < studentList.size(); i++) {
                    try {
                        studentList.get(i).setRoles(Collections.singleton(new Roles(1, "ROLE_STUDENT")));
                        usersRepository.save(studentList.get(i));
                        studentDataList.get(i).setId(studentList.get(i).getId());
                        studentDataRepository.save(studentDataList.get(i));
                        String decodePassword = decodePasswords.get(i);
                        // TODO Сделать рассылку писем о регистрации
                    } catch (Exception e) {
                        System.out.println("Попытка зарегистрировать уже имеющегося пользователя");
                    }
                }
                testListToFile(decodePasswords, studentList, false);
                deleteFile.delete();
                return "Студенты были успешно зарегистрированы!";
            } catch (NullPointerException e) {
                deleteFile.delete();
                return "Произошла ошибка чтения файла";
            }
        } catch (IOException ioException) {
            return "Произошла ошибка!";
        }
    }

    // Регистрация научных руководителей на основе *.xlsx файла
    public String advisorAutomaticRegistration(AutomaticRegistrationForm automaticRegistrationForm) throws IOException {
        documentUploadService.createDocumentRootDirIfIsNotExist();
        MultipartFile multipartFile = automaticRegistrationForm.getStudentData();
        String cathedra = automaticRegistrationForm.getCathedra();
        Integer places = 10; // TODO Заглушка для мест, потом можно добавить, если понадобится
        String tempPath = storageLocation + File.separator + "temp";
        File temp = new File(tempPath);
        if (!temp.exists()) {
            temp.mkdir();
        }
        if (!documentUploadService.getFileExtension(multipartFile).equals("xlsx")) {
            return "Поддреживается только формат xlsx!";
        }
        // Загрузим xlsx-файл в систему
        try (OutputStream os = Files.newOutputStream(Paths.get(tempPath + File.separator +
                "advisorData.xlsx"))) {
            os.write(multipartFile.getBytes());
            os.close();
            XSSFWorkbook excelStudentData = new XSSFWorkbook(
                    new FileInputStream(new File(tempPath + File.separator + "advisorData.xlsx")));
            File deleteFile = new File(tempPath + File.separator + "advisorData.xlsx");
            XSSFSheet studentSheet = excelStudentData.getSheetAt(0);
            try {
                List<Users> advisorList = new ArrayList<>();
                List<ScientificAdvisorData> advisorDataList = new ArrayList<>();
                List<String> decodePasswords = new ArrayList<>();
                Iterator rowIter = studentSheet.rowIterator();
                while (rowIter.hasNext()) {
                    XSSFRow xssfRow = (XSSFRow) rowIter.next();
                    if (xssfRow.getRowNum() > 0) {
                        Users advisor = new Users();
                        ScientificAdvisorData advisorData = new ScientificAdvisorData();

                        String fio = xssfRow.getCell(1).getRichStringCellValue().getString();
                        String[] names = fio.split(" ");
                        if (!names[0].equals("")) {
                            advisor.setSurname(names[0]);
                        } else {
                            deleteFile.delete();
                            return "Ошибка чтения ФИО";
                        }
                        if (!names[1].equals("")) {
                            advisor.setName(names[1]);
                        } else {
                            deleteFile.delete();
                            return "Ошибка чтения ФИО";
                        }
                        if (!names[2].equals("")) {
                            advisor.setSecond_name(names[2]);
                        } else {
                            deleteFile.delete();
                            return "Ошибка чтения ФИО";
                        }

                        advisorData.setCathedra(cathedrasRepository.getCathedrasByCathedraName(cathedra).getId());
                        advisorData.setPlaces(places);
                        advisorDataList.add(advisorData);

                        String email = xssfRow.getCell(2).getRichStringCellValue().getString();
                        if (!email.equals("")) {
                            advisor.setEmail(email);
                        } else if (email.equals("")) {
                            email = xssfRow.getCell(3).getRichStringCellValue().getString();
                            if (!email.equals("")) {
                                advisor.setEmail(email);
                            } else {
                                deleteFile.delete();
                                return "Не у всех научных руководителей удалось найти почту, операция отменена";
                            }
                        }
                        String password = generatePassword();
                        decodePasswords.add(password);
                        advisor.setPhone("Не указан");
                        advisor.setPassword(bCryptPasswordEncoder.encode(password));
                        advisor.setConfirmed(true);
                        advisor.setSendMailAccepted(true);
                        advisorList.add(advisor);
                    }
                }
                // Теперь сохраним студентов в базе, тем самым их зарегистрировав
                if (advisorList.size() != advisorDataList.size()) {
                    deleteFile.delete();
                    return "Ошибка чтения файла, количество данных научных руководителей и " +
                            "научных руководителей не совпадает!";
                }
                for (int i = 0; i < advisorList.size(); i++) {
                    try {
                        advisorList.get(i).setRoles(Collections.singleton(new Roles(2, "ROLE_SCIENTIFIC_ADVISOR")));
                        usersRepository.save(advisorList.get(i));
                        advisorDataList.get(i).setId(advisorList.get(i).getId());
                        scientificAdvisorDataRepository.save(advisorDataList.get(i));
                        String decodePassword = decodePasswords.get(i);
                        // TODO Сделать рассылку писем о регистрации
                    } catch (Exception e) {
                        System.out.println("Попытка зарегистрировать уже имеющегося пользователя");
                    }
                }
                testListToFile(decodePasswords, advisorList, true);
                excelStudentData.close();
                deleteFile.delete();
                return "Научные руководители были успешно зарегистрированы!";
            } catch (NullPointerException e) {
                excelStudentData.close();
                deleteFile.delete();
                return "Произошла ошибка чтения файла";
            }
        } catch (IOException ioException) {
            return "Произошла ошибка!";
        }
    }

    // Сгенерируем пароль
    public String generatePassword() {
        PasswordGenerator passwordGenerator = new PasswordGenerator.PasswordGeneratorBuilder()
                .useDigits(true).useLower(true).useUpper(true).build();
        String password = passwordGenerator.generate(12);
        return password;
    }

    // Приведем все телефоны к унифицированному виду
    public String getNormalPhone(String phone) {
        String temp = phone.replaceAll("\\D+","");
        String normalPhone = "+" + temp.charAt(0) + " " + temp.charAt(1) + temp.charAt(2) + temp.charAt(3) + " " +
                temp.charAt(4) + temp.charAt(5) + temp.charAt(6) + "-" + temp.charAt(7) + temp.charAt(8) + "-" +
                temp.charAt(9) + temp.charAt(10);
        return normalPhone;
    }

    // Тестовый метод для сохранения логинов и паролей студентов перед включением почтовой рассылки
    public void testListToFile(List<String> passwords, List<Users> students, boolean isAdvisor) {
        File testUserDir = new File(userTestStorage);
        if (!testUserDir.exists()) {
            testUserDir.mkdir();
        }
        File file = new File(userTestStorage + File.separator + "testFioLoginAndPassword_" +
                documentUploadService.getCurrentDate() + ".txt");
        if (file.exists()) {
            file.delete();
        }
        Writer writer = null;
        try {
            if (!isAdvisor) {
                writer = new FileWriter(userTestStorage +  File.separator + "testStudentFioLoginAndPassword_" +
                        documentUploadService.getCurrentDate() + ".txt");
            } else {
                writer = new FileWriter(userTestStorage +  File.separator +  "testAdvisorFioLoginAndPassword_" +
                        documentUploadService.getCurrentDate() + ".txt");
            }

            for (int i = 0; i < students.size(); i++) {
                writer.write(i + 1 + ")"
                        + " " + students.get(i).getSurname()
                        + " " + students.get(i).getName()
                        + " " + students.get(i).getSecond_name()
                        + " email: " + students.get(i).getEmail()
                        + " password: " + passwords.get(i));
                writer.write(System.getProperty("line.separator"));
            }
            writer.flush();
        } catch (Exception e) {
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    // Тестовый метод для сохранения данных авторизации конкретного пользователя
    public void testUserToFile(String password, Users user) {
        File testUserDir = new File(userTestStorage);
        if (!testUserDir.exists()) {
            testUserDir.mkdir();
        }
        File file = new File(userTestStorage + File.separator + "auth for " +
                user.getSurname() + " " + user.getName() + " " + user.getSecond_name() + " " + user.getEmail() + ".txt");
        if (file.exists()) {
            file.delete();
        }
        Writer writer = null;
        try {
                writer = new FileWriter(userTestStorage + File.separator + "auth for " +
                        user.getSurname() + " " + user.getName() + " " + user.getSecond_name() + " " + user.getEmail()
                        + ".txt");
                UsersRoles usersRole = usersRolesRepository.findUsersRolesByUserId(user.getId());
                Roles role = rolesService.findById(usersRole.getRoleId()).get();
                writer.write(
                        " " + role.getRole()
                        + " " + user.getName()
                        + " " + user.getSecond_name()
                        + " email: " + user.getEmail()
                        + " password: " + password);
                writer.write(System.getProperty("line.separator"));
            writer.flush();
        } catch (Exception e) {
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    public StudentAdvisorView getAdvisorDataByStudentToken(String token) {
        Integer studentID = getUserId(token);
        StudentAdvisorView studentAdvisorView = new StudentAdvisorView();
        if (studentID != null) {
            AssociatedStudents associatedStudent = associatedStudentsRepository.findByStudent(studentID);
            if (associatedStudent != null) {
                Users advisor = associatedStudent.getAdvisorUser();
                studentAdvisorView.setSystemAdvisorID(advisor.getId());
                studentAdvisorView.setAdvsiorFio(advisor.getSurname() + " " + advisor.getName() + " " + advisor.getSecond_name());
                studentAdvisorView.setAdvisorPhone(advisor.getPhone());
                studentAdvisorView.setAdvisorEmail(advisor.getEmail());
            }
        }
        return studentAdvisorView;
    }

    // Метод получения кода смены пароля пользователем
    public String getChangeUserPasswordCode(String token) {
        Random random = new Random();
        // Сгенерируем код подтверждения
        passwordChangeCode = random.nextInt(900000) + 100000;
        Integer userID = getUserId(token);
        Users user;
        if (usersRepository.findById(userID).isPresent()) {
            user = usersRepository.findById(userID).get();
            mailService.sendMailWithPasswordChangeCode(user, passwordChangeCode);
            return "Код для подтверждения смены пароля успешно сгенерирован и отправлен";
        } else {
            return "Ошибка: пользователь не найден";
        }
    }

    public String getChangeUserPasswordCodeByEmail(String email) {
        Random random = new Random();
        // Сгенерируем код подтверждения
        passwordChangeCode = random.nextInt(900000) + 100000;
        Users user = usersRepository.findByEmail(email);
        if (user != null) {
            mailService.sendMailWithPasswordChangeCode(user, passwordChangeCode);
            return "Код для подтверждения смены пароля успешно сгенерирован и отправлен";
        } else {
            return "Ошибка: пользователь не найден";
        }
    }

    // Проверить верный ли код подтверждения
    public boolean isCodeEquals(Integer code) {
        if (code.equals(passwordChangeCode)) {
            return true;
        } else {
            return false;
        }
    }

    // Сменить пароль
    public String changeUserPassword(String token, Integer code, String newPassword) {
        Integer userID = getUserId(token);
        Users user;
        if (usersRepository.findById(userID).isPresent() && passwordChangeCode.equals(code)) {
            user = usersRepository.findById(userID).get();
            mailService.sendMailWithPasswordChangeCode(user, passwordChangeCode);
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            usersRepository.save(user);
            mailService.sendMailAboutPasswordChanging(user);
            passwordChangeCode = null;
            return "Пароль успешно изменен";
        } else {
            return "Ошибка: пользователь не найден";
        }
    }

    public String changeUserPasswordByEmail(String email, Integer code, String newPassword) {
        Users user = usersRepository.findByEmail(email);
        if (user != null && passwordChangeCode.equals(code)) {
            mailService.sendMailWithPasswordChangeCode(user, passwordChangeCode);
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            usersRepository.save(user);
            mailService.sendMailAboutPasswordChanging(user);
            passwordChangeCode = null;
            return "Пароль успешно изменен";
        } else {
            return "Ошибка: пользователь не найден";
        }
    }

    public StagesDatesView getStagesDates() {
        StagesDatesView stagesDatesView = new StagesDatesView();
        List<Document> nirOrders = new ArrayList<>();
        List<Document> ppppuipdOrders = new ArrayList<>();
        List<Document> ppOrders = new ArrayList<>();
        List<Document> vkrOrders = new ArrayList<>();
        List<Document> orderList = documentRepository.findByKind(1);
        for (Document currentOrder: orderList) {
            if (currentOrder.getDocumentType().getType().equals("Научно-исследовательская работа")) {
                nirOrders.add(currentOrder);
            } else if (currentOrder.getDocumentType().getType().equals("Научно-исследовательская работа")) {
                ppppuipdOrders.add(currentOrder);
            } else if (currentOrder.getDocumentType().getType().equals("Научно-исследовательская работа")) {
                ppOrders.add(currentOrder);
            } else if (currentOrder.getDocumentType().getType().equals("Научно-исследовательская работа")) {
                vkrOrders.add(currentOrder);
            }
        }
        // НИР
        if (nirOrders.size() > 0) {
            stagesDatesView.setNirStart(getRussianDate(nirOrders.get(0).getOrderProperties().getStartDate()));
            stagesDatesView.setNirEnd(getRussianDate(nirOrders.get(0).getOrderProperties().getEndDate()));
        } else {
            stagesDatesView.setNirStart("Приказ не вышел");
            stagesDatesView.setNirEnd("Приказ не вышел");
        }
        // ППпПиУПД
        if (ppppuipdOrders.size() > 0) {
            stagesDatesView.setPpppuipdStart(getRussianDate(ppppuipdOrders.get(0).getOrderProperties().getStartDate()));
            stagesDatesView.setPpppuipdEnd(getRussianDate(ppppuipdOrders.get(0).getOrderProperties().getEndDate()));
        } else {
            stagesDatesView.setPpppuipdStart("Приказ не вышел");
            stagesDatesView.setPpppuipdEnd("Приказ не вышел");
        }
        // ПП
        if (ppOrders.size() > 0) {
            stagesDatesView.setPpStart(getRussianDate(ppOrders.get(0).getOrderProperties().getStartDate()));
            stagesDatesView.setPpEnd(getRussianDate(ppOrders.get(0).getOrderProperties().getEndDate()));
        } else {
            stagesDatesView.setPpStart("Приказ не вышел");
            stagesDatesView.setPpEnd("Приказ не вышел");
        }
        // ВКР
        if (vkrOrders.size() > 0) {
            stagesDatesView.setVkrStart(getRussianDate(vkrOrders.get(0).getOrderProperties().getStartDate()));
            stagesDatesView.setVkrEnd(getRussianDate(vkrOrders.get(0).getOrderProperties().getEndDate()));
        } else {
            stagesDatesView.setVkrStart("Приказ не вышел");
            stagesDatesView.setVkrEnd("Приказ не вышел");
        }
        // Текущая дата
        stagesDatesView.setCurrentDate(getCurrentDate());
        return stagesDatesView;
    }

    public String getRussianDate(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        return day + "." + month + "." + year;
    }

    public String getCurrentDate() {
        ZonedDateTime dateTime = ZonedDateTime.now();
        String currentDay;
        if (dateTime.getDayOfMonth() < 10)
            currentDay = "0" + dateTime.getDayOfMonth();
        else
            currentDay = dateTime.getDayOfMonth() + "";
        String currentDate = currentDay + "." + documentUploadService.monthWordToMonthNumber(dateTime.getMonth().toString()) +
                "." + dateTime.getYear() + ".";
        return currentDate;
    }
}