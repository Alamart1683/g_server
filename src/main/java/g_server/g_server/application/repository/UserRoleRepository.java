package g_server.g_server.application.repository;

import g_server.g_server.application.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {

}
