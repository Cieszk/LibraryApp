package pl.cieszk.libraryapp.features.publishers.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.core.exceptions.custom.PublisherNotFoundException;
import pl.cieszk.libraryapp.features.publishers.domain.Publisher;
import pl.cieszk.libraryapp.features.publishers.repository.PublisherRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublisherService {
    private final PublisherRepository publisherRepository;

    public Publisher getPublisherEntityById(Long id) {
        return publisherRepository.findById(id)
                .orElseThrow(() -> new PublisherNotFoundException("Publisher not found"));
    }

    public List<Publisher> getAllPublishers() {
        return publisherRepository.findAll();
    }

    public Publisher addPublisher(Publisher publisher) {
        return publisherRepository.save(publisher);
    }

    public Publisher updatePublisher(Publisher publisher) {
        if (!publisherRepository.existsById(publisher.getPublisherId())) {
            throw new PublisherNotFoundException("Publisher not found");
        }
        return publisherRepository.save(publisher);
    }

    public void deletePublisher(Long id) {
        if (!publisherRepository.existsById(id)) {
            throw new PublisherNotFoundException("Publisher not found");
        }
        publisherRepository.deleteById(id);
    }
}
