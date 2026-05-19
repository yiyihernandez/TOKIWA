package com.tokiwa.tokiwa.service;
 
import java.util.HashMap;
import java.util.Map;
 
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
 
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokiwa.tokiwa.model.TranslationRequest;
import com.tokiwa.tokiwa.model.TranslationResponse;
 
/**
 * Implementación del servicio de traducción de Tokiwa.
 * Es el núcleo del backend: construye el prompt, llama a Ollama y procesa la respuesta.
 *
 * POO - Implementación de interfaz: al escribir "implements ITranslatorService"
 * esta clase cumple el contrato definido en la interfaz y está obligada a tener el método traducir().
 */
@Service // Indica a Spring que esta clase es un servicio y la registra para inyección de dependencia
public class TranslatorService implements ITranslatorService {
 
    // POO - Encapsulamiento: atributos privados, no accesibles desde afuera de la clase
    private final String OLLAMA_URL = "http://localhost:11434/api/generate"; // URL del modelo Ollama
    private final RestTemplate restTemplate = new RestTemplate();            // Realiza las peticiones HTTP a Ollama
    private final ObjectMapper objectMapper = new ObjectMapper();            // Convierte el JSON de respuesta en objetos Java
 
    /**
     * Orquesta el flujo completo de traducción: construir prompt → llamar a Ollama → parsear respuesta.
     */
    public TranslationResponse traducir(TranslationRequest request) {
        String prompt = construirPrompt(request);
        String respuestaJson = llamarOllama(prompt);
        return parsearRespuesta(respuestaJson, request);
    }
 
    /**
     * Construye el prompt que se le envía a la IA.
     * Define el rol de Tokiwa, las reglas de traducción y el formato de respuesta esperado.
     */
    private String construirPrompt(TranslationRequest request) {
        return String.format(
            "Actua como TOKIWA, experto en traduccion transcultural para espanol, ingles, frances, japones y portugues. " +
            "Tu objetivo es la equivalencia dinamica (sentido), no la literalidad.\n\n" +
            "ENTRADA:\n" +
            "- Texto original: \"%s\"\n" +
            "- Idioma destino: %s\n" +
            "- Tono: %s (informal = coloquial y natural; formal = gramatica perfecta y respetuoso; profesional = lexico corporativo y ejecutivo)\n\n" +
            "REGLAS CRITICAS:\n" +
            "1. 'traduccion': Texto adaptado al tono indicado en %s. No traduzcas literalmente.\n" +
            "2. 'explicacion': Maximo 2 oraciones explicando por que elegiste esas palabras. " +
            "Escrita OBLIGATORIAMENTE en el mismo idioma del texto original. " +
            "No menciones la alternativa aqui.\n" +
            "3. 'alternativa': OBLIGATORIO, nunca vacio. Segunda traduccion en %s con sinonimos diferentes a la traduccion principal. " +
            "Es una oracion independiente, no va dentro de la explicacion.\n" +
            "4. 'confianza': Numero entero entre 80 y 99 segun la claridad del contexto.\n" +
            "5. Si el texto original no tiene sentido, no es una palabra real o no puede ser traducido con precision, " +
            "responde con este JSON exacto sin inventar traducciones:\n" +
            "{\"traduccion\": \"El texto ingresado no es valido o no tiene significado reconocible.\", " +
            "\"explicacion\": \"El texto no corresponde a ninguna palabra o frase en un idioma reconocido.\", " +
            "\"alternativa\": \"Por favor ingresa un texto con sentido para obtener una traduccion.\", " +
            "\"confianza\": 0}\n\n" +
            "RESPONDE UNICAMENTE CON ESTE JSON, sin markdown, sin texto extra, sin explicaciones fuera del JSON:\n" +
            "{\"traduccion\": \"\", \"explicacion\": \"\", \"alternativa\": \"\", \"confianza\": 0}",
            request.getTexto(),
            request.getIdioma(),
            request.getTono(),
            request.getIdioma(),
            request.getIdioma()
        );
    }
 
    /**
     * Envía el prompt a Ollama mediante una petición HTTP POST y retorna la respuesta en texto.
     * Si la llamada falla, retorna un JSON de error para que el flujo no se rompa.
     */
    private String llamarOllama(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("ngrok-skip-browser-warning", "true"); // Evita la página de advertencia de ngrok
 
        // POO - Uso de objetos: se construye el cuerpo de la petición como un Map (clave-valor)
        Map<String, Object> body = new HashMap<>();
        body.put("model", "mistral");
        body.put("prompt", prompt);
        body.put("stream", false);
 
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
 
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                OLLAMA_URL, entity, Map.class
            );
            return (String) response.getBody().get("response");
        } catch (Exception e) {
            // Si Ollama no responde, retorna un JSON de error controlado
            return "{\"traduccion\": \"Error\", \"explicacion\": \"" + e.getMessage() + "\", \"alternativa\": \"\", \"confianza\": 0}";
        }
    }
 
    /**
     * Convierte el JSON que retorna Ollama en un objeto TranslationResponse.
     * POO - Uso de constructores: se usa el constructor completo de TranslationResponse
     * para armar el objeto con todos sus atributos de una sola vez.
     */
    private TranslationResponse parsearRespuesta(String json, TranslationRequest request) {
        try {
            Map<String, Object> map = objectMapper.readValue(json.trim(), Map.class);
            return new TranslationResponse(
                request.getTexto(),
                (String) map.get("traduccion"),
                (String) map.get("explicacion"),
                (String) map.get("alternativa"),
                ((Number) map.get("confianza")).intValue(),
                request.getIdioma(),
                request.getTono()
            );
        } catch (Exception e) {
            // Si el JSON no se puede leer, retorna una respuesta con el error visible
            return new TranslationResponse(
                request.getTexto(),
                json,
                "No se pudo parsear la respuesta",
                "",
                0,
                request.getIdioma(),
                request.getTono()
            );
        }
    }
}