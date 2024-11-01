package pl.cieszk.libraryapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import pl.cieszk.libraryapp.entity.User;
import pl.cieszk.libraryapp.repository.UserRepository;
import pl.cieszk.libraryapp.security.JwtUtils;
import pl.cieszk.libraryapp.service.CustomUserDetailsService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(value = AuthController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void authenticateUser_ShouldReturnJwtToken() throws Exception {
        // Given
        User loginRequest = User.builder()
                .username("testUser")
                .password("testPassword")
                .build();

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(loginRequest);
        when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("mockJwtToken");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("mockJwtToken"));
    }

    @Test
    void authenticateUser_ShouldReturnUnauthorizedException() throws Exception {
        // Given
        User loginRequest = User.builder()
                .username("testUser")
                .password("testPassword")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Invalid credentials") {});

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerUser_ShouldReturnSuccessMessage() throws Exception {
        // Given
        User singUpRequest = User.builder()
                .username("testUser")
                .password("testPassword")
                .email("test@test.com")
                .build();

        when(userRepository.existsByUsername(singUpRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(singUpRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(singUpRequest.getPassword())).thenReturn("encodedPassword");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(singUpRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void registerUser_ShouldReturnBadRequest_WhenUsernameTaken() throws Exception {
        // Given
        User signUpRequest = User.builder()
                .username("testUsernameTaken")
                .password("testPassword")
                .email("test@test.com")
                .build();

        when(userRepository.existsByUsername(signUpRequest.getUsername())).thenReturn(true);
        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username or email already taken"));
    }

    @Test
    void registerUser_ShouldReturnBadRequest_WhenEmailTaken() throws Exception {
        // Given
        User signUpRequest = User.builder()
                .username("testUsernameTaken")
                .password("testPassword")
                .email("test@test.com")
                .build();

        when(userRepository.existsByUsername(signUpRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(true);
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username or email already taken"));
    }
}
