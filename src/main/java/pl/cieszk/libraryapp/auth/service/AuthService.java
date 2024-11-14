package pl.cieszk.libraryapp.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.auth.dto.LoginUserDto;
import pl.cieszk.libraryapp.auth.dto.RegisterUserDto;
import pl.cieszk.libraryapp.auth.model.User;
import pl.cieszk.libraryapp.auth.repository.UserRepository;
import pl.cieszk.libraryapp.exceptions.custom.AuthenticationFailedException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public User signUp(RegisterUserDto registerUserDto) {
        User user = User.builder()
                .username(registerUserDto.getUsername())
                .email(registerUserDto.getEmail())
                .password(passwordEncoder.encode(registerUserDto.getPassword()))
                .build();
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto loginUserDto) {
        if (loginUserDto == null) {
            throw new IllegalArgumentException("Login request cannot be null");
        }

        User user = userRepository.findByEmail(loginUserDto.getEmail())
                .orElseThrow(() -> new AuthenticationFailedException("User not found"));

        if (!passwordEncoder.matches(loginUserDto.getPassword(), user.getPassword())) {
            throw new AuthenticationFailedException("Invalid credentials");
        }

        return user;
    }
}
