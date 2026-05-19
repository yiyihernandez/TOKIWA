import com.tokiwa.tokiwa.model.TranslationRequest;
import com.tokiwa.tokiwa.model.TranslationResponse;
import com.tokiwa.tokiwa.service.TranslatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api")
public class TranslatorController {

    private final TranslatorService translatorService;

    public TranslatorController(TranslatorService translatorService) {
        this.translatorService = translatorService;
    }

    @PostMapping("/traducir")
    public ResponseEntity<TranslationResponse> traducir(@RequestBody TranslationRequest request) {
        try {
            TranslationResponse response = translatorService.traducir(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}