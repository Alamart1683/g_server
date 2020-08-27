package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.DocumentKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentKindRepository extends JpaRepository<DocumentKind, Integer> {
    @Override
    List<DocumentKind> findAll();

    @Override
    <S extends DocumentKind> S save(S s);

    @Override
    Optional<DocumentKind> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    DocumentKind getDocumentKindByKind(String kind);
}