package g_server.g_server.application.repository.system_data;

import g_server.g_server.application.entity.system_data.GroupsConsultants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupsConsultantsRepository extends JpaRepository<GroupsConsultants, Integer> {
    @Override
    List<GroupsConsultants> findAll();

    @Override
    <S extends GroupsConsultants> S save(S s);

    @Override
    Optional<GroupsConsultants> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    GroupsConsultants findByGroupID(Integer groupID);
}
