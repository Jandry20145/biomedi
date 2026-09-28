package ec.edu.ucacue.odontocare.odontogram.application;

import java.util.Comparator;
import ec.edu.ucacue.odontocare.odontogram.domain.DentalPiece;
import ec.edu.ucacue.odontocare.odontogram.domain.DentalSurface;
import ec.edu.ucacue.odontocare.odontogram.domain.OdontogramEvent;
import ec.edu.ucacue.odontocare.odontogram.domain.OdontogramStatus;
import ec.edu.ucacue.odontocare.odontogram.presentation.CurrentOdontogramPieceResponse;
import ec.edu.ucacue.odontocare.odontogram.presentation.OdontogramEventResponse;

public class OdontogramMapper {
    public OdontogramEventResponse toResponse(OdontogramEvent event) {
        var surfaces = event.getSurfaces().stream().map(surface -> DentalSurface.fromCatalogCode(surface.getCode()))
                .sorted(Comparator.comparing(Enum::ordinal)).toList();
        return new OdontogramEventResponse(event.getId(), event.getConsultation().getId(),
                event.getPiece().getInternalCode(), OdontogramStatus.fromCatalogCode(event.getStatus().getCode()),
                surfaces, event.getObservations(), event.getEventDate(), event.getCreatedBy());
    }

    public CurrentOdontogramPieceResponse toCurrentPiece(DentalPiece piece, OdontogramEvent current) {
        return new CurrentOdontogramPieceResponse(piece.getInternalCode(), piece.getPositionCode(), piece.getArch(),
                piece.getSide(), piece.getPieceType(), piece.getVisualOrder(), piece.getModelNodeName(),
                piece.getFdiCode(), current == null ? null : toResponse(current));
    }
}
