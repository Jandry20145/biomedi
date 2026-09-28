package ec.edu.ucacue.odontocare.auth.domain;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Personal")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_rol", nullable = false)
    private Role role;
    @Column(name = "usuario", nullable = false, unique = true, length = 80)
    private String username;
    @Column(name = "clave", nullable = false, length = 100)
    private String passwordHash;
    @Column(name = "nombres", nullable = false, length = 120)
    private String nombres;

    protected UserAccount() {
    }

    public UserAccount(Role role, String username, String passwordHash, String nombres) {
        this.role = role;
        this.username = username;
        this.passwordHash = passwordHash;
        this.nombres = nombres;
    }

    public Long getId() { return id; }
    public Role getRole() { return role; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getNombres() { return nombres; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}

