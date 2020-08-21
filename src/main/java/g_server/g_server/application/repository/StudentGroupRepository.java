package g_server.g_server.application.repository;

import g_server.g_server.application.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentGroupRepository extends JpaRepository<StudentGroup, Integer> {

}
