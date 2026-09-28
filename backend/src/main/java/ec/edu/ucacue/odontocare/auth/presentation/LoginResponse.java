package ec.edu.ucacue.odontocare.auth.presentation;

import java.time.Instant;

public record LoginResponse(String accessToken, String tokenType, Instant expiresAt, CurrentUserResponse user) {}

