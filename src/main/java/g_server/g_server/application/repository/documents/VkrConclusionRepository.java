package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.vkr_other.VkrAdvisorConclusion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VkrConclusionRepository extends JpaRepository<VkrAdvisorConclusion, Integer> {
    @Override
    List<VkrAdvisorConclusion> findAll();

    @Override
    <S extends VkrAdvisorConclusion> S save(S s);

    @Override
    Optional<VkrAdvisorConclusion> findById(Integer integer);

    @Override
    boolean existsById(Integer integer);

    @Override
    void deleteById(Integer integer);

    VkrAdvisorConclusion findByVersionID(Integer integer);
}