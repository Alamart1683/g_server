package g_server.g_server.application.service;

import g_server.g_server.application.entity.Roles;
import g_server.g_server.application.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RolesService {
    @Autowired
    private RolesRepository rolesRepository;

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