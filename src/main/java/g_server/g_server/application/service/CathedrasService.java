package g_server.g_server.application.service;

import g_server.g_server.application.entity.Cathedras;
import g_server.g_server.application.repository.CathedrasRepository;
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

    public Cathedras findByCathedraName(String cathedra_name) {
        return cathedrasRepository.findByCathedraName(cathedra_name);
    }
}
