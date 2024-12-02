package pl.cieszk.libraryapp.features.books.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.cieszk.libraryapp.features.books.domain.enums.BookStatus;
import pl.cieszk.libraryapp.features.loans.domain.BookLoan;
import pl.cieszk.libraryapp.features.reservations.domain.Reservation;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookInstanceId;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookStatus bookStatus;

    @OneToOne(mappedBy = "bookInstance", cascade = CascadeType.ALL)
    private Reservation reservation;

    @OneToMany(mappedBy = "bookInstance", cascade = CascadeType.ALL)
    private Set<BookLoan> bookLoans;

}

