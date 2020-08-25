package g_server.g_server.application.service.users;

import g_server.g_server.application.entity.users.ScientificAdvisorData;
import g_server.g_server.application.repository.users.ScientificAdvisorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScientificAdvisorDataService {
    @Autowired
    private ScientificAdvisorDataRepository scientificAdvisorDataRepository;

    public List<ScientificAdvisorData> findAll() {
        return scientificAdvisorDataRepository.findAll();
    }

    public Optional<ScientificAdvisorData> findById(int id) {
        return scientificAdvisorDataRepository.findById(id);
    }

    public void save(ScientificAdvisorData scientificAdvisorData) {
        scientificAdvisorDataRepository.save(scientificAdvisorData);
    }

    public void delete(int id) {
        scientificAdvisorDataRepository.deleteById(id);
    }
}
