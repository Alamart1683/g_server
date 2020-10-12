package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.OrderProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderPropertiesRepository extends JpaRepository<OrderProperties, Integer> {
    @Override
    List<OrderProperties> findAll();

    @Override
    <S extends OrderProperties> S save(S s);

    @Override
    Optional<OrderProperties> findById(Integer integer);

    @Override
    void deleteAll();

    OrderProperties findBySpeciality(int speciality);
}
