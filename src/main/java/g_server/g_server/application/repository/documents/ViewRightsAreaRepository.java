package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.ViewRightsArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViewRightsAreaRepository extends JpaRepository<ViewRightsArea, Integer> {
    @Override
    List<ViewRightsArea> findAll();

    @Override
    <S extends ViewRightsArea> S save(S s);

    @Override
    Optional<ViewRightsArea> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    ViewRightsArea findByDocumentAndArea(Integer document, Integer area);
}
