package g_server.g_server.application.service.users;

import g_server.g_server.application.entity.users.Roles;
import g_server.g_server.application.repository.users.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RolesService {
    private RolesRepository rolesRepository;

    @Autowired
    public void setRolesRepository(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    public List<Roles> findAll() {
        return rolesRepository.findAll();
    }

    public Optional<Roles> findById(int id) {
        return rolesRepository.findById(id);
    }

    public void save(Roles roles) {
        rolesRepository.save(roles);
    }

    public void delete(int id) {
        rolesRepository.deleteById(id);
    }
}