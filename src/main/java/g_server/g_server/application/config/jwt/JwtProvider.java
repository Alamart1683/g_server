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
import java.util.Date;

@Log
@Component
public class JwtProvider {
    @Autowired
    UsersRepository usersRepository;

    @Value("$(jwt.secret)")
    private String jwtSecret;

    @Value("$(registration.secret)")
    private String registrationSecret;

    public String generateToken(String email) {
        Date date = Date.from(LocalDate.now().plusDays(15).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String generateConfirmToken(int registrationCode) {
        String registrationCodeString = registrationCode + "";
        Date date = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(registrationCodeString)
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, registrationSecret)
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

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public String getRegistrationCodeFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(registrationSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}