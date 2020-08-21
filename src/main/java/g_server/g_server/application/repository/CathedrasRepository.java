package g_server.g_server.application.repository;

import g_server.g_server.application.entity.Cathedras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CathedrasRepository extends JpaRepository<Cathedras, Integer> {
    @Override
    List<Cathedras> findAll();

    @Override
    Optional<Cathedras> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    @Override
    <S extends Cathedras> S save(S s);
}
