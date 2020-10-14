package g_server.g_server.application.config.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// LEGACY CODE
// Новый CORS фильтр переехал в Application

//@Configuration
//@EnableWebMvc
public class  CorsConfig implements WebMvcConfigurer {

    final String webFrontURL = "http://localhost:3000";

    //@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(webFrontURL)
            .allowCredentials(true)
            .allowedHeaders("*")
            .allowedMethods("*");
    }

}