package com.tokiwa.tokiwa;
 
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
/**
 * Clase principal de Tokiwa. Es el punto de entrada de toda la aplicación.
 * Cuando se ejecuta, Spring Boot arranca el servidor y carga todas las clases
 * anotadas con @Service, @RestController, @Configuration, etc.
 */
@SpringBootApplication // Activa la configuración automática de Spring Boot y escanea todas las clases del proyecto
public class TokiwaApplication {
 
    // POO - Método principal: el método main es el punto de arranque de cualquier programa Java
    public static void main(String[] args) {
        SpringApplication.run(TokiwaApplication.class, args); // Inicia el servidor con esta clase como base
    }
}
 