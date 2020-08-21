package g_server.g_server.application.repository;

import g_server.g_server.application.entity.StudentGroup;
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

    @Override
    <S extends StudentGroup> List<S> saveAll(Iterable<S> iterable);
}
