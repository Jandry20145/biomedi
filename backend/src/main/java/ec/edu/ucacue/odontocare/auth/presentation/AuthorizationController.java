package ec.edu.ucacue.odontocare.auth.presentation;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/permissions")
public class AuthorizationController {
    @GetMapping("/clinical-write")
    @PreAuthorize("hasRole('ODONTOLOGO')")
    public Map<String, Object> clinicalWritePermission() {
        return Map.of("permission", "CLINICAL_WRITE", "allowed", true);
    }
}

