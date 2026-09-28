package ec.edu.ucacue.odontocare.auth.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Size(max = 80) String username,
        @NotBlank @Size(max = 120) String password) {}

