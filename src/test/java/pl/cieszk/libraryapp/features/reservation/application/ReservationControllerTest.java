package pl.cieszk.libraryapp.features.reservation.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pl.cieszk.libraryapp.core.config.JwtAuthenticationFilter;
import pl.cieszk.libraryapp.core.config.SecurityConfiguration;
import pl.cieszk.libraryapp.core.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.features.auth.application.JwtService;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.auth.domain.enums.UserRole;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;
import pl.cieszk.libraryapp.features.reservations.application.ReservationController;
import pl.cieszk.libraryapp.features.reservations.application.ReservationService;
import pl.cieszk.libraryapp.features.reservations.domain.Reservation;
import pl.cieszk.libraryapp.shared.dto.BookUserRequest;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReservationController.class)
@Import(SecurityConfiguration.class)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean(name = "handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    private User user;
    private Book book;
    private BookInstance bookInstance;

    private String token;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .userId(1L)
                .role(UserRole.USER)
                .build();

        book = Book.builder()
                .bookId(1L)
                .build();

        bookInstance = BookInstance.builder()
                .bookInstanceId(1L)
                .book(book)
                .build();

        // Mock the JwtService to return a valid token
        token = "Bearer valid-jwt-token";
        when(jwtService.extractUsername(anyString())).thenReturn("testuser");
        when(jwtService.isTokenValid(anyString(), any())).thenReturn(true);

        // Configure MockMvc to use the JwtAuthenticationFilter
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new JwtAuthenticationFilter(handlerExceptionResolver, jwtService, userDetailsService))
                .build();
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    public void addReservation_ShouldCreateReservationForAuthenticatedUser() throws BookNotAvailableException, Exception {
        // Given
        BookUserRequest request = BookUserRequest.builder()
                .book(book)
                .user(user)
                .build();
        Reservation reservation = Reservation.builder()
                .reservationId(1L)
                .bookInstance(bookInstance)
                .user(user)
                .build();

        when(reservationService.createReservation(book, user)).thenReturn(reservation);

        // When & Then
        mockMvc.perform(post("/api/reservations")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value(reservation.getReservationId()));

    }

    @Test
    public void addReservation_ShouldReturnForbiddenForUnauthenticatedUser() throws BookNotAvailableException, Exception {
        // Given
        BookUserRequest request = BookUserRequest.builder()
                .book(book)
                .user(user)
                .build();
        Reservation reservation = Reservation.builder()
                .reservationId(1L)
                .bookInstance(bookInstance)
                .user(user)
                .build();

        when(reservationService.createReservation(book, user)).thenReturn(reservation);

        // When & Then
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    public void deleteReservation_ShouldDeleteReservationForAuthenticatedUser() throws Exception {
        // Given
        Long reservationId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/reservations/{reservationId}", reservationId)
                .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(reservationService, times(1)).deleteReservation(eq(reservationId));
    }

    @Test
    public void deleteReservation_ShouldReturnForbiddenForUnauthenticatedUser() throws Exception {
        // Given
        Long reservationId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/reservations/{reservationId}", reservationId))
                .andDo(print())
                .andExpect(status().isForbidden());
        verify(reservationService, never()).deleteReservation(eq(reservationId));
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    public void extendReservation_ShouldExtendReservationForAuthenticatedUser() throws Exception {
        // Given
        Long reservationId = 1L;
        Reservation reservation = Reservation.builder()
                .reservationId(reservationId)
                .bookInstance(bookInstance)
                .user(user)
                .dueDate(LocalDateTime.now())
                .build();

        when(reservationService.extendReservation(reservationId)).thenReturn(reservation);

        // When & Then
        mockMvc.perform(put("/api/reservations/{reservationId}", reservationId)
                .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value(reservationId))
                .andExpect(jsonPath("$.dueDate").isNotEmpty());
    }

    @Test
    public void extendReservation_ShouldReturnForbiddenForUnauthenticatedUser() throws Exception {
        // Given
        Long reservationId = 1L;

        // When & Then
        mockMvc.perform(put("/api/reservations/{reservationId}", reservationId))
                .andDo(print())
                .andExpect(status().isForbidden());
        verify(reservationService, never()).extendReservation(eq(reservationId));
    }
}
