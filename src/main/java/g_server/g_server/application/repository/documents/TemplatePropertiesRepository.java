package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.TemplateProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TemplatePropertiesRepository extends JpaRepository<TemplateProperties, Integer> {
    @Override
    List<TemplateProperties> findAll();

    @Override
    <S extends TemplateProperties> S save(S s);

    @Override
    Optional<TemplateProperties> findById(Integer integer);

    @Override
    void deleteById(Integer integer);
}
