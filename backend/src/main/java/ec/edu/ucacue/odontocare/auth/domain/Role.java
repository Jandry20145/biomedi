package ec.edu.ucacue.odontocare.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Roles")
public class Role {
    @Id 
    private Long id;

    @Column(name = "Nombre")
    private String nombre;

    protected Role() {
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
}

