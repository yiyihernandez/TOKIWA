
package com.tokiwa.tokiwa.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslationResponse {
    private String textoOriginal;
    private String textoTraducido;
    private String explicacion = "";
    private String alternativa = "";
    private double confianza;
    private String idioma;
    private String tono;
}