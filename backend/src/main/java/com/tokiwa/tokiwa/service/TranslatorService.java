package com.tokiwa.tokiwa.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokiwa.tokiwa.model.TranslationRequest;
import com.tokiwa.tokiwa.model.TranslationResponse;

@Service
public class TranslatorService implements ITranslatorService {

    @Value("${ia.api.url}")
    private String ollamaUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public TranslationResponse traducir(TranslationRequest request) {
        String prompt = construirPrompt(request);
        String respuestaJson = llamarOllama(prompt);
        return parsearRespuesta(respuestaJson, request);
    }

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

    private String llamarOllama(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("ngrok-skip-browser-warning", "true");

        Map<String, Object> body = new HashMap<>();
        body.put("model", "mistral");
        body.put("prompt", prompt);
        body.put("stream", false);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                ollamaUrl, entity, Map.class
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