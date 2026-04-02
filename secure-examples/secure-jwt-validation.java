package secure.examples;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;

/**
 * Secure JWT validation example.
 *
 * Before:
 * - Token was only Base64-decoded and claims were trusted directly.
 * - Expiration was not enforced.
 *
 * Improved:
 * - Signature is verified with explicit algorithm.
 * - Issuer and expiration are validated by verifier configuration.
 */
public class SecureJwtValidationExample {

    private final JWTVerifier verifier;

    public SecureJwtValidationExample(String secret) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm)
            .withIssuer("telecom-api")
            .acceptLeeway(2)
            .build();
    }

    public RequesterContext validateAndExtract(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing bearer token");
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();
        try {
            DecodedJWT jwt = verifier.verify(token);
            Long userId = jwt.getClaim("userId").asLong();
            String role = jwt.getClaim("role").asString();
            if (userId == null || role == null || role.isBlank()) {
                throw new UnauthorizedException("Required claims are missing");
            }
            if (jwt.getExpiresAtAsInstant() != null && jwt.getExpiresAtAsInstant().isBefore(Instant.now())) {
                throw new UnauthorizedException("Token expired");
            }
            return new RequesterContext(userId, role);
        } catch (JWTVerificationException ex) {
            throw new UnauthorizedException("Invalid token");
        }
    }

    public static class RequesterContext {
        private final Long userId;
        private final String role;

        public RequesterContext(Long userId, String role) {
            this.userId = userId;
            this.role = role;
        }

        public Long getUserId() {
            return userId;
        }

        public String getRole() {
            return role;
        }
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}
