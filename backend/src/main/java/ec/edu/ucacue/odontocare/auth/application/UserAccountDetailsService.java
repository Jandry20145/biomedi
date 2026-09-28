package ec.edu.ucacue.odontocare.auth.application;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ec.edu.ucacue.odontocare.auth.domain.UserAccount;
import ec.edu.ucacue.odontocare.auth.infrastructure.UserAccountRepository;

@Service
public class UserAccountDetailsService implements UserDetailsService {
    private final UserAccountRepository repository;

    public UserAccountDetailsService(UserAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserAccount user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));
        return new UserPrincipal(user.getId(), user.getUsername(), user.getPasswordHash(),
                user.getNombres(), user.getRole().getNombre());
    }
}

