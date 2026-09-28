package ec.edu.ucacue.odontocare.patient.application;

import java.time.LocalDate;
import java.time.Period;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.ucacue.odontocare.auth.application.UserPrincipal;
import ec.edu.ucacue.odontocare.clinicalrecord.domain.ClinicalRecord;
import ec.edu.ucacue.odontocare.clinicalrecord.infrastructure.ClinicalRecordRepository;
import ec.edu.ucacue.odontocare.patient.domain.ClinicalBackground;
import ec.edu.ucacue.odontocare.patient.domain.ClinicalBackgroundType;
import ec.edu.ucacue.odontocare.patient.domain.Patient;
import ec.edu.ucacue.odontocare.patient.infrastructure.ClinicalBackgroundRepository;
import ec.edu.ucacue.odontocare.patient.infrastructure.PatientRepository;
import ec.edu.ucacue.odontocare.patient.presentation.PatientRequest;
import ec.edu.ucacue.odontocare.patient.presentation.PatientResponse;
import jakarta.persistence.criteria.Predicate;

@Service
public class PatientService {
    private final PatientRepository patients;
    private final ClinicalRecordRepository clinicalRecords;
    private final ClinicalBackgroundRepository backgrounds;
    private final ec.edu.ucacue.odontocare.shared.infrastructure.AuditLogRepository auditLogRepository;

