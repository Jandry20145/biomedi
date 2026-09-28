package ec.edu.ucacue.odontocare.patient.application;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(Long id) { super("No existe el paciente con identificador " + id + "."); }
}

