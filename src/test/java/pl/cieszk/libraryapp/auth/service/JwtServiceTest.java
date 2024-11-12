package pl.cieszk.libraryapp.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtServiceTest {

    JwtService jwtService = new JwtService();

    @Mock
    private UserDetails userDetails;

    private final String secretKey = "dGhpcyBpcyBhIHZlcnkgc2VjdXJlIHNlY3JldCBrZXk=";
    private final long expirationTime = 3600000;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService.setSecretKey(secretKey);
        jwtService.setJwtExpiration(expirationTime);
        when(userDetails.getUsername()).thenReturn("testUser");
    }

    @Test
    void generateToken_ShouldGenerateValidToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("testUser", jwtService.extractUsername(token));
    }

    @Test
    void generateTokenWithClaims_ShouldIncludeClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");

        String token = jwtService.generateToken(claims, userDetails);

        assertNotNull(token);
        assertEquals("ADMIN", jwtService.extractClaim(token, claimsResolver -> claimsResolver.get("role")));
        assertEquals("testUser", jwtService.extractUsername(token));
    }

    @Test
    void extractUsername_ShouldReturnCorretUsername() {
        String token = jwtService.generateToken(userDetails);

        assertEquals("testUser", jwtService.extractUsername(token));
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValid() {
        String token = jwtService.generateToken(userDetails);

        boolean result = jwtService.isTokenValid(token, userDetails);

        assertTrue(result);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenTokenIsInvalid() {
        String token = jwtService.generateToken(userDetails);
        String invalidToken = token.substring(0, token.length() - 1) + "x";

        boolean result = jwtService.isTokenValid(invalidToken, userDetails);

        assertFalse(result);
    }

    @Test
    void isTokenExpired_ShouldReturnTrue_WhenTokenIsExpired() {
        String token = jwtService.buildToken(new HashMap<>(), userDetails, 1L);

        // Wait for the token to expire
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(jwtService.isTokenExpired(token));
    }

    @Test
    void isTokenExpired_ShouldFalse_WhenTokenIsNotExpired() {
        String token = jwtService.generateToken(userDetails);

        boolean result = jwtService.isTokenExpired(token);

        assertFalse(result);
    }
}
