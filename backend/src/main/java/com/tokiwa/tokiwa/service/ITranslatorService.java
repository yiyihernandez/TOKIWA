package com.tokiwa.tokiwa.service;

import com.tokiwa.tokiwa.model.TranslationRequest;
import com.tokiwa.tokiwa.model.TranslationResponse;

public interface ITranslatorService {
    TranslationResponse traducir(TranslationRequest request);
}