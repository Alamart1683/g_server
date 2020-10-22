package g_server.g_server.application.repository.users;

import g_server.g_server.application.entity.users.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    @Override
    List<RefreshToken> findAll();

    @Override
    <S extends RefreshToken> S save(S s);

    @Override
    Optional<RefreshToken> findById(Integer integer);

    @Override
    boolean existsById(Integer integer);

    @Override
    void deleteById(Integer integer);

    RefreshToken findByRefreshToken(String refreshToken);

    RefreshToken findByUserID(Integer userID);
}