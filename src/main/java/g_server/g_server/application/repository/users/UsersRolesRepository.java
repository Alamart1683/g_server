package g_server.g_server.application.repository.users;

import g_server.g_server.application.entity.users.UsersRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRolesRepository extends JpaRepository<UsersRoles, Integer> {
    @Override
    List<UsersRoles> findAll();

    UsersRoles findUsersRolesByUserId(int userID);

    UsersRoles findByRoleId(int roleID);
}
