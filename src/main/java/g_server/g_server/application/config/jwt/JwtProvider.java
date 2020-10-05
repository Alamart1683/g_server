package g_server.g_server.application.config.jwt;

import g_server.g_server.application.repository.users.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Log
@Component
public class JwtProvider {
    @Autowired
    UsersRepository usersRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${registration.secret}")
    private String registrationSecret;

    @Value("&{request.handle.secret}")
    private String requestHandleSecret;

    // Сгенерировать токен по email
    public String generateToken(String email) {
        ZonedDateTime dateTime = ZonedDateTime.now();
        email = email + "$" + dateTime;
        Date date = Date.from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.severe("invalid token");
        }
        return false;
    }

    // Получить токен из email
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        if (claims.getSubject().lastIndexOf("$") == -1)
            return null;
        return claims.getSubject().substring(0, claims.getSubject().lastIndexOf("$"));
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