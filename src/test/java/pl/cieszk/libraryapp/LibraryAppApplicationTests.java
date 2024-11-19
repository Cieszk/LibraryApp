package pl.cieszk.libraryapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import pl.cieszk.libraryapp.features.auth.application.JwtService;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class LibraryAppApplicationTests {

    @MockBean
    private JwtService jwtService;

    @Test
    void contextLoads() {

    }

}
