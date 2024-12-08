package pl.cieszk.libraryapp.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.auth.domain.enums.UserRole;
import pl.cieszk.libraryapp.features.auth.repository.UserRepository;

@Configuration
public class ApplicationConfiguration {
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Value("${security.admin.username}")
    private String username;
    @Value("${security.admin.password}")
    private String password;
    @Value("${security.admin.email}")
    private String email;

    public ApplicationConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    CommandLineRunner initAdminUser() {
        return args -> {
            if (userRepository.findByEmail(email).isEmpty()) {
                User admin = User.builder()
                        .email(email)
                        .username(username)
                        .password(password)
                        .role(UserRole.ADMIN)
                        .build();

                userRepository.save(admin);
                logger.debug("Admin user created successfully!");
            } else {
                logger.debug("Admin user already exists.");
            }
        };
    }
}
