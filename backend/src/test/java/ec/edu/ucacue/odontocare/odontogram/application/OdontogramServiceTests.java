package ec.edu.ucacue.odontocare.odontogram.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import ec.edu.ucacue.odontocare.clinicalrecord.infrastructure.ClinicalRecordRepository;
import ec.edu.ucacue.odontocare.clinicalrecord.infrastructure.DentalConsultationRepository;
import ec.edu.ucacue.odontocare.odontogram.domain.DentalPiece;
import ec.edu.ucacue.odontocare.odontogram.infrastructure.*;

class OdontogramServiceTests {
    private final ClinicalRecordRepository records = mock(ClinicalRecordRepository.class);
    private final DentalConsultationRepository consultations = mock(DentalConsultationRepository.class);
    private final DentalPieceRepository pieces = mock(DentalPieceRepository.class);
    private final OdontogramStatusRepository statuses = mock(OdontogramStatusRepository.class);
    private final OdontogramSurfaceRepository surfaces = mock(OdontogramSurfaceRepository.class);
    private final OdontogramEventRepository events = mock(OdontogramEventRepository.class);
    private final OdontogramService service = new OdontogramService(records, consultations, pieces, statuses, surfaces, events);

    @Test void currentProjectionAlwaysRequiresExactlyThirtySixActivePieces() {
        when(records.existsById(1L)).thenReturn(true);
        when(pieces.findByActiveTrueOrderByVisualOrderAsc()).thenReturn(List.of(mock(DentalPiece.class)));
        var error = assertThrows(OdontogramException.class, () -> service.current(1L));
        assertEquals("ODONTOGRAM_CATALOG_INTEGRITY", error.getCode());
    }

    @Test void currentProjectionReturnsThirtySixRenderSlotsWithoutEvents() {
        when(records.existsById(1L)).thenReturn(true);
        List<DentalPiece> catalog = IntStream.rangeClosed(1, 36).mapToObj(number -> {
            DentalPiece piece = mock(DentalPiece.class);
            when(piece.getInternalCode()).thenReturn("TOOTH_%02d".formatted(number));
            when(piece.getVisualOrder()).thenReturn((short) number);
            return piece;
        }).toList();
        when(pieces.findByActiveTrueOrderByVisualOrderAsc()).thenReturn(catalog);
        when(events.findByConsultationClinicalRecordIdOrderByEventDateAscIdAsc(1L)).thenReturn(List.of());
        assertEquals(36, service.current(1L).size());
    }
}
