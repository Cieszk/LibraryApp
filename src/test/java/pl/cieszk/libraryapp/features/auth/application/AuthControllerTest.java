package pl.cieszk.libraryapp.features.auth.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.cieszk.libraryapp.core.config.JwtAuthenticationFilter;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.auth.domain.enums.UserRole;
import pl.cieszk.libraryapp.core.exceptions.custom.AuthenticationFailedException;
import pl.cieszk.libraryapp.core.exceptions.GlobalExceptionHandler;
import pl.cieszk.libraryapp.core.exceptions.custom.UserAlreadyExistsException;

import java.util.HashSet;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuthController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void register_WhenUserAlreadyExists_ShouldReturnConflict() throws Exception {
        // Given
        RegisterUserDto registerUserDto = new RegisterUserDto();
        when(authService.signUp(registerUserDto))
                .thenThrow(new UserAlreadyExistsException("User already exists"));

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(registerUserDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User already exists"));
    }

    @Test
    void authenticate_WhenInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        // Given
        LoginUserDto loginUserDto = new LoginUserDto();
        when(authService.authenticate(loginUserDto))
                .thenThrow(new AuthenticationFailedException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginUserDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void register_WhenInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        RegisterUserDto registerUserDto = new RegisterUserDto();
        when(authService.signUp(registerUserDto))
                .thenThrow(new IllegalArgumentException("Invalid registration data"));

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(registerUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid registration data"));
    }

    @Test
    void register_ShouldReturnRegisteredUser() throws Exception {
        // Given
        RegisterUserDto registerUserDto = RegisterUserDto.builder()
                        .email("test@test.com")
                        .username("testUser")
                        .password("testPassword")
                        .build();


        User expectedUser = User.builder()
                .userId(1L)
                .email("test@test.com")
                .username("testUser")
                .role(UserRole.USER)
                .isActive(true)
                .reviews(new HashSet<>())
                .reservations(new HashSet<>())
                .bookLoans(new HashSet<>())
                .build();

        when(authService.signUp(registerUserDto)).thenReturn(expectedUser);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(registerUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(expectedUser.getUserId()))
                .andExpect(jsonPath("$.email").value(expectedUser.getEmail()))
                .andExpect(jsonPath("$.username").value(expectedUser.getUsername()))
                .andExpect(jsonPath("$.role").value(expectedUser.getRole().name()));
    }

    @Test
    void authenticate_ShouldReturnLoginResponse() throws Exception {
        // Given
        LoginUserDto loginUserDto = new LoginUserDto();
        User authenticatedUser = new User();
        String expectedToken = "jwt-token";
        long expirationTime = 3600L;

        when(authService.authenticate(loginUserDto)).thenReturn(authenticatedUser);
        when(jwtService.generateToken(authenticatedUser)).thenReturn(expectedToken);
        when(jwtService.getExpirationTime()).thenReturn(expirationTime);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken))
                .andExpect(jsonPath("$.expiresIn").value(expirationTime));
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}