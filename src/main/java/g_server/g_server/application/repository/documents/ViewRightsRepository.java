package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.ViewRights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ViewRightsRepository extends JpaRepository<ViewRights, Integer> {
    @Override
    List<ViewRights> findAll();

    @Override
    <S extends ViewRights> S save(S s);

    @Override
    Optional<ViewRights> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    ViewRights findByViewRight(String viewRight);
}