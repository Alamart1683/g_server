package g_server.g_server.application.repository;

import g_server.g_server.application.entity.StudentData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentDataRepository extends JpaRepository<StudentData, Integer> {
    @Override
    List<StudentData> findAll();

    @Override
    <S extends StudentData> S save(S s);

    @Override
    void deleteById(Integer integer);

    @Override
    Optional<StudentData> findById(Integer integer);
}
