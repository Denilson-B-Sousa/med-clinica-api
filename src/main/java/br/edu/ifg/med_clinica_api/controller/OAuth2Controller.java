package br.edu.ifg.med_clinica_api.controller;

import br.edu.ifg.med_clinica_api.domain.bo.GoogleAuthService;
import br.edu.ifg.med_clinica_api.domain.dto.patient.GooglePatientRegisterDTO;
import br.edu.ifg.med_clinica_api.domain.dto.user.GoogleSignupPendingDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Duration;

@RestController
@RequestMapping("auth")
public class OAuth2Controller {

    private final GoogleAuthService googleAuthService;

    public OAuth2Controller(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    @GetMapping("/google")
    public void loginWithGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/google/pending")
    public ResponseEntity<GoogleSignupPendingDTO> getGooglePendingSignup(
            @CookieValue("google_signup_token") String signupToken
    ) {
        return ResponseEntity.ok(googleAuthService.getPendingSignup(signupToken));
    }

    @PostMapping("/google/complete-patient")
    public ResponseEntity<Void> completeGooglePatientSignup(
            @CookieValue("google_signup_token") String signupToken,
            @RequestBody @Valid GooglePatientRegisterDTO data,
            HttpServletResponse response
    ) {
        String tokenJWT = googleAuthService.completePatientSignup(signupToken, data);

        ResponseCookie authCookie = ResponseCookie.from("token", tokenJWT)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofHours(2))
                .sameSite("Lax")
                .build();

        ResponseCookie clearSignupCookie = ResponseCookie.from("google_signup_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ZERO)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, authCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearSignupCookie.toString());

        return ResponseEntity.ok().build();
    }
}
