package pl.cieszk.libraryapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookLoanId;

    @Column(nullable = false)
    private LocalDateTime loanDate;

    @Column
    private LocalDateTime returnDate;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Column
    private Double fineAmount;

    @Column
    private Integer renewCount = 0;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_instance_id", nullable = false)
    private BookInstance bookInstance;
}
