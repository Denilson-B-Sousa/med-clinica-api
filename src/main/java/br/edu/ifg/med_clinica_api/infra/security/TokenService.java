package br.edu.ifg.med_clinica_api.infra.security;

import br.edu.ifg.med_clinica_api.domain.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.issuer}")
    private String issuer;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(user.getEmail())
                    .withClaim(
                            "role",
                            user.getRole().name()
                    )
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException(
                    "Erro ao gerar token JWT.",
                    exception
            );
        }
    }

    public String generateGoogleSignupToken(
            String email,
            String name,
            String providerId
    ) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(email)
                    .withClaim("purpose", "GOOGLE_SIGNUP")
                    .withClaim("name", name)
                    .withClaim("providerId", providerId)
                    .withExpiresAt(
                            LocalDateTime.now()
                                    .plusMinutes(15)
                                    .toInstant(ZoneOffset.of("-03:00"))
                    )
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token de cadastro Google.", exception);
        }
    }

    public String getSubject(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            {
                throw new RuntimeException(
                        "Token JWT inválido ou expirado!"
                );
            }
        }
    }

    public DecodedJWT validateGoogleSignupToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .withClaim("purpose", "GOOGLE_SIGNUP")
                    .build()
                    .verify(token);

            return decodedJWT;
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token de Cadastro Google inválido ou expirado.");
        }
    }

        private Instant generateExpirationDate () {
            return LocalDateTime.now()
                    .plusHours(2)
                    .toInstant(ZoneOffset.of("-03:00"));
        }
    }