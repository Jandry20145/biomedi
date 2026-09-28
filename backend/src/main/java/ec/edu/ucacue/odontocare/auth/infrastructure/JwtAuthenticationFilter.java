package ec.edu.ucacue.odontocare.auth.infrastructure;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ec.edu.ucacue.odontocare.auth.application.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String AUTH_ERROR_CODE = "auth.error.code";
    public static final String AUTH_ERROR_MESSAGE = "auth.error.message";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.substring(7);
                String username = jwtService.parse(token).getSubject();
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails user = userDetailsService.loadUserByUsername(username);
                    if (user.isEnabled()) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (ExpiredJwtException exception) {
                request.setAttribute(AUTH_ERROR_CODE, "TOKEN_EXPIRED");
                request.setAttribute(AUTH_ERROR_MESSAGE, "La sesión ha expirado. Inicie sesión nuevamente.");
            } catch (JwtException | IllegalArgumentException exception) {
                request.setAttribute(AUTH_ERROR_CODE, "TOKEN_INVALID");
                request.setAttribute(AUTH_ERROR_MESSAGE, "El token de acceso no es válido.");
            }
        }
        chain.doFilter(request, response);
    }
}

