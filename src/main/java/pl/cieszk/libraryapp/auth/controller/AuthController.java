package pl.cieszk.libraryapp.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cieszk.libraryapp.auth.config.LoginResponse;
import pl.cieszk.libraryapp.auth.dto.LoginUserDto;
import pl.cieszk.libraryapp.auth.dto.RegisterUserDto;
import pl.cieszk.libraryapp.auth.model.User;
import pl.cieszk.libraryapp.auth.service.AuthService;
import pl.cieszk.libraryapp.auth.service.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authService.signUp(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = LoginResponse.builder()
                .expiresIn(jwtService.getExpirationTime())
                .token(jwtToken)
                .build();

        return ResponseEntity.ok(loginResponse);
    }
}
