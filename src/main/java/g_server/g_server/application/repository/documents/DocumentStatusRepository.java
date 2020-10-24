package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentStatusRepository extends JpaRepository<DocumentStatus, Integer> {
    @Override
    List<DocumentStatus> findAll();

    @Override
    <S extends DocumentStatus> S save(S s);

    @Override
    Optional<DocumentStatus> findById(Integer integer);

    @Override
    void deleteById(Integer integer);
}
