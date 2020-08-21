package g_server.g_server.application.repository;

import g_server.g_server.application.entity.StudentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentTypeRepository extends JpaRepository<StudentType, Integer> {

}
