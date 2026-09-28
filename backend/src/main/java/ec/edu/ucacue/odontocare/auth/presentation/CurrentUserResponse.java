package ec.edu.ucacue.odontocare.auth.presentation;

import ec.edu.ucacue.odontocare.auth.application.UserPrincipal;
public record CurrentUserResponse(Long id, String username, String nombres, String role) {
    static CurrentUserResponse from(UserPrincipal principal) {
        return new CurrentUserResponse(principal.id(), principal.username(), principal.nombres(),
                principal.role().toUpperCase());
    }
}

