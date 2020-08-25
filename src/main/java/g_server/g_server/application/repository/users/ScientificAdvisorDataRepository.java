package g_server.g_server.application.repository.users;

import g_server.g_server.application.entity.users.ScientificAdvisorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScientificAdvisorDataRepository extends JpaRepository<ScientificAdvisorData, Integer> {
    @Override
    List<ScientificAdvisorData> findAll();

    @Override
    <S extends ScientificAdvisorData> S save(S s);

    @Override
    Optional<ScientificAdvisorData> findById(Integer integer);

    @Override
    void deleteById(Integer integer);
}