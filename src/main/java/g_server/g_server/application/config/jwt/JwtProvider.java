package g_server.g_server.application.config.jwt;

import g_server.g_server.application.entity.users.RefreshToken;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.repository.users.RefreshTokenRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import io.jsonwebtoken.*;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Log
@Component
public class JwtProvider {
    private UsersRepository usersRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public void setUsersRepository(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Autowired
    public void setbCryptPasswordEncoder(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Autowired
    public void setRefreshTokenRepository(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${registration.secret}")
    private String registrationSecret;

    @Value("&{request.handle.secret}")
    private String requestHandleSecret;

    // Сгенерировать refresh-токен
    public String generateRefreshToken(String email, long issue, long expire) {
        Users user = usersRepository.findByEmail(email);
        String rawToken = email + " " + issue + " " + expire;
        try {
            RefreshToken refreshToken = refreshTokenRepository.findByUserID(user.getId());
            if (refreshToken == null) {
                try {
                    refreshToken = new RefreshToken(
                            user.getId(),
                            bCryptPasswordEncoder.encode(rawToken),
                            issue,
                            expire
                    );
                    refreshTokenRepository.save(refreshToken);
                } catch (Exception e) {
                    System.out.println("Обработано исключение вызванное " +
                            "несовершенством токенной библиотеки и не несущее угрозы целостности базе данных");
                }
            } else {
                refreshToken.setRefreshToken(bCryptPasswordEncoder.encode(rawToken));
                refreshToken.setIssue(issue);
                refreshToken.setExpire(expire);
                refreshTokenRepository.save(refreshToken);
            }
            return refreshToken.getRefreshToken();
        } catch (NullPointerException nullPointerException) {
            return null;
        }
    }

    public boolean validateRefreshToken(String token) {
        RefreshToken refreshToken;
        try {
            refreshToken = refreshTokenRepository.findByRefreshToken(token);
        } catch (Exception e) {
            refreshToken = null;
        }
        if (refreshToken != null && (refreshToken.getExpire() - java.time.Instant.now().getEpochSecond() > 0)) {
            return true;
        }
        return false;
    }

    // Сгенерировать access-токен по email
    public String generateAccessToken(String email, long issue, long expire) {
        email = email + " " + issue + " " + expire;
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(Instant.ofEpochSecond(issue)))
                .setExpiration(Date.from(Instant.ofEpochSecond(expire)))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            log.info("Токен успешно прошел валидацию");
            return true;
        } catch (SignatureException e) {
            log.info("Получена некорректная сигнатура токена");
        } catch (MalformedJwtException e) {
            log.info("Получен скомпроментированный токен");
        } catch (ExpiredJwtException e) {
            log.info("Получен просроченный токен");
        } catch (UnsupportedJwtException e) {
            log.info("Полученный не корректный токен");
        } catch (IllegalArgumentException e) {
            log.info("Заголовок или аргументы полученного токена некорректны");
        }
        return false;
    }

    // Получить токен из email
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        if (claims.getSubject().lastIndexOf(' ') == -1)
            return null;
        return claims.getSubject().substring(0, claims.getSubject().indexOf(' '));
    }

    // Токен подтверждения регистрации
    public String generateConfirmToken(int registrationCode) {
        ZonedDateTime dateTime = ZonedDateTime.now();
        String registrationCodeString = registrationCode + "$" + dateTime;;
        Date date = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(registrationCodeString)
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, registrationSecret)
                .compact();
    }

    // Получить код регистрации из токена
    public String getRegistrationCodeFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(registrationSecret).parseClaimsJws(token).getBody();
        if (claims.getSubject().lastIndexOf("$") == -1)
            return null;
        return claims.getSubject().substring(0, claims.getSubject().lastIndexOf("$"));
    }

    // Токен ссылок обработки заявок студента
    public String getStudentRequestHandleToken(String studentRequestIdentifier) {
        ZonedDateTime dateTime = ZonedDateTime.now();
        studentRequestIdentifier = studentRequestIdentifier + "$" + dateTime;
        Date date = Date.from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(studentRequestIdentifier)
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, requestHandleSecret)
                .compact();
    }

    public String getRequestIdentifierFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(requestHandleSecret).parseClaimsJws(token).getBody();
        if (claims.getSubject().lastIndexOf("$") == -1)
            return null;
        return claims.getSubject().substring(0, claims.getSubject().lastIndexOf("$"));
    }
}