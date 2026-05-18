package com.tokiwa.tokiwa.model;
 
import lombok.Data;
 
/**
 * Representa la petición que envía el frontend al backend para traducir un texto.
 * Contiene los datos necesarios para que la IA procese la traducción.
 */
@Data // POO - Encapsulamiento: Lombok genera automáticamente los getters, setters,
      // equals, hashCode y toString para todos los atributos privados de la clase.
public class TranslationRequest {
 
    private String texto;    // Texto que el usuario quiere traducir
    private String idioma;   // Idioma al que se desea traducir
    private String tono;     // Tono de la traducción: formal, informal, profesional
    private String contexto; // Contexto adicional para que la IA traduzca con mayor precisión
}
 