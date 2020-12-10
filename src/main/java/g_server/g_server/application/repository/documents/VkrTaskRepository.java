package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.tasks.VkrTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VkrTaskRepository extends JpaRepository<VkrTask, Integer> {
    @Override
    List<VkrTask> findAll();

    @Override
    <S extends VkrTask> S save(S s);

    @Override
    Optional<VkrTask> findById(Integer integer);

    @Override
    boolean existsById(Integer integer);

    @Override
    void deleteById(Integer integer);

    VkrTask findByVersionID(Integer integer);
}
