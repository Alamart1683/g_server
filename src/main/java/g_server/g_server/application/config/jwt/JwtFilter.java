package g_server.g_server.application.config.jwt;

import g_server.g_server.application.service.users.UsersService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.springframework.util.StringUtils.hasText;

@Component
@Log
public class JwtFilter extends GenericFilterBean {
    public static final String AUTHORIZATION = "Authorization";

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UsersService usersService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (((HttpServletRequest)servletRequest));
        String token = getTokenFromRequest((HttpServletRequest) servletRequest);
        if (httpServletRequest.getRequestURI().contains("prolongation")) {
            log.info("Обработан запрос пролонгации");
        }
        else if (httpServletRequest.getRequestURI().contains("authorization")) {
            log.info("Обработан запрос авторизации");
        }
        else if (token != null && jwtProvider.validateAccessToken(token)) {
            String email = jwtProvider.getEmailFromToken(token);
            UsernamePasswordAuthenticationToken auth = null;
            if (usersService.loadUserByUsername(email) != null) {
                auth = new UsernamePasswordAuthenticationToken(usersService.loadUserByUsername(email), null, usersService.loadUserByUsername(email).getAuthorities());
            }
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
