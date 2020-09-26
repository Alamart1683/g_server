package g_server.g_server.application.repository.users;

import g_server.g_server.application.entity.users.AssociatedStudents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssociatedStudentsRepository extends JpaRepository<AssociatedStudents, Integer> {
    @Override
    List<AssociatedStudents> findAll();

    @Override
    <S extends AssociatedStudents> S save(S s);

    @Override
    Optional<AssociatedStudents> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    List<AssociatedStudents> findByScientificAdvisor(int scientificAdvisorId);

    AssociatedStudents findByStudent(int studentId);

    AssociatedStudents findByScientificAdvisorAndStudent(int scientificAdvisorId, int studentId);

    boolean existsByTheme(Integer themeID);
}