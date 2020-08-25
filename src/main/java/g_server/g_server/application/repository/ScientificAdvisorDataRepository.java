package g_server.g_server.application.repository;

import g_server.g_server.application.entity.StudentData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScientificAdvisorDataRepository extends JpaRepository<StudentData.ScientificAdvisorData, Integer> {
    @Override
    List<StudentData.ScientificAdvisorData> findAll();

    @Override
    <S extends StudentData.ScientificAdvisorData> S save(S s);

    @Override
    Optional<StudentData.ScientificAdvisorData> findById(Integer integer);

    @Override
    void deleteById(Integer integer);
}
