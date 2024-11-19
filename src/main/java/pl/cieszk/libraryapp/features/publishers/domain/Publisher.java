package pl.cieszk.libraryapp.features.publishers.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.cieszk.libraryapp.features.books.domain.Book;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long publisherId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private String address;

    @Column(length = 100)
    private String website;

    @Column(length = 20)
    private String contactNumber;

    @OneToMany(mappedBy = "publisher")
    private Set<Book> books;
}
