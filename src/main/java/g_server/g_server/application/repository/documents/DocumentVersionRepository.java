package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Integer> {
    @Override
    List<DocumentVersion> findAll();

    @Override
    <S extends DocumentVersion> S save(S s);

    @Override
    Optional<DocumentVersion> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    List<DocumentVersion> findByDocument(int document);

    List<DocumentVersion> findByEditor(int editor);
}