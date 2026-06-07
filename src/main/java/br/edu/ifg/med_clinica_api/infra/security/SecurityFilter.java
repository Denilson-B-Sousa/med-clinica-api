package br.edu.ifg.med_clinica_api.infra.security;

import br.edu.ifg.med_clinica_api.domain.dao.DoctorRepository;
import br.edu.ifg.med_clinica_api.domain.dao.PatientRepository;
import br.edu.ifg.med_clinica_api.domain.dao.UserRepository;
import br.edu.ifg.med_clinica_api.domain.enums.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public SecurityFilter(TokenService tokenService, UserRepository userRepository, PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {

        var tokenJWT = recoverToken(request);

        if (tokenJWT != null) {
            var subject = tokenService.getSubject(tokenJWT);
            var user = userRepository.findByEmail(subject);

            if (user == null || isInactiveProfile(user.getEmail(), user.getRole())) {
                clearAuthCookie(response);
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sessão Inválida.");
                return;
            }

            var authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {

        if (request.getCookies() == null) {
            return null;
        }

        for (var cookie : request.getCookies()) {
            if("token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private void clearAuthCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ZERO)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private boolean isInactiveProfile(String email, UserRole role) {
        if (role == UserRole.ROLE_PATIENT) {
            return patientRepository.findByUserEmail(email)
                    .map(patient -> !patient.getActive())
                    .orElse(true);
        }

        if(role == UserRole.ROLE_DOCTOR) {
            return doctorRepository.findByUserEmail(email)
                    .map(doctor -> !doctor.getActive())
                    .orElse(true);
        }

        return false;
    }
}
