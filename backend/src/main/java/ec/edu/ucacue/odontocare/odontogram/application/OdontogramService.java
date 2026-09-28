package ec.edu.ucacue.odontocare.odontogram.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import ec.edu.ucacue.odontocare.auth.application.UserPrincipal;
import ec.edu.ucacue.odontocare.clinicalrecord.application.ClinicalRecordNotFoundException;
import ec.edu.ucacue.odontocare.clinicalrecord.application.ConsultationNotFoundException;
import ec.edu.ucacue.odontocare.clinicalrecord.infrastructure.ClinicalRecordRepository;
import ec.edu.ucacue.odontocare.clinicalrecord.infrastructure.DentalConsultationRepository;
import ec.edu.ucacue.odontocare.odontogram.domain.DentalSurface;
import ec.edu.ucacue.odontocare.odontogram.domain.OdontogramEvent;
import ec.edu.ucacue.odontocare.odontogram.infrastructure.DentalPieceRepository;
import ec.edu.ucacue.odontocare.odontogram.infrastructure.OdontogramEventRepository;
import ec.edu.ucacue.odontocare.odontogram.infrastructure.OdontogramStatusRepository;
import ec.edu.ucacue.odontocare.odontogram.infrastructure.OdontogramSurfaceRepository;
import ec.edu.ucacue.odontocare.odontogram.presentation.CurrentOdontogramPieceResponse;
import ec.edu.ucacue.odontocare.odontogram.presentation.OdontogramEventRequest;
import ec.edu.ucacue.odontocare.odontogram.presentation.OdontogramEventResponse;

@Service
public class OdontogramService {
    private final ClinicalRecordRepository records;
    private final DentalConsultationRepository consultations;
    private final DentalPieceRepository pieces;
    private final OdontogramStatusRepository statuses;
    private final OdontogramSurfaceRepository surfaces;
    private final OdontogramEventRepository events;
    private final OdontogramMapper mapper;
    private final Clock clock;

    @Autowired
    public OdontogramService(ClinicalRecordRepository records, DentalConsultationRepository consultations,
            DentalPieceRepository pieces, OdontogramStatusRepository statuses,
            OdontogramSurfaceRepository surfaces, OdontogramEventRepository events) {
        this(records, consultations, pieces, statuses, surfaces, events, new OdontogramMapper(), Clock.systemDefaultZone());
    }

    OdontogramService(ClinicalRecordRepository records, DentalConsultationRepository consultations,
            DentalPieceRepository pieces, OdontogramStatusRepository statuses,
            OdontogramSurfaceRepository surfaces, OdontogramEventRepository events,
            OdontogramMapper mapper, Clock clock) {
        this.records = records; this.consultations = consultations; this.pieces = pieces; this.statuses = statuses;
        this.surfaces = surfaces; this.events = events; this.mapper = mapper; this.clock = clock;
    }

    @Transactional
    public OdontogramEventResponse create(Long recordId, Long consultationId, OdontogramEventRequest request,
            UserPrincipal user) {
        requireRecord(recordId);
        var consultation = consultations.findByIdAndClinicalRecordId(consultationId, recordId)
                .orElseThrow(() -> new ConsultationNotFoundException(consultationId));
        var piece = pieces.findByInternalCodeAndActiveTrue(request.pieceCode())
                .orElseThrow(() -> new OdontogramException("ODONTOGRAM_PIECE_NOT_FOUND",
                        "La pieza " + request.pieceCode() + " no existe en el catálogo activo."));
        var status = statuses.findByCodeAndActiveTrue(request.status().catalogCode())
                .orElseThrow(() -> new OdontogramException("ODONTOGRAM_STATUS_INVALID", "El estado no está activo."));
        var requestedSurfaceCodes = request.surfaces().stream().map(DentalSurface::catalogCode).toList();
        var foundSurfaces = surfaces.findByCodeInAndActiveTrue(requestedSurfaceCodes);
        if (foundSurfaces.size() != new LinkedHashSet<>(requestedSurfaceCodes).size()) {
            throw new OdontogramException("ODONTOGRAM_SURFACE_INVALID", "Una o más superficies no están activas.");
        }
        var eventDate = request.eventDate() == null ? LocalDateTime.now(clock) : request.eventDate();
        if (eventDate.isAfter(LocalDateTime.now(clock))) {
            throw new OdontogramException("ODONTOGRAM_FUTURE_DATE", "La fecha del evento no puede ser futura.");
        }
        var event = new OdontogramEvent(consultation, piece, status, request.observations(), eventDate,
                user.id(), new LinkedHashSet<>(foundSurfaces));
        return mapper.toResponse(events.save(event));
    }

    @Transactional(readOnly = true)
    public List<CurrentOdontogramPieceResponse> current(Long recordId) {
        requireRecord(recordId);
        var catalog = requireCatalog();
        Map<String, OdontogramEvent> latest = new LinkedHashMap<>();
        events.findByConsultationClinicalRecordIdOrderByEventDateAscIdAsc(recordId)
                .forEach(event -> latest.put(event.getPiece().getInternalCode(), event));
        return catalog.stream().map(piece -> mapper.toCurrentPiece(piece, latest.get(piece.getInternalCode()))).toList();
    }

    @Transactional(readOnly = true)
    public List<OdontogramEventResponse> byConsultation(Long recordId, Long consultationId) {
        requireRecord(recordId);
        consultations.findByIdAndClinicalRecordId(consultationId, recordId)
                .orElseThrow(() -> new ConsultationNotFoundException(consultationId));
        return events.findByConsultationIdOrderByEventDateAscIdAsc(consultationId).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<OdontogramEventResponse> pieceHistory(Long recordId, String pieceCode) {
        requireRecord(recordId);
        pieces.findByInternalCodeAndActiveTrue(pieceCode)
                .orElseThrow(() -> new OdontogramException("ODONTOGRAM_PIECE_NOT_FOUND",
                        "La pieza " + pieceCode + " no existe en el catálogo activo."));
        return events.findByConsultationClinicalRecordIdAndPieceInternalCodeOrderByEventDateAscIdAsc(recordId, pieceCode)
                .stream().map(mapper::toResponse).toList();
    }

    private void requireRecord(Long recordId) {
        if (!records.existsById(recordId)) throw new ClinicalRecordNotFoundException(recordId);
    }

    private List<ec.edu.ucacue.odontocare.odontogram.domain.DentalPiece> requireCatalog() {
        var catalog = pieces.findByActiveTrueOrderByVisualOrderAsc();
        if (catalog.size() != 36) throw new OdontogramException("ODONTOGRAM_CATALOG_INTEGRITY",
                "El catálogo renderizable debe contener exactamente 36 piezas activas.");
        return catalog;
    }
}
