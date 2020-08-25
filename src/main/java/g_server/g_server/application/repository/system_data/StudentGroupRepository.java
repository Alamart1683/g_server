package g_server.g_server.application.repository.system_data;

import g_server.g_server.application.entity.system_data.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Integer> {
    @Override
    List<StudentGroup> findAll();

    @Override
    Optional<StudentGroup> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    @Override
    <S extends StudentGroup> S save(S s);

    StudentGroup getByStudentGroup(String s);
}
