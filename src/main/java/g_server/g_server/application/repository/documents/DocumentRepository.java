package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
    @Override
    List<Document> findAll();

    @Override
    <S extends Document> S save(S s);

    @Override
    Optional<Document> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    List<Document> findByCreator(Integer creator_id);

    Document findByCreatorAndName(Integer creator_id, String name);

    List<Document> findByTypeAndKind(Integer type, Integer kind);
 }