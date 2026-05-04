package com.tokiwa.tokiwa.controller;

import com.tokiwa.tokiwa.model.TranslationRequest;
import com.tokiwa.tokiwa.model.TranslationResponse;
import com.tokiwa.tokiwa.service.TranslatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TranslatorController {

    @Autowired
    private TranslatorService translatorService;

    @PostMapping("/traducir")
    public TranslationResponse traducir(@RequestBody TranslationRequest request) {
        return translatorService.traducir(request);
    }
}