package g_server.g_server.application.repository.system_data;

import g_server.g_server.application.entity.system_data.StudentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentTypeRepository extends JpaRepository<StudentType, Integer> {
    @Override
    List<StudentType> findAll();

    @Override
    Optional<StudentType> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    @Override
    <S extends StudentType> S save(S s);

    StudentType getByStudentType(String s);
}