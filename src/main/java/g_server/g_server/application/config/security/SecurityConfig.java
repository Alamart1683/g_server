package g_server.g_server.application.config.security;

import g_server.g_server.application.config.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtFilter jwtFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // То, что можно студенту
                .antMatchers(
                        "/student/**",
                        "/student_group/{id}").hasRole("STUDENT")

                // То, что можно научному руководителю
                .antMatchers(
                        "/scientific_advisor/**",
                        "/student_group/save/").hasAnyRole("SCIENTIFIC_ADVISOR", "HEAD_OF_CATHEDRA")

                // То, что можно только зав. кафедрой
                .antMatchers(
                        "/head_of_cathedra/**").hasRole("HEAD_OF_CATHEDRA")

                // То, что можно админу и руту
                .antMatchers(
                        "/admin/**").hasAnyRole("ADMIN", "ROOT")

                // То, что можно только руту
                .antMatchers("/root/**").hasRole("ROOT")

                // То, что можно всем авторизованным
                .antMatchers("/document/**").hasAnyRole("STUDENT", "SCIENTIFIC_ADVISOR",
                      "HEAD_OF_CATHEDRA", "ADMIN", "ROOT")

                // То, что можно всем
                .antMatchers(
                        "/registration/student",
                        "/authorization",
                        "/student_group/all",
                        "/student_type/all",
                        "/cathedras/all",
                        "/registration/student/mail",
                        "/registration/mail/check/**",
                        "/mail/request/handle/",
                        "/date/all").permitAll()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}