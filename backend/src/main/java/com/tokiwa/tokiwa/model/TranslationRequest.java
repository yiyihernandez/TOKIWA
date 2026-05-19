package com.tokiwa.tokiwa.model;

import lombok.Data;

@Data
public class TranslationRequest {
    private String texto;
    private String idioma;
    private String tono;
    private String contexto;
}