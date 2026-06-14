package br.edu.ifg.med_clinica_api.infra.security;

import br.edu.ifg.med_clinica_api.domain.dao.UserRepository;
import br.edu.ifg.med_clinica_api.domain.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final String FRONTEND_SUCCESS_URL = "http://localhost:5173/oauth-success";
    private static final String FRONTEND_SIGNUP_URL = "http://localhost:5173/cadastro/google";
    private static final String FRONTEND_ERROR_URL = "http://localhost:5173/login?error=google_login_failed";

    private final UserRepository userRepository;
    private final TokenService tokenService;

    public OAuth2SuccessHandler(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        String email = getEmail(authentication);
        String name = getName(authentication);
        String providerId = getProviderId(authentication);

        if (email == null || email.isBlank() || providerId == null || providerId.isBlank()) {
            response.sendRedirect(FRONTEND_ERROR_URL);
            return;
        }

        if (name == null || name.isBlank()) {
            name = email;
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            String signupToken = tokenService.generateGoogleSignupToken(email, name, providerId);

            ResponseCookie signupCookie = ResponseCookie.from("google_signup_token", signupToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(Duration.ofMinutes(15))
                    .sameSite("Lax")
                    .build();

            ResponseCookie clearAuthCookie = ResponseCookie.from("token", "")
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(Duration.ZERO)
                    .sameSite("Lax")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, signupCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, clearAuthCookie.toString());
            response.sendRedirect(FRONTEND_SIGNUP_URL);
            return;
        }

        String tokenJWT = tokenService.generateToken(user);

        ResponseCookie cookie = ResponseCookie.from("token", tokenJWT)
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

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearSignupCookie.toString());
        response.sendRedirect(FRONTEND_SUCCESS_URL);
    }

    private String getEmail(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser oidcUser) {
            return oidcUser.getEmail();
        }

        if (principal instanceof OAuth2User oAuth2User) {
            return oAuth2User.getAttribute("email");
        }

        return null;
    }

    private String getName(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser oidcUser) {
            String fullName = oidcUser.getFullName();

            if (fullName != null && !fullName.isBlank()) {
                return fullName;
            }

            return oidcUser.getGivenName();
        }

        if (principal instanceof OAuth2User oAuth2User) {
            String name = oAuth2User.getAttribute("name");

            if (name != null && !name.isBlank()) {
                return name;
            }

            return oAuth2User.getAttribute("given_name");
        }

        return null;
    }

    private String getProviderId(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser oidcUser) {
            return oidcUser.getSubject();
        }

        if (principal instanceof OAuth2User oAuth2User) {
            return oAuth2User.getAttribute("sub");
        }

        return null;
    }
}
