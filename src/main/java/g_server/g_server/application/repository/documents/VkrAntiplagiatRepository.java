package g_server.g_server.application.repository.documents;

import g_server.g_server.application.entity.documents.vkr_other.VkrAntiplagiat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VkrAntiplagiatRepository extends JpaRepository<VkrAntiplagiat, Integer> {
    @Override
    List<VkrAntiplagiat> findAll();

    @Override
    <S extends VkrAntiplagiat> S save(S s);

    @Override
    Optional<VkrAntiplagiat> findById(Integer integer);

    @Override
    boolean existsById(Integer integer);

    @Override
    void deleteById(Integer integer);

    VkrAntiplagiat findByVersionID(Integer integer);
}