package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.ViewRightsProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ViewRightsProjectRepository extends JpaRepository<ViewRightsProject, Integer> {
    @Override
    List<ViewRightsProject> findAll();

    @Override
    <S extends ViewRightsProject> S save(S s);

    @Override
    Optional<ViewRightsProject> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    ViewRightsProject findByDocument(Integer documentID);
}
