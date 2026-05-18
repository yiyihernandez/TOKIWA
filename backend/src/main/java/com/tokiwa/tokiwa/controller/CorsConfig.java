package com.tokiwa.tokiwa.controller;
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
 
/**
 * Permite que el frontend se comunique con el backend sin ser bloqueado por el navegador.
 * Sin esta clase, el navegador rechazaría las peticiones por venir de otro origen.
 */
@Configuration // Indica que esta clase tiene configuraciones que Spring debe cargar al inicio
public class CorsConfig {
 
    /**
     * Define y registra las reglas de acceso al backend.
     * Spring lo ejecuta automáticamente al arrancar la aplicación.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
 
        // POO - Herencia 
        // Se crea una clase anónima que implementa la interfaz WebMvcConfigurer.
        // Se sobreescribe addCorsMappings para definir reglas personalizadas de acceso.
        return new WebMvcConfigurer() {
 
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")                                         // Aplica a todas las rutas
                        .allowedOrigins("*")                                       // Acepta peticiones de cualquier origen
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                        .allowedHeaders("*");                                      // Acepta cualquier encabezado
            }
        };
    }
}
 