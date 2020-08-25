package g_server.g_server.application.config;

import g_server.g_server.application.config.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

                // То, что можно студенту
                .antMatchers(
                        "/student/**",
                        "/student_group/{id}").hasRole("STUDENT")

                // То, что можно научному руководителю
                .antMatchers(
                        "/scientific_advisor/**",
                        "/student_group/save/",
                        "/document/**").hasRole("SCIENTIFIC_ADVISOR")

                // То, что можно зав. кафедрой
                .antMatchers(
                        "/head_of_cathedra/**",
                        "/document/**").hasRole("HEAD_OF_CATHEDRA")

                // То, что можно админу
                .antMatchers(
                        "/admin/**",
                        "/document/**").hasRole("ADMIN")

                // То, что можно всем авторизованным
                .antMatchers(
                        "/document/type/all",
                        "/document/type/{id}").fullyAuthenticated()

                // То, что можно всем
                .antMatchers(
                        "/registration/student",
                        "/authorization",
                        "/student_group/all",
                        "/student_type/all",
                        "/cathedras/all").permitAll()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
