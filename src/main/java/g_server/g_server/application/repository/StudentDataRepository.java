package g_server.g_server.application.repository;

import g_server.g_server.application.entity.StudentData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentDataRepository extends JpaRepository<StudentData, Integer> {

}
