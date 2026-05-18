package com.tokiwa.tokiwa.controller;
 
import com.tokiwa.tokiwa.model.TranslationRequest;
import com.tokiwa.tokiwa.model.TranslationResponse;
import com.tokiwa.tokiwa.service.TranslatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
/**
 * Controlador principal del traductor.
 * Recibe la petición del frontend, la valida y la envía al servicio de traducción.
 */
@RestController             // POO - Clase: Spring la reconoce como controlador que maneja peticiones HTTP
@CrossOrigin(origins = "*") // Permite peticiones desde cualquier origen (complementa CorsConfig)
@RequestMapping("/api")     // Todas las rutas de este controlador parten de /api
public class TranslatorController {
 
    // POO - Atributo + Inyección de dependencia:
    // En lugar de crear el servicio manualmente con "new", Spring lo inyecta automáticamente.
    // Esto desacopla el controlador del servicio — uno no depende directamente del otro.
    @Autowired
    private TranslatorService translatorService;
 
    /**
     * Endpoint principal del proyecto. Recibe el texto a traducir y retorna la respuesta de la IA.
     * Incluye validación del texto y manejo de errores.
     */
    @PostMapping("/traducir") // Responde a peticiones POST en /api/traducir
    public ResponseEntity<TranslationResponse> traducir(@RequestBody TranslationRequest request) {
        try {
            // Validación: si el texto viene vacío o nulo, retorna error 400
            if (request.getTexto() == null || request.getTexto().isBlank()) {
                return ResponseEntity.badRequest().build();
            }
 
            // POO - Uso de métodos: se llama al método traducir() del servicio con el objeto request
            TranslationResponse response = translatorService.traducir(request);
            return ResponseEntity.ok(response); // Retorna la respuesta con estado 200
 
        } catch (Exception e) {
            // Si ocurre cualquier error inesperado, retorna estado 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}