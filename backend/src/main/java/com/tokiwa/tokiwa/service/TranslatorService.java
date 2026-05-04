package com.tokiwa.tokiwa.service;

import com.tokiwa.tokiwa.model.TranslationRequest;
import com.tokiwa.tokiwa.model.TranslationResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.HashMap;

@Service
public class TranslatorService {

    private final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TranslationResponse traducir(TranslationRequest request) {
        String prompt = construirPrompt(request);
        String respuestaJson = llamarOllama(prompt);
        return parsearRespuesta(respuestaJson, request);
    }

    private String construirPrompt(TranslationRequest request) {
        return String.format(
            "Eres TOKIWA, un traductor contextual inteligente. No traduces palabra por palabra, " +
            "adaptas el significado segun el contexto y el tono indicado. Datos de entrada:\n\n" +
            "* Texto original: \"%s\"\n" +
            "* Idioma destino: \"%s\" (espanol, ingles, frances, japones o portugues)\n" +
            "* Tono: \"%s\" (formal = educado y respetuoso, informal = relajado y cercano, " +
            "profesional = directo, breve y orientado a accion)\n\n" +
            "REGLA CRITICA: el campo 'alternativa' es una segunda traduccion obligatoria con vocabulario " +
            "diferente a la traduccion principal. Ejemplo: traduccion = 'Hola, recibiste lo que te mande?' " +
            "y alternativa = 'Oye, te llego lo que te envie?'. Son dos oraciones distintas, ninguna va " +
            "dentro de la explicacion. El campo confianza es un numero entero entre 80 y 99. " +
            "Responde UNICAMENTE con este JSON, sin texto adicional, sin markdown:\n" +
            "{\"traduccion\": \"\", \"explicacion\": \"\", \"alternativa\": \"\", \"confianza\": 0}",
            request.getTexto(),
            request.getIdioma(),
            request.getTono()
        );
    }

    private String llamarOllama(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

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
            return "{\"traduccion\": \"Error\", \"explicacion\": \"" + e.getMessage() + "\", \"alternativa\": \"\", \"confianza\": 0}";
        }
    }

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