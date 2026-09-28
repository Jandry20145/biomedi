package ec.edu.ucacue.odontocare.shared.presentation;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    @GetMapping
    @Operation(summary = "Verificar disponibilidad de la API")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "odontocare-api");
    }
}
