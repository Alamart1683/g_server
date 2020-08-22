package g_server.g_server.application.repository;

import g_server.g_server.application.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    @Override
    List<UserRole> findAll();

    @Override
    Optional<UserRole> findById(Integer integer);

    @Override
    <S extends UserRole> S save(S s);

    @Override
    void deleteById(Integer integer);
}