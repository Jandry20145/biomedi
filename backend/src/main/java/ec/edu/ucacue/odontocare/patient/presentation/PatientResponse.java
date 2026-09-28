package ec.edu.ucacue.odontocare.patient.presentation;

import java.time.LocalDate;
import java.util.Map;

import ec.edu.ucacue.odontocare.patient.domain.ClinicalBackgroundType;

public record PatientResponse(Long id, Long clinicalRecordId, String clinicalRecordNumber,
        String identification, String responsibleName, String firstNames, String lastNames, LocalDate birthDate, int age,
        String sex, String address, String phone, String email,
        Map<ClinicalBackgroundType, String> backgrounds) {}

