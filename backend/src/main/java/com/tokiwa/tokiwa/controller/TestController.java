package com.tokiwa.tokiwa.controller;
 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
 
/**
 * Controlador de prueba para verificar que el backend está corriendo correctamente.
 */
@RestController // POO - Clase: indica a Spring que esta clase es un controlador que responde peticiones HTTP
public class TestController {
 
    /**
     * Endpoint de prueba. Al acceder a /test retorna un mensaje de confirmación.
     */
    @GetMapping("/test") // Responde a peticiones GET en la ruta /test
    public String test() {
        return "Tokiwa está funcionando correctamente!";
    }
}