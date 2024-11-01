package pl.cieszk.libraryapp.authors.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String nationality;
    private String biography;
    private Set<Long> bookIds;
}
