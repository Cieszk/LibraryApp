package pl.cieszk.libraryapp.publishers.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.publishers.model.Publisher;
import pl.cieszk.libraryapp.publishers.repository.PublisherRepository;

@Service
@RequiredArgsConstructor
public class PublisherService {
    private final PublisherRepository publisherRepository;

    public Publisher getPublisherEntityById(Long id) {
        return publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found"));

    }
}
