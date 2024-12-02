package pl.cieszk.libraryapp.features.reviews.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.reviews.domain.Review;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@Import(SecurityConfiguration.class)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private ReviewService reviewService;

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
    private User user;
    private Review review;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId(1L)
                .build();
        review = Review.builder()
                .reviewId(1L)
                .user(user)
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
    public void getAllReviewsByBook_ShouldReturnReviewsForGivenBook() throws Exception {
        // Given
        when(reviewService.getAllReviewsByBook(any())).thenReturn(List.of(review));

        // When & Then
        mockMvc.perform(get("/api/reviews")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    public void getReviewById_ShouldReturnReviewForGivenId() throws Exception {
        // Given
        when(reviewService.getReviewEntityById(1L)).thenReturn(review);

        // When & Then
        mockMvc.perform(get("/api/reviews/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk());
    }

    @Test
    public void getReviewById_ShouldReturnForbiddenStatus() throws Exception {
        // Given
        when(reviewService.getReviewEntityById(1L)).thenReturn(review);

        // When & Then
        mockMvc.perform(get("/api/reviews/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    public void addReview_ShouldReturnReview() throws Exception {
        // Given
        when(reviewService.addReview(review)).thenReturn(review);

        // When & Then
        mockMvc.perform(post("/api/reviews")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(1));
    }

    @Test
    public void addReview_ShouldReturnForbiddenStatus() throws Exception {
        // Given
        when(reviewService.addReview(review)).thenReturn(review);

        // When & Then
        mockMvc.perform(post("/api/reviews")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void updateReview_ShouldReturnUpdatedReviewForUser() throws Exception {
        // Given
        when(reviewService.updateReview(review)).thenReturn(review);

        // When & Then
        mockMvc.perform(put("/api/reviews")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void updateReview_ShouldReturnForbiddenStatusForAdmin() throws Exception {
        // Given
        when(reviewService.updateReview(review)).thenReturn(review);

        // When & Then
        mockMvc.perform(put("/api/reviews")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateReview_ShouldReturnForbiddenStatus() throws Exception {
        // Given
        when(reviewService.updateReview(review)).thenReturn(review);

        // When & Then
        mockMvc.perform(put("/api/reviews")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    public void deleteReview_ShouldReturnNoContentStatus() throws Exception {
        // Given
        // When & Then
        mockMvc.perform(delete("/api/reviews/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteReview_ShouldReturnForbiddenStatus() throws Exception {
        // Given
        // When & Then
        mockMvc.perform(delete("/api/reviews/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isForbidden());
    }
}
