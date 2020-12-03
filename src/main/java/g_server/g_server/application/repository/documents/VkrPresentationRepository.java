package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.VkrPresentation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VkrPresentationRepository extends JpaRepository<VkrPresentation, Integer> {
    @Override
    <S extends VkrPresentation> S save(S s);

    @Override
    boolean existsById(Integer integer);

    @Override
    List<VkrPresentation> findAll();

    @Override
    void deleteById(Integer integer);

    VkrPresentation findByVersionID(Integer integer);
}