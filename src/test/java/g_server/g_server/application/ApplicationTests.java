package g_server.g_server.application;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.service.users.UsersService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {
    private UsersService usersService;
    private JwtProvider jwtProvider;

    @Autowired
    public void setUsersService(UsersService usersService) {
        this.usersService = usersService;
    }

    @Autowired
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Test
    void contextLoads() {
    }

    @Test
    void firstUserLoadTest() {
        String email = "Delamart1683@yandex.ru";
        String password = "3Bq0050eQxSx";
        Users user = usersService.loadUserByEmailAndPassword(email, password);
        Assertions.assertEquals("Андрей Лисовой", user.getName() + " " + user.getSurname());
    }

    @Test
    void secondUserLoadTest() {
        String email = "korra-m@yandex.ru";
        String password = "LGghRb0UL6RL";
        Users user = usersService.loadUserByEmailAndPassword(email, password);
        Assertions.assertEquals("Марина Карева", user.getName() + " " + user.getSurname());
    }

    @Test
    void thirdUserLoadTest() {
        String email = "vkgrig49@mail.ru";
        String password = "s4VQ5LtVVZ6g";
        Users user = usersService.loadUserByEmailAndPassword(email, password);
        Assertions.assertEquals("Виктор Григорьев", user.getName() + " " + user.getSurname());
    }

    @Test
    void fourthUserLoadTest() {
        String email = "abyrvalg";
        String password = "12345";
        Users user = usersService.loadUserByEmailAndPassword(email, password);
        Assertions.assertNull(user);
    }

    @Test
    void fifthUserLoadTest() {
        String email = "ascalon";
        String password = "54321";
        Users user = usersService.loadUserByEmailAndPassword(email, password);
        Assertions.assertNull(user);
    }

    @Test
    void firstAccessTokenTest() {
        String email = "Delamart1683@yandex.ru";
        long accessIssue = java.time.Instant.now().getEpochSecond();
        long accessExpire = java.time.Instant.now().getEpochSecond() + 172800;
        String accessToken = jwtProvider.generateAccessToken(email, accessIssue, accessExpire);
        Assertions.assertTrue(jwtProvider.validateAccessToken(accessToken));
    }

    @Test
    void secondAccessTokenTest() {
        String email = "korra-m@yandex.ru";
        long accessIssue = java.time.Instant.now().getEpochSecond();
        long accessExpire = java.time.Instant.now().getEpochSecond() + 172800;
        String accessToken = jwtProvider.generateAccessToken(email, accessIssue, accessExpire);
        Assertions.assertTrue(jwtProvider.validateAccessToken(accessToken));
    }

    @Test
    void thirdAccessTokenTest() {
        String email = "vkgrig49@mail.ru";
        long accessIssue = java.time.Instant.now().getEpochSecond();
        long accessExpire = java.time.Instant.now().getEpochSecond() + 172800;
        String accessToken = jwtProvider.generateAccessToken(email, accessIssue, accessExpire);
        Assertions.assertTrue(jwtProvider.validateAccessToken(accessToken));
    }

    @Test
    void fourthAccessTokenTest() {
        String email = "abyrvalg";
        long accessIssue = java.time.Instant.now().getEpochSecond();
        String accessToken = jwtProvider.generateAccessToken(email, accessIssue, accessIssue);
        Assertions.assertFalse(jwtProvider.validateAccessToken(accessToken));
    }

    @Test
    void fifthAccessTokenTest() {
        String email = "ascalon";
        long accessIssue = java.time.Instant.now().getEpochSecond();
        long accessExpire = java.time.Instant.now().getEpochSecond() + 172800;
        String accessToken = jwtProvider.generateAccessToken(email, accessIssue, accessExpire);
        Assertions.assertFalse(jwtProvider.validateAccessToken(accessToken));
    }

    @Test
    void firstRefreshTokenTest() {
        String email = "Delamart1683@yandex.ru";
        long refreshIssue = java.time.Instant.now().getEpochSecond();
        long refreshExpire = java.time.Instant.now().getEpochSecond() + 5184000;
        String refreshToken = jwtProvider.generateRefreshToken(email, refreshIssue, refreshExpire);
        Assertions.assertTrue(jwtProvider.validateRefreshToken(refreshToken));
    }

    @Test
    void secondRefreshTokenTest() {
        String email = "korra-m@yandex.ru";
        long refreshIssue = java.time.Instant.now().getEpochSecond();
        long refreshExpire = java.time.Instant.now().getEpochSecond() + 5184000;
        String refreshToken = jwtProvider.generateRefreshToken(email, refreshIssue, refreshExpire);
        Assertions.assertTrue(jwtProvider.validateRefreshToken(refreshToken));
    }

    @Test
    void thirdRefreshTokenTest() {
        String email = "vkgrig49@mail.ru";
        long refreshIssue = java.time.Instant.now().getEpochSecond();
        long refreshExpire = java.time.Instant.now().getEpochSecond() + 5184000;
        String refreshToken = jwtProvider.generateRefreshToken(email, refreshIssue, refreshExpire);
        Assertions.assertTrue(jwtProvider.validateRefreshToken(refreshToken));
    }

    @Test
    void fourthRefreshTokenTest() {
        String email = "abyrvalg";
        long refreshIssue = java.time.Instant.now().getEpochSecond();
        long refreshExpire = java.time.Instant.now().getEpochSecond() + 5184000;
        String refreshToken = jwtProvider.generateRefreshToken(email, refreshIssue, refreshExpire);
        Assertions.assertFalse(jwtProvider.validateRefreshToken(refreshToken));
    }

    @Test
    void fifthRefreshTokenTest() {
        String email = "ascalon";
        long refreshIssue = java.time.Instant.now().getEpochSecond();
        long refreshExpire = java.time.Instant.now().getEpochSecond() + 5184000;
        String refreshToken = jwtProvider.generateRefreshToken(email, refreshIssue, refreshExpire);
        Assertions.assertFalse(jwtProvider.validateRefreshToken(refreshToken));
    }

    @Test
    void firstGetRoleTest() {
        int userID = 1;
        String role = usersService.getUserRoleByRoleID(userID);
        Assertions.assertEquals("ROLE_ROOT", role);
    }

    @Test
    void secondGetRoleTest() {
        int userID = 2002;
        String role = usersService.getUserRoleByRoleID(userID);
        Assertions.assertEquals("ROLE_SCIENTIFIC_ADVISOR", role);
    }

    @Test
    void thirdGetRoleTest() {
        int userID = 5;
        String role = usersService.getUserRoleByRoleID(userID);
        Assertions.assertEquals("ROLE_HEAD_OF_CATHEDRA", role);
    }

    @Test
    void fourthGetRoleTest() {
        int userID = 2077;
        String role = usersService.getUserRoleByRoleID(userID);
        Assertions.assertEquals("ROLE_STUDENT", role);
    }

    @Test
    void fifthGetRoleTest() {
        int userID = 2076;
        String role = usersService.getUserRoleByRoleID(userID);
        Assertions.assertEquals("ROLE_STUDENT", role);
    }
}

