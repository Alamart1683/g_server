package g_server.g_server.application.repository.project;

import g_server.g_server.application.entity.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    @Override
    List<Project> findAll();

    @Override
    <S extends Project> S save(S s);

    @Override
    Optional<Project> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    List<Project> findAllByScientificAdvisorID(Integer id);

    boolean existsByArea(Integer typeID);

    Project findByScientificAdvisorIDAndName(Integer advisorID, String projectName);
}