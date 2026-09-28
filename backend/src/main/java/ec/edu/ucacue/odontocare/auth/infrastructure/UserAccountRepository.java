package ec.edu.ucacue.odontocare.auth.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.ucacue.odontocare.auth.domain.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
}

