package g_server.g_server.application.repository.system_data;

import g_server.g_server.application.entity.system_data.EconomyConsultants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EconomyConsultantsRepository extends JpaRepository<EconomyConsultants, Integer> {
    @Override
    List<EconomyConsultants> findAll();

    @Override
    <S extends EconomyConsultants> S save(S s);

    @Override
    Optional<EconomyConsultants> findById(Integer integer);

    @Override
    void deleteById(Integer integer);
}
