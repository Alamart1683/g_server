package g_server.g_server.application.repository.project;

import g_server.g_server.application.entity.project.ProjectArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectAreaRepository extends JpaRepository<ProjectArea, Integer> {
    @Override
    List<ProjectArea> findAll();

    @Override
    <S extends ProjectArea> S save(S s);

    @Override
    Optional<ProjectArea> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    ProjectArea findByThemeAndAdvisor(String theme, Integer advisor);

    List<ProjectArea> findByAdvisor(Integer advisor);
}
