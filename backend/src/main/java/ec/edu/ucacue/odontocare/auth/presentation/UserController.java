package ec.edu.ucacue.odontocare.auth.presentation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ucacue.odontocare.auth.infrastructure.UserAccountRepository;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserAccountRepository userRepository;

    public UserController(UserAccountRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .filter(user -> "ODONTOLOGO".equalsIgnoreCase(user.getRole().getNombre()))
                .map(user -> new UserDto(user.getId(), user.getNombres()))
                .collect(Collectors.toList());
    }
}
