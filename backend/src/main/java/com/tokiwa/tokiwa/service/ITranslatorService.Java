package com.tokiwa.tokiwa.service;

import com.tokiwa.tokiwa.model.TranslationRequest;
import com.tokiwa.tokiwa.model.TranslationResponse;

/**
 * POO - Interfaz: define el contrato del servicio de traducción.
 * Establece QUÉ debe hacer el servicio, sin definir CÓMO lo hace.
 */
public interface ITranslatorService {
    TranslationResponse traducir(TranslationRequest request);
}