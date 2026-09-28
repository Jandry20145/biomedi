package ec.edu.ucacue.odontocare.dashboard;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {
    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener métricas del panel de control")
    public DashboardMetricsResponse getMetrics() {
        return service.getMetrics();
    }
}
