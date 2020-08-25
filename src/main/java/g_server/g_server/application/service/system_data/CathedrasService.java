package g_server.g_server.application.service.system_data;

import g_server.g_server.application.entity.system_data.Cathedras;
import g_server.g_server.application.repository.system_data.CathedrasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CathedrasService {
    @Autowired
    private CathedrasRepository cathedrasRepository;

    public List<Cathedras> findAll() {
        return cathedrasRepository.findAll();
    }

    public Optional<Cathedras> findByID(int id) {
        return cathedrasRepository.findById(id);
    }

    public void save(Cathedras cathedras) {
        cathedrasRepository.save(cathedras);
    }

    public void delete(int id) {
        cathedrasRepository.deleteById(id);
    }
}
