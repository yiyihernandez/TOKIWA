package com.tokiwa.tokiwa.model;
 
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
 
/**
 * Representa la respuesta que el backend le devuelve al frontend tras procesar la traducción.
 * Contiene el resultado de la IA junto con información adicional sobre la traducción.
 */
@Data             // POO - Encapsulamiento: genera getters y setters para todos los atributos privados
@AllArgsConstructor // POO - Constructor: genera un constructor con todos los atributos como parámetros
@NoArgsConstructor  // POO - Constructor: genera un constructor vacío, necesario para que Spring pueda crear el objeto
public class TranslationResponse {
 
    private String textoOriginal; // Texto original que envió el usuario
    private String traduccion;    // Traducción generada por la IA
    private String explicacion;   // Explicación del proceso o decisiones de traducción
    private String alternativa;   // Una traducción alternativa sugerida por la IA
    private int confianza;        // Nivel de confianza de la traducción (valor numérico)
    private String idioma;        // Idioma al que se tradujo
    private String tono;          // Tono aplicado en la traducción
}
 