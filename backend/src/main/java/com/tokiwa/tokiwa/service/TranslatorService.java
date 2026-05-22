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

@Service
public class TranslatorService implements ITranslatorService {

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
        "Actua como TOKIWA, experto en traduccion transcultural para espanol, ingles, frances, japones y portugues. " +
        "Tu objetivo es la equivalencia dinamica (sentido y cultura), no la literalidad.\n\n" +
        "ENTRADA:\n" +
        "- Texto original: \"%s\"\n" +
        "- Idioma destino: %s\n" +
        "- Tono: %s\n\n" +
        "REGLAS DE TONO:\n" +
        "- informal: coloquial, natural, puede incluir groserias o expresiones vulgares si el texto original las contiene. " +
        "Usa contracciones y lenguaje cotidiano.\n" +
        "- formal: gramatica perfecta, respetuoso, sustituye groserias por expresiones educadas equivalentes. " +
        "Evita contracciones.\n" +
        "- profesional: lexico corporativo y ejecutivo, elimina cualquier vulgaridad y reformula con lenguaje neutro. " +
        "Orientado a resultados y accion directa.\n\n" +
        "REGLAS CRITICAS:\n" +
        "1. 'traduccion': Texto adaptado al tono indicado en %s. No traduzcas literalmente. " +
        "Respeta el nivel de vulgaridad o formalidad segun el tono. " +
        "Si el texto tiene humor o ironia, intentalo preservar en la traduccion.\n" +
        "2. 'explicacion': Maximo 2 oraciones explicando por que elegiste esas palabras y como adaptaste el tono. " +
        "Escrita OBLIGATORIAMENTE en el mismo idioma del texto original. " +
        "No menciones la alternativa aqui.\n" +
        "3. 'alternativa': OBLIGATORIO, nunca vacio. Segunda traduccion en %s con sinonimos o estructura diferente. " +
        "Si el texto es corto, busca otra expresion equivalente en el idioma destino. " +
        "Mantiene el mismo nivel de tono que la traduccion principal. " +
        "Es una oracion independiente, no va dentro de la explicacion.\n" +
        "4. 'confianza': Numero entero entre 80 y 99 segun la claridad del contexto. " +
        "Si el texto es ambiguo o muy corto, usa un valor mas bajo.\n" +
        "5. Para japones: usa hiragana, katakana y kanji estandar. " +
        "No uses transliteraciones como 'haro' o 'heroo'. " +
        "La traduccion debe ser natural para un hablante nativo japones. " +
        "Usa keigo para formal/profesional y lenguaje casual para informal.\n" +
        "6. Si el texto original no tiene sentido, no es una palabra real o no puede ser traducido con precision, " +
        "responde con este JSON exacto:\n" +
        "{\"traduccion\": \"El texto ingresado no es valido o no tiene significado reconocible.\", " +
        "\"explicacion\": \"El texto no corresponde a ninguna palabra o frase en un idioma reconocido.\", " +
        "\"alternativa\": \"Por favor ingresa un texto con sentido para obtener una traduccion.\", " +
        "\"confianza\": 0}\n\n" +
        "7. Adapta culturalmente segun el idioma destino:\n" +
        "- Ingles: usa ingles americano para informal y britanico para formal.\n" +
        "- Frances: usa 'vous' para formal/profesional y 'tu' para informal.\n" +
        "- Japones: usa keigo para formal/profesional y lenguaje casual para informal.\n" +
        "- Portugues: usa portugues de Brasil como referencia principal.\n" +
        "- Espanol: usa expresiones neutras que se entiendan en cualquier pais hispanohablante.\n\n" +
        "ADVERTENCIA CRITICA: El campo 'alternativa' debe ser UNICAMENTE una cadena de texto simple, " +
        "NO un objeto JSON, NO un diccionario, NO llaves {}. " +
        "Ejemplo correcto: \"alternativa\": \"Otra oracion aqui\" " +
        "Ejemplo INCORRECTO: \"alternativa\": {\"traduccion\": \"...\", \"explicacion\": \"...\"}\n\n" +
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