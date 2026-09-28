package ec.edu.ucacue.odontocare.dashboard;

import java.util.List;
import ec.edu.ucacue.odontocare.patient.presentation.PatientResponse;
import ec.edu.ucacue.odontocare.clinicalrecord.presentation.ConsultationSummary;

public record DashboardMetricsResponse(
    long totalPatients,
    long todayConsultations,
    long currentMonthConsultations,
    long generatedReports,
    List<ConsultationSummary> todayAgenda,
    List<PatientResponse> recentPatients,
    List<TreatmentDistributionData> treatmentDistribution,
    List<MonthlyConsultationData> monthlyConsultations
) {
    public record TreatmentDistributionData(String name, long value) {}
    public record MonthlyConsultationData(String name, long consultas) {}
}
