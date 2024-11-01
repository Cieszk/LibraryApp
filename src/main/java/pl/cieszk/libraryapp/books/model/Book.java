package pl.cieszk.libraryapp.books.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.cieszk.libraryapp.authors.model.Author;
import pl.cieszk.libraryapp.categories.model.Category;
import pl.cieszk.libraryapp.publishers.model.Publisher;
import pl.cieszk.libraryapp.reviews.model.Review;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long book_id;

    @Column(nullable = false)
    private String title;

    @Column(length = 50)
    private String genre;

    @Column
    private Integer publishYear;

    @Column(unique = true, length = 20)
    private String isbn;

    @Column(length = 50)
    private String language;

    @Column
    private Integer pageCount;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @ManyToMany
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors;

    @ManyToMany
    @JoinTable(
            name = "book_category",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;

    @OneToMany(mappedBy = "book")
    private Set<BookInstance> bookInstances;

    @OneToMany(mappedBy = "book")
    private Set<Review> reviews;
}
