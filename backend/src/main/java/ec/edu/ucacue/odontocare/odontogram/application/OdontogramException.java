package ec.edu.ucacue.odontocare.odontogram.application;

public class OdontogramException extends RuntimeException {
    private final String code;
    public OdontogramException(String code, String message) { super(message); this.code = code; }
    public String getCode() { return code; }
}
