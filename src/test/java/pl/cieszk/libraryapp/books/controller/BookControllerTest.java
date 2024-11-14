package pl.cieszk.libraryapp.books.controller;

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
import pl.cieszk.libraryapp.auth.config.JwtAuthenticationFilter;
import pl.cieszk.libraryapp.auth.config.SecurityConfiguration;
import pl.cieszk.libraryapp.auth.service.JwtService;
import pl.cieszk.libraryapp.books.dto.BookDto;
import pl.cieszk.libraryapp.books.service.BookService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookController.class)
@Import(SecurityConfiguration.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private BookService bookService;

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

    private BookDto bookDto;
    private String token;

    @BeforeEach
    void setUp() {
        bookDto = new BookDto();
        bookDto.setTitle("Test Book");

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
    @WithMockUser(roles = "ADMIN")
    void addBook_WithAdminRole_ShouldCreateBook() throws Exception {
        // Given
        when(bookService.createBook(any(BookDto.class))).thenReturn(bookDto);

        // When & Then
        mockMvc.perform(post("/api/books")
                        .with(csrf())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void addBook_WithUserRole_ShouldThrowAuthorizationDeniedException() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isForbidden());
        verify(bookService, never()).createBook(any(BookDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBook_WithAdminRole_ShouldUpdateBook() throws Exception {
        // Given
        Long bookId = 1L;
        when(bookService.updateBook(eq(bookId), any(BookDto.class))).thenReturn(bookDto);

        // When & Then
        mockMvc.perform(put("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(bookDto.getTitle()));

        verify(bookService).updateBook(eq(bookId), any(BookDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateBook_WithUserRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isForbidden());

        verify(bookService, never()).updateBook(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBook_WithAdminRole_ShouldDeleteBook() throws Exception {
        // Given
        Long bookId = 1L;
        doNothing().when(bookService).deleteBook(bookId);

        // When & Then
        mockMvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().string("Book deleted successfully."));

        verify(bookService).deleteBook(bookId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteBook_WithUserRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/books/{id}", 1L)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(bookService, never()).deleteBook(any());
    }
}