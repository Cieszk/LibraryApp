package pl.cieszk.libraryapp.features.books.application;

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
import pl.cieszk.libraryapp.features.auth.application.JwtService;
import pl.cieszk.libraryapp.features.books.application.dto.BookRequestDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookResponseDto;

import java.util.List;

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

    private BookResponseDto bookDto;

    private BookRequestDto bookRequestDto;
    private String token;

    @BeforeEach
    void setUp() {
        bookDto = new BookResponseDto();
        bookDto.setTitle("Test");

        bookRequestDto = new BookRequestDto();
        bookRequestDto.setTitle("Test");

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
        when(bookService.createBook(any(BookRequestDto.class))).thenReturn(bookDto);

        // When & Then
        mockMvc.perform(post("/api/books")
                        .with(csrf())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void addBook_WithUserRole_ShouldThrowAuthorizationDeniedException() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isForbidden());
        verify(bookService, never()).createBook(any(BookRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBook_WithAdminRole_ShouldUpdateBook() throws Exception {
        // Given
        Long bookId = 1L;
        when(bookService.updateBook(any(BookRequestDto.class))).thenReturn(bookDto);

        // When & Then
        mockMvc.perform(put("/api/books", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(bookRequestDto.getTitle()));

        verify(bookService).updateBook(any(BookRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateBook_WithUserRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/books", 1L)
                        .content(objectMapper.writeValueAsString(bookRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isForbidden());

        verify(bookService, never()).updateBook(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBook_WithAdminRole_ShouldDeleteBook() throws Exception {
        // Given
        doNothing().when(bookService).deleteBook(eq(bookRequestDto));

        // When & Then
        mockMvc.perform(delete("/api/books")
                .content(objectMapper.writeValueAsString(bookRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookService).deleteBook(bookRequestDto);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteBook_WithUserRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/books")
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(bookRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(bookService, never()).deleteBook(any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void getAllBooks_WithAdminOrUserRole_ShouldReturnAllBooks() throws Exception {
        // Given
        when(bookService.getAllBooks()).thenReturn(List.of(bookDto));

        // When & Then
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Test"));

        verify(bookService).getAllBooks();
    }
}