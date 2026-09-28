package ec.edu.ucacue.odontocare.odontogram.presentation;

import java.time.LocalDateTime;
import java.util.Set;
import ec.edu.ucacue.odontocare.odontogram.domain.DentalSurface;
import ec.edu.ucacue.odontocare.odontogram.domain.OdontogramStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record OdontogramEventRequest(
        @NotBlank @Size(max = 20) String pieceCode,
        @NotNull OdontogramStatus status,
        @NotEmpty Set<@NotNull DentalSurface> surfaces,
        @Size(max = 4000) String observations,
        @PastOrPresent LocalDateTime eventDate) {}
