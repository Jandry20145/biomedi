package ec.edu.ucacue.odontocare.auth.infrastructure;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        String code = (String) request.getAttribute(JwtAuthenticationFilter.AUTH_ERROR_CODE);
        String message = (String) request.getAttribute(JwtAuthenticationFilter.AUTH_ERROR_MESSAGE);
        if (code == null) {
            code = "AUTHENTICATION_REQUIRED";
            message = "Debe iniciar sesión para acceder a este recurso.";
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().printf("{\"status\":401,\"code\":\"%s\",\"message\":\"%s\"}", code, message);
    }
}

