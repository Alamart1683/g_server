package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.tasks.NirTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NirTaskRepository extends JpaRepository<NirTask, Integer> {
    @Override
    List<NirTask> findAll();

    @Override
    <S extends NirTask> S save(S s);

    @Override
    Optional<NirTask> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    NirTask findByVersionID(Integer versionID);
}