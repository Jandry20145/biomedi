package ec.edu.ucacue.odontocare.clinicalrecord.application;
public class ClinicalLifecycleException extends RuntimeException {
 private final String code;
 public ClinicalLifecycleException(String code,String message){super(message);this.code=code;}
 public String getCode(){return code;}
}
