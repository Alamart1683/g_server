package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.reports.VkrReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VkrReportRepository extends JpaRepository<VkrReport, Integer> {
    @Override
    List<VkrReport> findAll();

    @Override
    <S extends VkrReport> S save(S s);

    @Override
    Optional<VkrReport> findById(Integer integer);

    @Override
    boolean existsById(Integer integer);

    @Override
    void deleteById(Integer integer);

    VkrReport findByVersionID(Integer integer);
}
