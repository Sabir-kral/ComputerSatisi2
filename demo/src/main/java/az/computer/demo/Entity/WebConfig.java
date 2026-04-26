package az.computer.demo.Entity; // Qeyd: Bu klası 'Config' paketində saxlasan daha yaxşı olar

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Şəkillərin brauzerdə görünməsi üçün
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/app/uploads/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // "*" yerinə konkret frontend ünvanlarını yazırıq
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://127.0.0.1:3000",
                        "http://127.0.0.1:5500",
                        "https://localhost:3000"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // Bu true olanda yuxarıda "*" ola bilməz
    }
}