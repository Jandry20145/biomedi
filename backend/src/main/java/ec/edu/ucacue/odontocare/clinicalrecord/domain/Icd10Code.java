package ec.edu.ucacue.odontocare.clinicalrecord.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "icd10_codes")
public class Icd10Code {

    @Id
    private String code;

    private String description;

    protected Icd10Code() {}

    public Icd10Code(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
