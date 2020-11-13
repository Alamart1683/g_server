package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.PpppuiopdTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PpppuiopdTaskRepository extends JpaRepository<PpppuiopdTask, Integer> {
    @Override
    List<PpppuiopdTask> findAll();

    @Override
    <S extends PpppuiopdTask> S save(S s);

    @Override
    Optional<PpppuiopdTask> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    PpppuiopdTask findByVersionID(Integer versionID);
}