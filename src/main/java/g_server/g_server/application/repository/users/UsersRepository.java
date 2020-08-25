package g_server.g_server.application.repository.users;

import g_server.g_server.application.entity.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    @Override
    List<Users> findAll();

    @Override
    <S extends Users> S save(S s);

    @Override
    void deleteById(Integer integer);

    @Override
    Optional<Users> findById(Integer integer);

    Users findByEmail(String email);
}