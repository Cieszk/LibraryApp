package pl.cieszk.libraryapp.features.loans.application;

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
import pl.cieszk.libraryapp.features.auth.application.dto.UserRequestDto;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.auth.domain.enums.UserRole;
import pl.cieszk.libraryapp.features.books.application.BookService;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceFineDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceResponseDto;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;
import pl.cieszk.libraryapp.features.loans.application.dto.BookLoanResponseDto;
import pl.cieszk.libraryapp.features.loans.domain.BookLoan;
import pl.cieszk.libraryapp.shared.dto.BookUserRequest;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookLoanController.class)
@Import(SecurityConfiguration.class)
public class BookLoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private BookLoanService bookLoanService;

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

    @MockBean
    private BookService bookService;

    private String token;

    private User user;

    private Book book;

    private BookInstance bookInstance;

    private BookLoan bookLoan;

    private UserRequestDto userRequestDto;

    private BookLoanResponseDto bookLoanResponseDto;

    private BookUserRequest bookUserRequest;
    private BookInstanceResponseDto bookInstanceResponseDto;

    private BookInstanceFineDto bookInstanceFineDto;

    @BeforeEach
    void setUp() {
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

        bookLoan = BookLoan.builder()
                .bookInstance(bookInstance)
                .user(user)
                .build();

        userRequestDto = UserRequestDto.builder()
                .build();

        bookLoanResponseDto = BookLoanResponseDto.builder()
                .build();

        bookUserRequest = BookUserRequest.builder()
                .build();

        bookInstanceFineDto = mock(BookInstanceFineDto.class);

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
    void getCurrentUserLoans_ShouldReturnUserLoansWithUserRole() throws Exception {
        // Given
        when(bookLoanService.getCurrentUserLoans(userRequestDto)).thenReturn(Set.of(bookLoanResponseDto));

        // When & Then
        mockMvc.perform(get("/api/loans/current")
                    .header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void getCurrentUserLoans_ShouldReturnForbiddenForUnauthenticatedUser() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/loans/current")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void renewLoan_ShouldReturnRenewedLoanWithUserRole() throws Exception {
        // Given
        when(bookLoanService.renewLoan(bookUserRequest)).thenReturn(bookLoanResponseDto);

        // When & Then
        mockMvc.perform(post("/api/loans/renew")
                    .header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book))
                    .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void renewLoan_ShouldReturnForbiddenForUnauthenticatedUser() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/loans/renew")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book))
                    .content(objectMapper.writeValueAsString(user))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void returnBook_ShouldReturnOkWithUserRole() throws Exception {
        // Given
        bookLoan.setReturnDate(LocalDateTime.now());
        when(bookLoanService.returnBook(bookUserRequest)).thenReturn(bookLoanResponseDto);

        // When & Then
        mockMvc.perform(post("/api/loans/return")
                    .header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookUserRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void returnBook_ShouldReturnForbiddenForUnauthenticatedUser() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/loans/return")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book))
                    .content(objectMapper.writeValueAsString(user))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void createLoan_ShouldReturnOkWithUserRole() throws Exception, BookNotAvailableException {
        // Given
        when(bookLoanService.createLoan(bookUserRequest)).thenReturn(bookLoanResponseDto);

        // When & Then
        mockMvc.perform(post("/api/loans")
                    .header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookUserRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void createLoan_ShouldReturnForbiddenForUnauthenticatedUser() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/loans")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book))
                    .content(objectMapper.writeValueAsString(user))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void getLoanHistory_ShouldReturnUserLoanHistoryWithUserRole() throws Exception {
        // Given
        when(bookLoanService.getLoanHistory(userRequestDto)).thenReturn(Set.of(bookLoanResponseDto));

        // When & Then
        mockMvc.perform(get("/api/loans/history")
                    .header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getLoanHistory_ShouldReturnForbiddenForUnauthenticatedUser() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/loans/history")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void getUserFines_ShouldReturnUserFinesWithUserRole() throws Exception {
        // Given
        List<BookInstanceFineDto> userFines = List.of(bookInstanceFineDto);
        when(bookLoanService.getUserFines(userRequestDto)).thenReturn(userFines);

        // When & Then
        mockMvc.perform(get("/api/loans/fines")
                    .header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getUserFines_ShouldReturnForbiddenForUnauthenticatedUser() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/loans/fines")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
