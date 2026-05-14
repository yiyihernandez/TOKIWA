package com.tokiwa.tokiwa.controller;

import com.tokiwa.tokiwa.model.TranslationRequest;
import com.tokiwa.tokiwa.model.TranslationResponse;
import com.tokiwa.tokiwa.service.TranslatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class TranslatorController {

    @Autowired
    private TranslatorService translatorService;

    @PostMapping("/traducir")
    public ResponseEntity<TranslationResponse> traducir(@RequestBody TranslationRequest request) {
        try {
            if (request.getTexto() == null || request.getTexto().isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            TranslationResponse response = translatorService.traducir(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}