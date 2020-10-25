package g_server.g_server.application.service.users;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.forms.AutomaticStudentForm;
import g_server.g_server.application.entity.forms.ScientificAdvisorForm;
import g_server.g_server.application.entity.forms.StudentForm;
import g_server.g_server.application.entity.project.Project;
import g_server.g_server.application.entity.users.*;
import g_server.g_server.application.entity.users.passwords.PasswordGenerator;
import g_server.g_server.application.entity.view.PersonalStudentView;
import g_server.g_server.application.entity.view.StudentAdvisorView;
import g_server.g_server.application.repository.project.OccupiedStudentsRepository;
import g_server.g_server.application.repository.project.ProjectRepository;
import g_server.g_server.application.repository.system_data.CathedrasRepository;
import g_server.g_server.application.repository.system_data.StudentGroupRepository;
import g_server.g_server.application.repository.system_data.StudentTypeRepository;
import g_server.g_server.application.repository.users.*;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Logger;

@Service
public class UsersService implements UserDetailsService {
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

    // Сохарнить студента
    public boolean saveStudent(Users user, String student_type, String student_group, String cathedra_name) {
        Users userFromDB = usersRepository.findByEmail(user.getEmail());
        if (isUserExists(userFromDB)) {
            return false;
        }
        else {
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
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setRoles(Collections.singleton(new Roles(2, "ROLE_SCIENTIFIC_ADVISOR")));
            usersRepository.save(user);
            ScientificAdvisorData scientificAdvisorData = new ScientificAdvisorData(user.getId(),
                    cathedrasRepository.getCathedrasByCathedraName(cathedra_name).getId(), places);
            scientificAdvisorDataRepository.save(scientificAdvisorData);
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
            usersRepository.save(user);
            ScientificAdvisorData scientificAdvisorData = new ScientificAdvisorData(user.getId(),
                    cathedrasRepository.getCathedrasByCathedraName(cathedra_name).getId(), places);
            scientificAdvisorDataRepository.save(scientificAdvisorData);
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
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setRoles(Collections.singleton(new Roles(4, "ROLE_ADMIN")));
            usersRepository.save(user);
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

    // TODO Сформировать личный кабинет научного руководителя или заведующего кафедрой

    // TODO Сформировать личный кабинет администратора/рута

    // TODO Сделать функционал для восстановления пароля

    // Зарегистрировать студентов автоматически на базе *.xls файла
    public String studentAutomaticRegistration(AutomaticStudentForm automaticStudentForm) throws IOException {
        MultipartFile multipartFile = automaticStudentForm.getStudentData();
        String cathedra = automaticStudentForm.getCathedra();
        String type = automaticStudentForm.getType();
        String tempPath = "src" + File.separator + "main" + File.separator +
                "resources" + File.separator + "users_documents" + File.separator + "temp";
        File temp = new File(tempPath);
        if (!temp.exists()) {
            temp.mkdir();
        }
        // Загрузим xls-файл в систему
        try (OutputStream os = Files.newOutputStream(Paths.get(tempPath + File.separator +
                "studentData.xls"))) {
            os.write(multipartFile.getBytes());
            HSSFWorkbook excelStudentData = new HSSFWorkbook(
                    new FileInputStream(new File(tempPath + File.separator + "studentData.xls")));
            File deleteFile = new File(tempPath + File.separator + "studentData.xls");
            HSSFSheet studentSheet = excelStudentData.getSheet("Обучающиеся");
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
                        // TODO Сделать приведение телефона к уницифицированнному виду
                        String phone = hssfRow.getCell(7).getRichStringCellValue().getString();
                        if (!phone.equals("")) {
                            student.setPhone(getNormalPhone(phone));
                        } else if (phone.equals("")) {
                            phone = hssfRow.getCell(9).getRichStringCellValue().getString();
                            if (!phone.equals("")) {
                                student.setPhone(getNormalPhone(phone));
                            }
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

                        // Укажем, что аккаунт подтвержден и согласен на почтовую рассылку
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
                // Проверим, нет ли данных студентов в базе
                for (int i = 0; i < studentList.size(); i++) {
                    if (isUserExistsInDB(studentList.get(i))) {
                        deleteFile.delete();
                        return "Операция отменена для поддержания целостности данных." +
                                " Часть студентов уже существует в базе";
                    }
                }
                for (int i = 0; i < studentList.size(); i++) {
                    studentList.get(i).setRoles(Collections.singleton(new Roles(1, "ROLE_STUDENT")));
                    usersRepository.save(studentList.get(i));
                    studentDataList.get(i).setId(studentList.get(i).getId());
                    studentDataRepository.save(studentDataList.get(i));
                    String decodePassword = decodePasswords.get(i);
                    // TODO Сделать рассылку писем о регистрации
                }
                testListToFile(decodePasswords, studentList);
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
    public void testListToFile(List<String> passwords, List<Users> students) {
        File file = new File("src" + File.separator + "main" + File.separator +
                "resources" + File.separator + "testFioLoginAndPassword.txt");
        if (file.exists()) {
            file.delete();
        }
        Writer writer = null;
        try {
            writer = new FileWriter("src" + File.separator + "main" + File.separator +
                    "resources" + File.separator + "testFioLoginAndPassword.txt");
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
}