package pl.cieszk.libraryapp.loans.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.cieszk.libraryapp.loans.model.BookLoan;
import pl.cieszk.libraryapp.loans.repository.BookLoanRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class FineService {

    private final BookLoanRepository bookLoanRepository;
    private static final double DAILY_FINE_RATE = 0.5;

    @Transactional
    public void calculateDailyFines() {
        List<BookLoan> overdueLoans = bookLoanRepository.findByReturnDateIsNullAndDueDateBefore(LocalDate.now());

        for (BookLoan loan : overdueLoans) {
            long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
            loan.setFineAmount(daysOverdue * DAILY_FINE_RATE);
            bookLoanRepository.save(loan);
        }
    }
}
