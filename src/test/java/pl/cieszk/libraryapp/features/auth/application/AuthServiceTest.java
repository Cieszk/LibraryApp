package pl.cieszk.libraryapp.features.auth.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.cieszk.libraryapp.features.auth.application.LoginUserDto;
import pl.cieszk.libraryapp.features.auth.application.RegisterUserDto;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.auth.repository.UserRepository;
import pl.cieszk.libraryapp.core.exceptions.custom.AuthenticationFailedException;
import pl.cieszk.libraryapp.features.auth.application.AuthService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        // Given
        RegisterUserDto registerUserDto = RegisterUserDto.builder()
                .username("test")
                .email("test@test.com")
                .password("password")
                .build();

        User mockUser = User.builder()
                .userId(1L)
                .username("test")
                .email("test@test.com")
                .password("encodedPassword")
                .build();

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When
        User createdUser = authService.signUp(registerUserDto);

        // Then
        assertEquals(mockUser.getUsername(), createdUser.getUsername());
        assertEquals(mockUser.getEmail(), createdUser.getEmail());
        assertEquals(mockUser.getPassword(), createdUser.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password");
    }

    @Test
    void testAuthenticate_Success() {
        // Given
        LoginUserDto loginUserDto = LoginUserDto.builder()
                .email("test@test.com")
                .password("password")
                .build();

        User mockedUser = User.builder()
                .userId(1L)
                .username("testuser")
                .email("test@test.com")
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockedUser));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        // When
        User authenticatedUser = authService.authenticate(loginUserDto);

        // Then
        assertEquals(mockedUser, authenticatedUser);
        verify(userRepository, times(1)).findByEmail("test@test.com");
        verify(passwordEncoder, times(1)).matches("password", "encodedPassword");
    }



    @Test
    void testAuthenticate_UserNotFound() {
        // Given
        LoginUserDto loginUserDto = LoginUserDto.builder()
                .email("wrong@test.com")
                .password("password")
                .build();

        when(userRepository.findByEmail(loginUserDto.getEmail()))
                .thenReturn(Optional.empty()); // Simulate user not found

        // When & Then
        assertThrows(AuthenticationFailedException.class, () -> authService.authenticate(loginUserDto));
        verify(userRepository).findByEmail(loginUserDto.getEmail());
    }
}
