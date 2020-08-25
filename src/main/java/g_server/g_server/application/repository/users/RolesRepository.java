package g_server.g_server.application.repository.users;

import g_server.g_server.application.entity.users.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer> {
    @Override
    List<Roles> findAll();

    @Override
    Optional<Roles> findById(Integer integer);

    @Override
    <S extends Roles> S save(S s);

    @Override
    void deleteById(Integer integer);
}