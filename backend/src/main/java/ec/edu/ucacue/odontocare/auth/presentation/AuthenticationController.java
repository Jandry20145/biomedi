package ec.edu.ucacue.odontocare.auth.presentation;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ucacue.odontocare.auth.application.JwtService;
import ec.edu.ucacue.odontocare.auth.application.UserPrincipal;
import ec.edu.ucacue.odontocare.shared.domain.AuditLog;
import ec.edu.ucacue.odontocare.shared.infrastructure.AuditLogRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditLogRepository auditLogRepository;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtService jwtService, AuditLogRepository auditLogRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.auditLogRepository = auditLogRepository;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        UserPrincipal principal = (UserPrincipal) authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())).getPrincipal();
        
        auditLogRepository.save(new AuditLog(principal.id(), "USER_LOGIN", "User", principal.id(), "{\"username\":\"" + request.username() + "\"}"));
        
        JwtService.IssuedToken token = jwtService.issue(principal);
        return new LoginResponse(token.value(), "Bearer", token.expiresAt(), CurrentUserResponse.from(principal));
    }

    @GetMapping("/me")
    public CurrentUserResponse currentUser(@AuthenticationPrincipal UserPrincipal principal) {
        return CurrentUserResponse.from(principal);
    }
}

