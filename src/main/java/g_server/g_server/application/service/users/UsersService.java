package g_server.g_server.application.service.users;

import g_server.g_server.application.entity.forms.ScientificAdvisorForm;
import g_server.g_server.application.entity.forms.StudentForm;
import g_server.g_server.application.entity.users.Roles;
import g_server.g_server.application.entity.users.ScientificAdvisorData;
import g_server.g_server.application.entity.users.StudentData;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.repository.system_data.CathedrasRepository;
import g_server.g_server.application.repository.system_data.StudentGroupRepository;
import g_server.g_server.application.repository.system_data.StudentTypeRepository;
import g_server.g_server.application.repository.users.ScientificAdvisorDataRepository;
import g_server.g_server.application.repository.users.StudentDataRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = usersRepository.findByEmail(email);
        if (user == null)
            throw new UsernameNotFoundException("Пользователь не найден");
        return user;
    }

    public Users loadUserByEmailAndPassword(String email, String password) {
        Users user = usersRepository.findByEmail(email);
        if (user != null)
            if (bCryptPasswordEncoder.matches(password, user.getPassword()))
                return user;
        return null;
    }

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

    public boolean saveScientificAdvisor(Users user, String cathedra_name) {
        Users userFromDB = usersRepository.findByEmail(user.getEmail());
        if (isUserExists(userFromDB)) {
            return false;
        }
        else {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setRoles(Collections.singleton(new Roles(2, "ROLE_SCIENTIFIC_ADVISOR")));
            usersRepository.save(user);
            ScientificAdvisorData scientificAdvisorData = new ScientificAdvisorData(user.getId(),
                    cathedrasRepository.getCathedrasByCathedraName(cathedra_name).getId());
            scientificAdvisorDataRepository.save(scientificAdvisorData);
            return true;
        }
    }

    public boolean saveHeadOfCathedra(Users user, String cathedra_name) {
        Users userFromDB = usersRepository.findByEmail(user.getEmail());
        if (isUserExists(userFromDB)) {
            return false;
        }
        else {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setRoles(Collections.singleton(new Roles(3, "ROLE_HEAD_OF_CATHEDRA")));
            usersRepository.save(user);
            ScientificAdvisorData scientificAdvisorData = new ScientificAdvisorData(user.getId(),
                    cathedrasRepository.getCathedrasByCathedraName(cathedra_name).getId());
            scientificAdvisorDataRepository.save(scientificAdvisorData);
            return true;
        }
    }

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

    public boolean isUserExists(Users user) {
        if (user == null)
            return false;
        else
            return true;
    }

    public boolean isCathedraExist(StudentForm studentForm) {
        try { Integer cathedraTest = cathedrasRepository.getCathedrasByCathedraName(studentForm.getCathedra()).getId(); }
        catch (NullPointerException exception) { return false; }
        return true;
    }

    public boolean isCathedraExist(ScientificAdvisorForm scientificAdvisorForm) {
        try { Integer cathedraTest = cathedrasRepository.getCathedrasByCathedraName(scientificAdvisorForm.getCathedra()).getId(); }
        catch (NullPointerException exception) { return false; }
        return true;
    }

    public boolean isGroupExist(StudentForm studentForm) {
        try { Integer groupTest = studentGroupRepository.getByStudentGroup(studentForm.getStudent_group()).getId(); }
        catch (NullPointerException exception) { return false; }
        return true;
    }

    public boolean isStudentTypeExist(StudentForm studentForm) {
        try { Integer typeTest = studentTypeRepository.getByStudentType(studentForm.getStudent_type()).getId(); }
        catch (NullPointerException exception) { return false; }
        return true;
    }

    public Optional<Users> findById(int id) {
        return usersRepository.findById(id);
    }

    public List<Users> findAll() {
        return usersRepository.findAll();
    }

    public void save(Users users) {
        usersRepository.save(users);
    }

    public void delete(int id) {
        usersRepository.deleteById(id);
    }

    public Users findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }
}