package g_server.g_server.application.repository.project;

import g_server.g_server.application.entity.project.OccupiedStudents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OccupiedStudentsRepository extends JpaRepository<OccupiedStudents, Integer> {
    @Override
    List<OccupiedStudents> findAll();

    @Override
    <S extends OccupiedStudents> S save(S s);

    @Override
    Optional<OccupiedStudents> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    List<OccupiedStudents> findAllByProjectID(Integer projectID);

    List<OccupiedStudents> findAllByStudentID(Integer studentID);

    OccupiedStudents findByStudentID(Integer studentID);

    OccupiedStudents findAllByStudentIDAndProjectID(Integer studentID, Integer projectID);
}