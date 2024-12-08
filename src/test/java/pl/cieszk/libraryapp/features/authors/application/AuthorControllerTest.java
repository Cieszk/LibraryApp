package pl.cieszk.libraryapp.features.authors.application;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import pl.cieszk.libraryapp.core.exceptions.custom.AuthorNotFoundException;
import pl.cieszk.libraryapp.features.auth.application.JwtService;
import pl.cieszk.libraryapp.features.authors.application.dto.AuthorRequestDto;
import pl.cieszk.libraryapp.features.authors.application.dto.AuthorResponseDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookRequestDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthorController.class)
@Import(SecurityConfiguration.class)
public class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean(name = "handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private AuthenticationProvider authenticationProvider;
    private String token;
    private AuthorResponseDto authorResponseDto;
    private AuthorRequestDto authorRequestDto;
    private BookRequestDto bookRequestDto;

    @BeforeEach
    void setUp() {
        authorResponseDto = mock(AuthorResponseDto.class);
        authorRequestDto = mock(AuthorRequestDto.class);
        bookRequestDto = mock(BookRequestDto.class);

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
    void getAuthorById_WithAdminRole_ShouldReturnAuthor() throws Exception {
        // given
        when(authorService.getAuthorById(1L)).thenReturn(authorResponseDto);

        // when & then
        mockMvc.perform(get("/authors/{id}", 1L)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAuthorById_WithAdminRole_ShouldThrowAuthorNotFoundException() throws Exception {
        // given
        when(authorService.getAuthorById(1L)).thenThrow(AuthorNotFoundException.class);

        // when & then
        mockMvc.perform(get("/authors/{id}", 1L)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAuthorById_WithUserRole_ShouldReturnForbiddenStatus() throws Exception {
        // when & then
        mockMvc.perform(get("/authors/{id}", 1L)
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void getAuthorByName_WithUserAndAdminRole_ShouldReturnListOfAuthors() throws Exception {
        // given
        when(authorService.getAuthorByName(authorRequestDto)).thenReturn(List.of(authorResponseDto));

        // when & then
        mockMvc.perform(get("/authors")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAuthorByName_ShouldThrowForbiddenException_ForUnauthenticatedUser() throws Exception {
        // when & then
        mockMvc.perform(get("/authors")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorRequestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void getAuthorByBook_WithUserAndAdminRole_ShouldReturnAuthor() throws Exception {
        // given
        when(authorService.getAuthorByBook(bookRequestDto)).thenReturn(authorResponseDto);

        // when & then
        mockMvc.perform(get("/authors/book")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getAuthorByBook_WithUnauthenticatedUser_ShouldThrowForbidden() throws Exception {
        // given
        mockMvc.perform(get("/authors/book")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createAuthor_WithAdminRole_ShouldCreateAuthor() throws Exception{
        // given
        when(authorService.createAuthor(authorRequestDto)).thenReturn(authorResponseDto);

        // when & then
        mockMvc.perform(post("/authors")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createAuthor_WithUserRole_ShouldThrowForbidden() throws Exception {
        // when & then
        mockMvc.perform(post("/authors")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorRequestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createAuthor_ForUnauthenticatedUser_ShouldThrowForbidden() throws Exception {
        // when & then
        mockMvc.perform(post("/authors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAuthor_WithAdminRole_ShouldUpdateAuthor() throws Exception {
        // given
        when(authorService.updateAuthor(1L, authorRequestDto)).thenReturn(authorResponseDto);

        // when & then
        mockMvc.perform(put("/authors/{id}", 1L)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateAuthor_WithUserRole_ShouldThrowForbidden() throws Exception {
        // when & then
        mockMvc.perform(put("/authors/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateAuthor_ForUnauthenticatedUser_ShouldThrowForbidden() throws Exception {
        // when & then
        mockMvc.perform(put("/authors/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAuthor_WithAdminRole_ShouldDeleteAuthor() throws Exception {
        // given
        doNothing().when(authorService).deleteAuthor(1L);

        // when & then
        mockMvc.perform(delete("/authors/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteAuthor_WithUserRole_ShouldThrowForbidden() throws Exception {
        // when & then
        mockMvc.perform(delete("/authors/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAuthor_ForUnauthenticatedUser_ShouldThrowForbidden() throws Exception {
        // when & then
        mockMvc.perform(delete("/authors/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}