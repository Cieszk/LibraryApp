package pl.cieszk.libraryapp.loans.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.cieszk.libraryapp.auth.model.User;
import pl.cieszk.libraryapp.auth.model.enums.UserRole;
import pl.cieszk.libraryapp.books.model.Book;
import pl.cieszk.libraryapp.books.model.BookInstance;
import pl.cieszk.libraryapp.books.model.enums.BookStatus;
import pl.cieszk.libraryapp.loans.model.BookLoan;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookLoanRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("library_db")
            .withUsername("postgres")
            .withPassword("postgres");

    @Autowired
    private BookLoanRepository bookLoanRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .userId(1L)
                .email("test@test.com")
                .username("test")
                .password("test")
                .isActive(true)
                .role(UserRole.USER)
                .build();

        Book book = Book.builder()
                .bookId(1L)
                .title("Spring in Action")
                .build();

        BookInstance bookInstance = BookInstance.builder()
                .book(book)
                .bookInstanceId(1L)
                .bookStatus(BookStatus.ACTIVE)
                .build();

        BookLoan bookLoan = BookLoan.builder()
                .bookInstance(bookInstance)
                .user(user)
                .loanDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusWeeks(2))
                .build();

        testEntityManager.merge(user);
        testEntityManager.merge(book);
        testEntityManager.merge(bookInstance);
        testEntityManager.merge(bookLoan);
    }

    @Test
    void testFindByUser_UserId() {
       // When & Then
        List<BookLoan> loans = bookLoanRepository.findByUser_UserId(1L);
        assertEquals(1, loans.size());
        assertEquals("test", loans.get(0).getUser().getUsername());
    }
}
