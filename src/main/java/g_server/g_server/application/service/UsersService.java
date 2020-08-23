package g_server.g_server.application.service;

import g_server.g_server.application.entity.*;
import g_server.g_server.application.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService implements UserDetailsService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

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

    public boolean saveStudent(Users user, String student_type, String student_group, String cathedra_name) {
        Users userFromDB = usersRepository.findByEmail(user.getEmail());
        if (isUserExists(userFromDB)) {
            return false;
        }
        else {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            usersRepository.save(user);
            UserRole userRole = new UserRole(user.getId(), true,
                    false, false, false);
            userRoleRepository.save(userRole);
            StudentData studentData = new StudentData(
                    userRole.getId(),
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
            usersRepository.save(user);
            UserRole userRole = new UserRole(user.getId(), false,
                    true, false, false);
            userRoleRepository.save(userRole);
            ScientificAdvisorData scientificAdvisorData = new ScientificAdvisorData(userRole.getId(),
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
            usersRepository.save(user);
            UserRole userRole = new UserRole(user.getId(), false,
                    false, true, false);
            userRoleRepository.save(userRole);
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
            usersRepository.save(user);
            UserRole userRole = new UserRole(user.getId(), false,
                    false, false, true);
            userRoleRepository.save(userRole);
            ScientificAdvisorData scientificAdvisorData = new ScientificAdvisorData(userRole.getId(),
                    cathedrasRepository.getCathedrasByCathedraName(cathedra_name).getId());
            scientificAdvisorDataRepository.save(scientificAdvisorData);
            return true;
        }
    }

    public boolean isUserExists(Users user) {
        if (user == null)
            return false;
        else
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