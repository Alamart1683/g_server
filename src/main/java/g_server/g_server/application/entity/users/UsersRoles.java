package g_server.g_server.application.entity.users;

import javax.persistence.*;

/*
Доступ к данной таблице будет только
для чтения и сопоставления ролей
 */
@Entity
@Table(name = "users_roles")
public class UsersRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "role_id")
    private int roleId;

    public UsersRoles() { }

    public int getRoleId() {
        return roleId;
    }

    public int getUserId() { return  userId; }
}