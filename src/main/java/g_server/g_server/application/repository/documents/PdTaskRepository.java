package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.PdTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PdTaskRepository extends JpaRepository<PdTask, Integer> {
    @Override
    List<PdTask> findAll();

    @Override
    <S extends PdTask> S save(S s);

    @Override
    Optional<PdTask> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    PdTask findByVersionID(Integer versionID);
}