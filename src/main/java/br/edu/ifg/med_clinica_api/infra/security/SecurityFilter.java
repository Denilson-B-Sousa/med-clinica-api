package br.edu.ifg.med_clinica_api.infra.security;

import br.edu.ifg.med_clinica_api.model.dao.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    private final UserRepository userRepository;

    public SecurityFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {

        var tokenJWT = recoverToken(request);

        System.out.println("URI: " + request.getRequestURI());
        System.out.println("Authorization header: " + request.getHeader("Authorization"));
        System.out.println("Token recuperado: " + tokenJWT);

        if (tokenJWT != null) {
            var subject = tokenService.getSubject(tokenJWT);
            var user = userRepository.findByEmail(subject);


            System.out.println("Subject do token: " + subject);
            System.out.println("Usuário encontrado: " + (user != null ? user.getUsername() : "null"));
            System.out.println("Authorities: " + (user != null ? user.getAuthorities() : "null"));

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
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        return authorizationHeader.replace("Bearer ", "");
    }
}
