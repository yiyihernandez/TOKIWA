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