package ec.edu.ucacue.odontocare.odontogram.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "odontogram_surfaces")
public class OdontogramSurface {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String code;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private boolean active;
    protected OdontogramSurface() {}
    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public boolean isActive() { return active; }
}
