package pl.cieszk.libraryapp.features.publishers.application;

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
import pl.cieszk.libraryapp.core.exceptions.custom.PublisherNotFoundException;
import pl.cieszk.libraryapp.features.auth.application.JwtService;
import pl.cieszk.libraryapp.features.publishers.application.dto.PublisherRequestDto;
import pl.cieszk.libraryapp.features.publishers.application.dto.PublisherResponseDto;
import pl.cieszk.libraryapp.features.publishers.domain.Publisher;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublisherController.class)
@Import(SecurityConfiguration.class)
public class PublisherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private PublisherService publisherService;

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

    private String token;

    private PublisherResponseDto publisherDto;

    @BeforeEach
    void setUp() {
        publisherDto = PublisherResponseDto.builder()
                .name("Publisher")
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
    @WithMockUser(roles = "ADMIN")
    void getAllPublishers_ShouldReturnListOfPublishers() throws Exception {
        // Given
        when(publisherService.getAllPublishers()).thenReturn(List.of(publisherDto));

        // When & Then
        mockMvc.perform(get("/api/publishers")
                .header("Authorization", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Publisher"));
    }

    @Test
    void getAllPublishers_ShouldReturnForbiddenWhenUserIsNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/publishers"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllPublishers_ShouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/publishers")
                .header("Authorization", token))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPublisherById_ShouldReturnPublisher() throws Exception {
        // Given
        when(publisherService.getPublisherById(1L)).thenReturn(publisherDto);

        // When & Then
        mockMvc.perform(get("/api/publishers/1")
                .header("Authorization", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Publisher"));
    }

    @Test
    void getPublisherById_ShouldThrowForbiddenWhenUserIsNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/publishers/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPublisherById_ShouldReturnNotFoundWhenPublisherNotFound() throws Exception {
        // Given
        when(publisherService.getPublisherById(1L)).thenThrow(new PublisherNotFoundException("Publisher not found"));

        // When & Then
        mockMvc.perform(get("/api/publishers/1")
                .header("Authorization", token))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPublisherById_ShouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/publishers/1")
                .header("Authorization", token))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addPublisher_ShouldReturnPublisher() throws Exception {
        // Given
        when(publisherService.addPublisher(any(PublisherRequestDto.class))).thenReturn(publisherDto);

        // When & Then
        mockMvc.perform(post("/api/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publisherDto))
                .header("Authorization", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Publisher"));
    }

    @Test
    void addPublisher_ShouldThrowForbiddenWhenUserIsNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publisherDto)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void addPublisher_ShouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publisherDto))
                .header("Authorization", token))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePublisher_ShouldReturnPublisher() throws Exception {
        // Given
        when(publisherService.updatePublisher(any(PublisherRequestDto.class))).thenReturn(publisherDto);

        // When & Then
        mockMvc.perform(put("/api/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publisherDto))
                .header("Authorization", token))
                .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Publisher"));
    }

    @Test
    void updatePublisher_ShouldThrowForbiddenWhenUserIsNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publisherDto)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updatePublisher_ShouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publisherDto))
                .header("Authorization", token))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePublisher_ShouldReturnNotFoundWhenPublisherNotFound() throws Exception {
        // Given
        when(publisherService.updatePublisher(any(PublisherRequestDto.class))).thenThrow(new PublisherNotFoundException("Publisher not found"));

        // When & Then
        mockMvc.perform(put("/api/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publisherDto))
                .header("Authorization", token))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePublisher_ShouldReturnNoContent() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/publishers/1")
                .header("Authorization", token))
            .andExpect(status().isNoContent());
    }

    @Test
    void deletePublisher_ShouldThrowForbiddenWhenUserIsNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/publishers/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deletePublisher_ShouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/publishers/1")
                .header("Authorization", token))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePublisher_ShouldReturnNotFoundWhenPublisherNotFound() throws Exception {
        // Given
        doThrow(new PublisherNotFoundException("Publisher not found"))
                .when(publisherService).deletePublisher(1L);

        // When & Then
        mockMvc.perform(delete("/api/publishers/1")
                .header("Authorization", token))
            .andExpect(status().isNotFound());
    }
}
