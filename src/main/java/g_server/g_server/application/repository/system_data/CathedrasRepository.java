package g_server.g_server.application.repository.system_data;

import g_server.g_server.application.entity.system_data.Cathedras;
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

    Cathedras getCathedrasByCathedraName(String s);
}