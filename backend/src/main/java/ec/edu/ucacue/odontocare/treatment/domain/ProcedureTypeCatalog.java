package ec.edu.ucacue.odontocare.treatment.domain;
import jakarta.persistence.*;
@Entity @Table(name="procedure_type_catalog") public class ProcedureTypeCatalog{@Id @GeneratedValue(strategy=GenerationType.IDENTITY)private Long id;private String code;private String name;private boolean active;protected ProcedureTypeCatalog(){}public Long getId(){return id;}public String getCode(){return code;}public String getName(){return name;}}
