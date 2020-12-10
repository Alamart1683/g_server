package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.vkr_other.VkrAllowance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VkrAllowanceRepository extends JpaRepository<VkrAllowance, Integer> {
    @Override
    List<VkrAllowance> findAll();

    @Override
    Page<VkrAllowance> findAll(Pageable pageable);

    @Override
    <S extends VkrAllowance> S save(S s);

    @Override
    Optional<VkrAllowance> findById(Integer integer);

    @Override
    boolean existsById(Integer integer);

    VkrAllowance findByVersionID(Integer integer);
}