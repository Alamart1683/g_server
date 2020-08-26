package g_server.g_server.application.service.documents;

import g_server.g_server.application.entity.documents.ViewRights;
import g_server.g_server.application.repository.documents.ViewRightsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ViewRightsService {
    @Autowired
    private ViewRightsRepository viewRightsRepository;

    public List<ViewRights> findAll() {
        return viewRightsRepository.findAll();
    }

    public Optional<ViewRights> findByID(int id) {
        return viewRightsRepository.findById(id);
    }

    public void save(ViewRights viewRigths) {
        viewRightsRepository.save(viewRigths);
    }

    public void delete(int id) {
        viewRightsRepository.deleteById(id);
    }
}