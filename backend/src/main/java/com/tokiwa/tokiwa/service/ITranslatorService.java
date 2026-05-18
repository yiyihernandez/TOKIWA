package com.tokiwa.tokiwa.service;
 
import com.tokiwa.tokiwa.model.TranslationRequest;
import com.tokiwa.tokiwa.model.TranslationResponse;
 
/**
 * POO - Interfaz: define el contrato del servicio de traducción.
 * Establece QUÉ debe hacer el servicio, sin definir CÓMO lo hace.
 * Cualquier clase que implemente esta interfaz está obligada a tener el método traducir().
 */
public interface ITranslatorService {
 
    // Método que toda implementación debe cumplir:
    // recibe una petición y retorna una respuesta de traducción
    TranslationResponse traducir(TranslationRequest request);
}
 