package pl.cieszk.libraryapp.features.publishers.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cieszk.libraryapp.core.exceptions.custom.PublisherNotFoundException;
import pl.cieszk.libraryapp.features.publishers.domain.Publisher;
import pl.cieszk.libraryapp.features.publishers.repository.PublisherRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PublisherServiceTest {
    @Mock
    private PublisherRepository publisherRepository;

    @InjectMocks
    private PublisherService publisherService;

    private Publisher publisher;

    @BeforeEach
    public void setUp() {
        publisher = Publisher.builder()
                .publisherId(1L)
                .name("Publisher")
                .address("Address")
                .website("Website")
                .contactNumber("123456789")
                .build();
    }

    @Test
    public void getPublisherEntityById_ShouldReturnPublisher() {
        // Given
        when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

        // When
        Publisher result = publisherService.getPublisherEntityById(1L);

        // Then
        assertEquals(publisher, result);
        verify(publisherRepository).findById(1L);
    }

    @Test
    public void getPublisherById_ShouldThrowPublisherNotFoundException() {
        // Given
        when(publisherRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        PublisherNotFoundException ex = assertThrows(PublisherNotFoundException.class, () -> publisherService.getPublisherEntityById(1L));

        // Then
        assertEquals("Publisher not found", ex.getMessage());
    }

    @Test
    public void getAllPublishers_ShouldReturnListOfPublishers() {
        // Given
        when(publisherRepository.findAll()).thenReturn(List.of(publisher));

        // When
        List<Publisher> result = publisherService.getAllPublishers();

        // Then
        assertEquals(List.of(publisher), result);
        verify(publisherRepository).findAll();
        assertEquals(1, result.size());
    }

    @Test
    public void addPublisher_ShouldReturnPublisher() {
        // Given
        when(publisherRepository.save(publisher)).thenReturn(publisher);

        // When
        Publisher result = publisherService.addPublisher(publisher);

        // Then
        assertEquals(publisher, result);
        verify(publisherRepository).save(publisher);
    }

    @Test
    public void updatePublisher_ShouldReturnUpdatedPublisher() {
        // Given
        when(publisherRepository.existsById(1L)).thenReturn(true);
        when(publisherRepository.save(publisher)).thenReturn(publisher);

        // When
        Publisher result = publisherService.updatePublisher(publisher);

        // Then
        assertEquals(publisher, result);
        verify(publisherRepository).existsById(1L);
        verify(publisherRepository).save(publisher);
    }

    @Test
    public void updatePublisher_ShouldThrowPublisherNotFoundException() {
        // Given
        when(publisherRepository.existsById(1L)).thenReturn(false);

        // When
        PublisherNotFoundException ex = assertThrows(PublisherNotFoundException.class, () -> publisherService.updatePublisher(publisher));

        // Then
        assertEquals("Publisher not found", ex.getMessage());
    }

    @Test
    public void deletePublisher_ShouldDeletePublisher() {
        // Given
        when(publisherRepository.existsById(1L)).thenReturn(true);

        // When
        publisherService.deletePublisher(1L);

        // Then
        verify(publisherRepository).existsById(1L);
        verify(publisherRepository).deleteById(1L);
    }

    @Test
    public void deletePublisher_ShouldThrowPublisherNotFoundException() {
        // Given
        when(publisherRepository.existsById(1L)).thenReturn(false);

        // When
        PublisherNotFoundException ex = assertThrows(PublisherNotFoundException.class, () -> publisherService.deletePublisher(1L));

        // Then
        assertEquals("Publisher not found", ex.getMessage());
    }
}
