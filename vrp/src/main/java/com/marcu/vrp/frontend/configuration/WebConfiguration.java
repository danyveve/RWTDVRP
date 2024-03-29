package com.marcu.vrp.frontend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                    .addMapping("/**")
                    .allowCredentials(true)
                    .allowedOrigins("http://localhost:4200",
                        "http://localhost:8080")
                    .allowedMethods("GET", "PUT", "POST", "DELETE");
            }
        };
    }
}
