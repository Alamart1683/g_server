package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.NirReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NirReportRepository extends JpaRepository<NirReport, Integer> {
    @Override
    List<NirReport> findAll();

    @Override
    <S extends NirReport> S save(S s);

    @Override
    Optional<NirReport> findById(Integer integer);

    @Override
    void deleteById(Integer integer);
}
