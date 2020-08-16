package g_server.entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

// Слой доступа к данным зарегистрированных пользователей
@Entity
@Table(name = "users")
@Data //Ломбок аннотация: генерирует геттеры, сеттеры, иквалс, хеш код методы
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String email;

    @Column
    private String password;

    /*
    @Column
    private String student_group;

    @Column
    private String phone;

    @Column
    private String name;

    @Column
    private String surname;

    @Column
    private String patronymic;

    @Column
    private String scientific_adviser;

    @Column
    private String role;
     */
}
