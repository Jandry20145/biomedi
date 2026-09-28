package ec.edu.ucacue.odontocare.dashboard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import ec.edu.ucacue.odontocare.clinicalrecord.domain.DentalConsultation;
import ec.edu.ucacue.odontocare.clinicalrecord.infrastructure.DentalConsultationRepository;
import ec.edu.ucacue.odontocare.clinicalrecord.presentation.ConsultationSummary;
import ec.edu.ucacue.odontocare.odontogram.infrastructure.OdontogramEventRepository;
import ec.edu.ucacue.odontocare.patient.infrastructure.PatientRepository;
import ec.edu.ucacue.odontocare.patient.presentation.PatientResponse;
import ec.edu.ucacue.odontocare.shared.infrastructure.AuditLogRepository;

@Service
public class DashboardService {
    private final PatientRepository patientRepo;
    private final DentalConsultationRepository consultationRepo;
    private final OdontogramEventRepository eventRepo;
    private final AuditLogRepository auditLogRepo;

    public DashboardService(PatientRepository patientRepo,
            DentalConsultationRepository consultationRepo,
            OdontogramEventRepository eventRepo,
            AuditLogRepository auditLogRepo) {
        this.patientRepo = patientRepo;
        this.consultationRepo = consultationRepo;
        this.eventRepo = eventRepo;
        this.auditLogRepo = auditLogRepo;
    }

    public DashboardMetricsResponse getMetrics() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime startOfYear = LocalDate.now().withDayOfYear(1).atStartOfDay();
        LocalDateTime endOfYear = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear()).atTime(LocalTime.MAX);
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);

        long totalPatients = patientRepo.countByActiveTrue();
        long todayConsultations = consultationRepo.countByConsultationDateBetween(startOfDay, endOfDay);
        long currentMonthConsultations = consultationRepo.countByConsultationDateBetween(startOfMonth, endOfMonth);
        long generatedReports = auditLogRepo.countByAction("REPORT_GENERATED");

        List<DentalConsultation> todayAgendaRaw = consultationRepo.findByConsultationDateBetweenOrderByConsultationDateAsc(startOfDay, endOfDay);
        List<ConsultationSummary> todayAgenda = todayAgendaRaw.stream().map(c -> new ConsultationSummary(
            c.getId(), c.getConsultationDate(), c.getConsultationType(),
            c.getLifecycleStatus().name(), c.getReason(), null // primaryDiagnosis not needed for dashboard
        )).toList();

        var recentPatientsRaw = patientRepo.findTop5ByActiveTrueOrderByIdDesc();
        List<PatientResponse> recentPatients = recentPatientsRaw.stream().map(p -> new PatientResponse(
            p.getId(), null, p.getClinicalRecordNumber(), p.getIdentification(), null, p.getFirstNames(),
            p.getLastNames(), p.getBirthDate(),
            java.time.Period.between(p.getBirthDate(), java.time.LocalDate.now()).getYears(),
            p.getSex(), null, null, null, null
        )).toList();

        List<Object[]> eventsRaw = eventRepo.countEventsByStatus();
        List<DashboardMetricsResponse.TreatmentDistributionData> treatmentDistribution = new ArrayList<>();
        for (Object[] row : eventsRaw) {
            String name = (String) row[0];
            long value = ((Number) row[1]).longValue();
            treatmentDistribution.add(new DashboardMetricsResponse.TreatmentDistributionData(name, value));
        }

        List<DentalConsultation> yearConsultations = consultationRepo.findByConsultationDateBetween(startOfYear, endOfYear);
        long[] monthCounts = new long[12];
        for (DentalConsultation c : yearConsultations) {
            int monthIndex = c.getConsultationDate().getMonthValue() - 1;
            monthCounts[monthIndex]++;
        }
        
        List<DashboardMetricsResponse.MonthlyConsultationData> monthlyConsultations = new ArrayList<>();
        Locale esLocale = new Locale("es", "ES");
        for (int i = 0; i < 12; i++) {
            String monthName = java.time.Month.of(i + 1).getDisplayName(TextStyle.SHORT, esLocale);
            monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
            monthlyConsultations.add(new DashboardMetricsResponse.MonthlyConsultationData(monthName, monthCounts[i]));
        }

        return new DashboardMetricsResponse(
            totalPatients,
            todayConsultations,
            currentMonthConsultations,
            generatedReports,
            todayAgenda,
            recentPatients,
            treatmentDistribution,
            monthlyConsultations
        );
    }
}