    public PatientService(PatientRepository patients, ClinicalRecordRepository clinicalRecords,
            ClinicalBackgroundRepository backgrounds, ec.edu.ucacue.odontocare.shared.infrastructure.AuditLogRepository auditLogRepository) {
        this.patients = patients; this.clinicalRecords = clinicalRecords; this.backgrounds = backgrounds;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public PatientResponse create(PatientRequest request, UserPrincipal user) {
        String recordNumber = request.clinicalRecordNumber() != null && !request.clinicalRecordNumber().isBlank() 
            ? request.clinicalRecordNumber().trim() 
            : null;
        String initialRecordNumber = recordNumber != null ? recordNumber : "TEMP-" + java.util.UUID.randomUUID().toString().substring(0, 10);
        
        validateDuplicates(request, initialRecordNumber, null);
        validateEcuadorianCedula(request.identification());
        
        Patient patient = patients.saveAndFlush(new Patient(initialRecordNumber, request.identification(), request.responsibleName(),
                request.firstNames(), request.lastNames(), request.birthDate(), request.sex(), request.address(),
                request.phone(), request.email(), user.id()));
                
        if (recordNumber == null) {
            Integer maxHce = patients.findMaxHceNumber();
            int next = (maxHce != null ? maxHce : 0) + 1;
            patient.setClinicalRecordNumber(String.format("HCE_%02d", next));
        }
        
        ClinicalRecord record = clinicalRecords.save(new ClinicalRecord(patient, user.id()));
        saveBackgrounds(patient, request.backgrounds(), user.id());
        
        auditLogRepository.save(new ec.edu.ucacue.odontocare.shared.domain.AuditLog(user.id(), "PATIENT_CREATED", "Patient", patient.getId(), "{\"name\":\"" + request.firstNames() + " " + request.lastNames() + "\"}"));
        
        return toResponse(patient, record.getId());
    }

    @Transactional
    public PatientResponse update(Long id, PatientRequest request, UserPrincipal user) {
        Patient patient = requirePatient(id);
        String oldName = patient.getFirstNames() + " " + patient.getLastNames();
        String recordNumber = request.clinicalRecordNumber() != null && !request.clinicalRecordNumber().isBlank() 
            ? request.clinicalRecordNumber().trim() 
            : patient.getClinicalRecordNumber();
        validateDuplicates(request, recordNumber, id);
        validateEcuadorianCedula(request.identification());
        patient.update(recordNumber, request.identification(), request.responsibleName(), request.firstNames(), request.lastNames(),
                request.birthDate(), request.sex(), request.address(), request.phone(), request.email(), user.id());
        saveBackgrounds(patient, request.backgrounds(), user.id());
        
        auditLogRepository.save(new ec.edu.ucacue.odontocare.shared.domain.AuditLog(user.id(), "PATIENT_UPDATED", "Patient", patient.getId(), "{\"name\":\"" + request.firstNames() + " " + request.lastNames() + "\", \"previousValue\":\"" + oldName + "\"}"));
        
        return toResponse(patient, requireRecord(id).getId());
    }

    @Transactional(readOnly = true)
    public PatientResponse get(Long id) { return toResponse(requirePatient(id), requireRecord(id).getId()); }

    @Transactional
    public void deactivate(Long id, UserPrincipal user) { 
        Patient patient = requirePatient(id);
        patient.deactivate(user.id()); 
        patient.update(patient.getClinicalRecordNumber(), 
                       patient.getIdentification() != null ? patient.getIdentification() + "-DEL-" + id : null,
                       patient.getResponsibleName(), 
                       patient.getFirstNames(), 
                       patient.getLastNames(), 
                       patient.getBirthDate(), 
                       patient.getSex(), 
                       patient.getAddress(), 
                       patient.getPhone(), 
                       patient.getEmail() != null ? patient.getEmail() + "-DEL-" + id : null, 
                       user.id());
        auditLogRepository.save(new ec.edu.ucacue.odontocare.shared.domain.AuditLog(user.id(), "PATIENT_DEACTIVATED", "Patient", patient.getId(), "{}"));
    }

    @Transactional(readOnly = true)
    public Page<PatientResponse> search(String recordNumber, String identification, String firstNames,
            String lastNames, String sex, String phone, Long dentistId, Pageable pageable) {
        String formattedRecordNumber = recordNumber != null && recordNumber.toUpperCase().matches("^HCE\\d+$") 
                ? "HCE_" + recordNumber.substring(3)
                : recordNumber;

        Specification<Patient> specification = (root, query, cb) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();
            predicates.add(cb.isTrue(root.get("active")));
            addLike(predicates::add, cb, root.get("clinicalRecordNumber"), formattedRecordNumber);
            addLike(predicates::add, cb, root.get("identification"), identification);
            addLike(predicates::add, cb, root.get("firstNames"), firstNames);
            addLike(predicates::add, cb, root.get("lastNames"), lastNames);
            addLike(predicates::add, cb, root.get("sex"), sex);
            addLike(predicates::add, cb, root.get("phone"), phone);
            if (dentistId != null) {
                predicates.add(cb.equal(root.get("createdBy"), dentistId));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return patients.findAll(specification, pageable).map(patient -> toResponse(patient, requireRecord(patient.getId()).getId()));
    }

    private void addLike(Consumer<Predicate> target, jakarta.persistence.criteria.CriteriaBuilder cb,
            jakarta.persistence.criteria.Path<String> path, String value) {
        if (value != null && !value.isBlank()) target.accept(cb.like(cb.lower(path), "%" + value.trim().toLowerCase() + "%"));
    }

    private void validateDuplicates(PatientRequest request, String recordNumber, Long id) {
        if (request.identification() != null && !request.identification().isBlank()) {
            boolean duplicateId = id == null ? patients.existsByIdentificationIgnoreCase(request.identification().trim())
                    : patients.existsByIdentificationIgnoreCaseAndIdNot(request.identification().trim(), id);
            if (duplicateId) throw new PatientConflictException("Ya existe un paciente con esa identificación.");
        }
        boolean duplicateRecord = id == null ? patients.existsByClinicalRecordNumberIgnoreCase(recordNumber)
                : patients.existsByClinicalRecordNumberIgnoreCaseAndIdNot(recordNumber, id);
        if (duplicateRecord) throw new PatientConflictException("Ya existe un paciente con ese número de historia clínica.");
    }

    private void validateEcuadorianCedula(String cedula) {
        if (cedula == null || cedula.trim().isBlank()) return;
        if (cedula.trim().length() != 10 || !cedula.trim().matches("\\d+")) {
            throw new IllegalArgumentException("La cédula debe tener 10 dígitos numéricos.");
        }
        int prov = Integer.parseInt(cedula.substring(0, 2));
        if (prov < 1 || prov > 24) {
            throw new IllegalArgumentException("Provincia de la cédula no es válida.");
        }
        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int total = 0;
        for (int i = 0; i < 9; i++) {
            int valor = Character.getNumericValue(cedula.charAt(i)) * coeficientes[i];
            if (valor > 9) valor -= 9;
            total += valor;
        }
        int digitoVerificador = Character.getNumericValue(cedula.charAt(9));
        int decenaSuperior = ((total + 9) / 10) * 10;
        int calculado = decenaSuperior - total;
        if (calculado == 10) calculado = 0;
        if (calculado != digitoVerificador) {
            throw new IllegalArgumentException("La cédula ingresada no es válida (Módulo 10).");
        }
    }

    private void saveBackgrounds(Patient patient, Map<ClinicalBackgroundType, String> values, Long userId) {
        if (values == null) return;
        Map<ClinicalBackgroundType, ClinicalBackground> existing = new EnumMap<>(ClinicalBackgroundType.class);
        backgrounds.findByPatientIdAndActiveTrue(patient.getId()).forEach(item -> existing.put(item.getType(), item));
        values.forEach((type, value) -> {
            if (value == null || value.isBlank()) return;
            ClinicalBackground current = existing.get(type);
            if (current == null) backgrounds.save(new ClinicalBackground(patient, type, value, userId));
            else current.updateDescription(value, userId);
        });
    }

    private Patient requirePatient(Long id) { return patients.findById(id).orElseThrow(() -> new PatientNotFoundException(id)); }
    private ClinicalRecord requireRecord(Long id) { return clinicalRecords.findByPatientId(id).orElseThrow(() -> new IllegalStateException("El paciente no tiene historia clínica.")); }

    private PatientResponse toResponse(Patient patient, Long recordId) {
        Map<ClinicalBackgroundType, String> values = new EnumMap<>(ClinicalBackgroundType.class);
        backgrounds.findByPatientIdAndActiveTrue(patient.getId()).forEach(item -> values.put(item.getType(), item.getDescription()));
        int age = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
        return new PatientResponse(patient.getId(), recordId, patient.getClinicalRecordNumber(), patient.getIdentification(),
                patient.getResponsibleName(), patient.getFirstNames(), patient.getLastNames(), patient.getBirthDate(), age, patient.getSex(),
                patient.getAddress(), patient.getPhone(), patient.getEmail(), values);
    }
}

