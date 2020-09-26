package g_server.g_server.application.repository.project;

import g_server.g_server.application.entity.project.ProjectTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectThemeRepository extends JpaRepository<ProjectTheme, Integer> {
    @Override
    List<ProjectTheme> findAll();

    @Override
    <S extends ProjectTheme> S save(S s);

    @Override
    Optional<ProjectTheme> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    ProjectTheme findByThemeAndAdvisor(String theme, Integer advisor);

    List<ProjectTheme> findByAdvisor(Integer advisor);
}
