package ec.edu.ucacue.odontocare.odontogram.presentation;

import java.time.LocalDateTime;
import java.util.List;
import ec.edu.ucacue.odontocare.odontogram.domain.DentalSurface;
import ec.edu.ucacue.odontocare.odontogram.domain.OdontogramStatus;

public record OdontogramEventResponse(Long id, Long consultationId, String pieceCode,
        OdontogramStatus status, List<DentalSurface> surfaces, String observations,
        LocalDateTime eventDate, Long createdBy) {}
