package g_server.g_server.application.service;

import g_server.g_server.application.entity.UserRole;
import g_server.g_server.application.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserRoleService {
    @Autowired
    private UserRoleRepository userRoleRepository;

    public List<UserRole> findAll() {
        return userRoleRepository.findAll();
    }

    public Optional<UserRole> findById(int id) {
        return userRoleRepository.findById(id);
    }

    public void save(UserRole userRole) {
        userRoleRepository.save(userRole);
    }

    public void delete(int id) {
        userRoleRepository.deleteById(id);
    }
}
