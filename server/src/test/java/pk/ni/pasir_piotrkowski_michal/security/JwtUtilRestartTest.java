package pk.ni.pasir_piotrkowski_michal.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import pk.ni.pasir_piotrkowski_michal.model.User;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilRestartTest {

    private static final String SECRET_1 = "very_long_secret_key_that_is_at_least_64_bytes_long_to_satisfy_hs512_requirement_1";
    private static final String SECRET_2 = "very_long_secret_key_that_is_at_least_64_bytes_long_to_satisfy_hs512_requirement_2";

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        return user;
    }

    @Test
    void shouldValidateTokenWhenGeneratedAndValidatedWithSameSecret() {
        JwtUtil jwtUtil = new JwtUtil(SECRET_1);
        User user = createTestUser();

        String token = jwtUtil.generateToken(user);

        assertTrue(jwtUtil.validateToken(token));
        assertDoesNotThrow(() -> jwtUtil.extractUsername(token));
    }

    @Test
    void shouldRejectTokenWhenSecretsDiffer() {
        JwtUtil jwtUtilBeforeRestart = new JwtUtil(SECRET_1);
        User user = createTestUser();
        String tokenFromBeforeRestart = jwtUtilBeforeRestart.generateToken(user);

        JwtUtil jwtUtilAfterRestart = new JwtUtil(SECRET_2);

        assertFalse(jwtUtilAfterRestart.validateToken(tokenFromBeforeRestart));
        assertThrows(JwtException.class, () -> jwtUtilAfterRestart.extractUsername(tokenFromBeforeRestart));
    }
}
