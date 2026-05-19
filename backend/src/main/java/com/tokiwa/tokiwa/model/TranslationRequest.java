import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
package com.tokiwa.tokiwa.model;

@Data
public class TranslationRequest {

    @NotBlank(message = "El texto no puede estar vacío")
    @Size(max = 2000, message = "El texto no puede superar los 2000 caracteres")
    private String texto;

    @NotBlank(message = "El idioma es obligatorio")
    private String idioma;

    private String tono = "neutral";
    private String contexto = "general";
}