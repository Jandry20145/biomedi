package ec.edu.ucacue.odontocare.auth.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.ucacue.odontocare.auth.domain.Role;
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNombre(String Nombre);
}

