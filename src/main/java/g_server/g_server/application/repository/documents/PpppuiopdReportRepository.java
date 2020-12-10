package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.reports.PpppuiopdReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PpppuiopdReportRepository extends JpaRepository<PpppuiopdReport, Integer> {
    @Override
    List<PpppuiopdReport> findAll();

    @Override
    <S extends PpppuiopdReport> S save(S s);

    @Override
    Optional<PpppuiopdReport> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    PpppuiopdReport findByVersionID(Integer versionID);
}