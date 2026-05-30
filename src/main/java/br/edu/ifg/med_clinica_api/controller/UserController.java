package br.edu.ifg.med_clinica_api.controller;

import br.edu.ifg.med_clinica_api.domain.bo.ProfileService;
import br.edu.ifg.med_clinica_api.domain.entity.User;
import br.edu.ifg.med_clinica_api.domain.dto.user.UserDTO;
import br.edu.ifg.med_clinica_api.infra.security.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("auth")
public class UserController {

    private final TokenService tokenService;
    private final AuthenticationManager manager;
    private final ProfileService profileService;

    public UserController(TokenService tokenService, AuthenticationManager manager,
                          ProfileService profileService) {
        this.tokenService = tokenService;
        this.manager = manager;
        this.profileService = profileService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(
            @RequestBody
            @Valid UserDTO data,
            HttpServletResponse response
    ) {

        var authenticationToken = new UsernamePasswordAuthenticationToken(
                data.email(),
                data.password()
        );
        var authentication = manager.authenticate(authenticationToken);

        var tokenJWT = tokenService.generateToken((User) authentication.getPrincipal());

        ResponseCookie cookie = ResponseCookie.from("token", tokenJWT)
                .httpOnly(true)
                .secure(false) // permitir http (localhost)
                .path("/")
                .maxAge(Duration.ofHours(2))
                .sameSite("Lax")
                .build();

        response.setHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                profileService.getMyProfile(
                        authentication.getName()
                )
        );
    }
}
