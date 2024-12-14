package pl.cieszk.libraryapp.features.category.application;

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
import pl.cieszk.libraryapp.features.categories.application.CategoryController;
import pl.cieszk.libraryapp.features.categories.application.CategoryService;
import pl.cieszk.libraryapp.features.categories.application.dto.CategoryRequestDto;
import pl.cieszk.libraryapp.features.categories.application.dto.CategoryResponseDto;
import pl.cieszk.libraryapp.features.categories.domain.Category;
import pl.cieszk.libraryapp.features.publishers.application.PublisherService;
import pl.cieszk.libraryapp.features.publishers.application.dto.PublisherRequestDto;
import pl.cieszk.libraryapp.features.publishers.application.dto.PublisherResponseDto;

import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@Import(SecurityConfiguration.class)
public class CategoryControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private CategoryService categoryService;

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

    private CategoryResponseDto categoryResponseDto;

    private CategoryRequestDto categoryRequestDto;

    private Category category;

    @BeforeEach
    void setUp() {
        categoryResponseDto = mock(CategoryResponseDto.class);
        categoryRequestDto = mock(CategoryRequestDto.class);
        category = mock(Category.class);

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
    void getAllCategories_ShouldReturnSetOfCategories_ForUserAndAdminRole() throws Exception {
        // given
        when(categoryService.getAllCategories()).thenReturn(Set.of(categoryResponseDto));

        // when & then
        mockMvc.perform(get("/api/categories/all")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(0));
    }

    @Test
    void getAllCategories_ShouldThrowForbidden_ForUnauthenticatedUser() throws Exception {
        // when & then
        mockMvc.perform(get("/api/categories/all")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void getCategory_ShouldReturnCategory_ForUserAndAdminRole() throws Exception {
        // given
        when(categoryService.findCategoryEntityById(categoryRequestDto)).thenReturn(categoryResponseDto);

        // when & then
        mockMvc.perform(get("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getCategory_ShouldThrowForbidden_ForUnauthenticatedUser() throws Exception {
        // when & then
        mockMvc.perform(get("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void addCategory_ShouldAddCategory_ForAdminRole() throws Exception {
        // given
        when(categoryService.addCategory(categoryRequestDto)).thenReturn(categoryResponseDto);

        // when & then
        mockMvc.perform(post("/api/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void addCategory_ShouldThrowForbidden_ForUserRole() throws Exception {
        // given
        when(categoryService.addCategory(categoryRequestDto)).thenReturn(categoryResponseDto);

        // when & then
        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void addCategory_ShouldThrowForbidden_ForUnauthenticatedUser() throws Exception {
        // when & then
        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles  = {"ADMIN"})
    void updateCategory_ShouldReturnUpdatedCategory_ForAdminRole() throws Exception {
        // given
        when(categoryService.updateCategory(1L, categoryRequestDto)).thenReturn(categoryResponseDto);

        mockMvc.perform(put("/api/categories/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("USER")
    void updateCategory_ShouldRThrowForbidden_ForUserRole() throws Exception {
        // when & then
        mockMvc.perform(put("/api/categories/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void updateCategory_ShouldRThrowForbidden_ForUnauthenticatedUser() throws Exception {
        // when & then
        mockMvc.perform(put("/api/categories/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles  = {"ADMIN"})
    void deleteCategory_ShouldDelete_ForAdminRole() throws Exception {
        // given
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser("USER")
    void deleteCategory_ShouldRThrowForbidden_ForUserRole() throws Exception {
        mockMvc.perform(put("/api/categories/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteCategory_ShouldRThrowForbidden_ForUnauthenticatedUser() throws Exception {
        mockMvc.perform(put("/api/categories/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
