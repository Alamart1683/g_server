package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Integer> {
    @Override
    List<DocumentType> findAll();

    @Override
    <S extends DocumentType> S save(S s);

    @Override
    Optional<DocumentType> findById(Integer integer);

    @Override
    void deleteById(Integer integer);
}