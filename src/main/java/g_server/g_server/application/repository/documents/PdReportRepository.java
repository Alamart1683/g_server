package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.reports.PdReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PdReportRepository extends JpaRepository<PdReport, Integer> {
    @Override
    List<PdReport> findAll();

    @Override
    <S extends PdReport> S save(S s);

    @Override
    Optional<PdReport> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    PdReport findByVersionID(Integer versionID);
}