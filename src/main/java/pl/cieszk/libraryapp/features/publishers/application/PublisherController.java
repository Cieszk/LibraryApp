package pl.cieszk.libraryapp.features.publishers.application;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.cieszk.libraryapp.features.publishers.domain.Publisher;

import java.util.List;

@RestController
@RequestMapping("/api/publishers")
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PublisherController {
    private final PublisherService publisherService;

    @GetMapping
    public ResponseEntity<List<Publisher>> getAllPublishers() {
        return ResponseEntity.ok(publisherService.getAllPublishers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publisher> getPublisherById(@PathVariable Long id) {
        return ResponseEntity.ok(publisherService.getPublisherEntityById(id));
    }

    @PostMapping
    public ResponseEntity<Publisher> addPublisher(@RequestBody Publisher publisher) {
        return ResponseEntity.ok(publisherService.addPublisher(publisher));
    }

    @PutMapping
    public ResponseEntity<Publisher> updatePublisher(@RequestBody Publisher publisher) {
        return ResponseEntity.ok(publisherService.updatePublisher(publisher));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }
}
