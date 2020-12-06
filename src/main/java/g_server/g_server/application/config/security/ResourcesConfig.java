package g_server.g_server.application.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class ResourcesConfig implements WebMvcConfigurer {
    @Value("${storage.location}")
    private String storageLocation;

    private File file = new File(storageLocation);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/document/outer/view/**").addResourceLocations(file.getAbsolutePath() + File.separator);
    }
}
