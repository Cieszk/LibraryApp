package pl.cieszk.libraryapp.features.publishers.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cieszk.libraryapp.core.exceptions.custom.PublisherNotFoundException;
import pl.cieszk.libraryapp.features.publishers.application.dto.PublisherRequestDto;
import pl.cieszk.libraryapp.features.publishers.application.dto.PublisherResponseDto;
import pl.cieszk.libraryapp.features.publishers.application.mapper.PublisherMapper;
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

    @Mock
    private PublisherMapper publisherMapper;

    private PublisherResponseDto publisherDto;

    private Publisher publisher;

    private PublisherRequestDto publisherRequestDto;

    @BeforeEach
    public void setUp() {
        publisherDto = PublisherResponseDto.builder()
                .name("Publisher")
                .address("Address")
                .website("Website")
                .contactNumber("123456789")
                .build();

        publisherRequestDto = PublisherRequestDto.builder()
                .name("Publisher")
                .address("Address")
                .website("Website")
                .contactNumber("123456789")
                .build();

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
        when(publisherRepository.findByName(publisher.getName())).thenReturn(Optional.of(publisher));
        when(publisherMapper.toResponseDto(publisher)).thenReturn(publisherDto);

        // When
        PublisherResponseDto result = publisherService.getPublisherById(publisherRequestDto);

        // Then
        assertEquals(publisherDto, result);
        verify(publisherRepository).findByName(publisher.getName());
    }

    @Test
    public void getPublisherById_ShouldThrowPublisherNotFoundException() {
        // Given
        when(publisherRepository.findByName(publisher.getName())).thenReturn(Optional.empty());

        // When
        PublisherNotFoundException ex = assertThrows(PublisherNotFoundException.class, () -> publisherService.getPublisherById(publisherRequestDto));

        // Then
        assertEquals("Publisher not found", ex.getMessage());
    }

    @Test
    public void getAllPublishers_ShouldReturnListOfPublishers() {
        // Given
        when(publisherRepository.findAll()).thenReturn(List.of(publisher));
        when(publisherMapper.toResponseDto(publisher)).thenReturn(publisherDto);

        // When
        List<PublisherResponseDto> result = publisherService.getAllPublishers();

        // Then
        assertEquals(List.of(publisherDto), result);
        verify(publisherRepository).findAll();
        assertEquals(1, result.size());
    }

    @Test
    public void addPublisher_ShouldReturnPublisher() {
        // Given
        when(publisherRepository.save(publisher)).thenReturn(publisher);
        when(publisherMapper.toResponseDto(publisher)).thenReturn(publisherDto);
        when(publisherMapper.toEntity(publisherRequestDto)).thenReturn(publisher);

        // When
        PublisherResponseDto result = publisherService.addPublisher(publisherRequestDto);

        // Then
        assertEquals(publisherDto, result);
        verify(publisherRepository).save(publisher);
    }

    @Test
    public void updatePublisher_ShouldReturnUpdatedPublisher() {
        // Given
        when(publisherRepository.existsByName(publisher.getName())).thenReturn(true);
        when(publisherRepository.save(publisher)).thenReturn(publisher);
        when(publisherMapper.toResponseDto(publisher)).thenReturn(publisherDto);
        when(publisherMapper.toEntity(publisherRequestDto)).thenReturn(publisher);

        // When
        PublisherResponseDto result = publisherService.updatePublisher(publisherRequestDto);

        // Then
        assertEquals(publisherDto, result);
        verify(publisherRepository).existsByName(publisher.getName());
        verify(publisherRepository).save(publisher);
    }

    @Test
    public void updatePublisher_ShouldThrowPublisherNotFoundException() {
        // Given
        when(publisherRepository.existsByName(publisher.getName())).thenReturn(false);

        // When
        PublisherNotFoundException ex = assertThrows(PublisherNotFoundException.class, () -> publisherService.updatePublisher(publisherRequestDto));

        // Then
        assertEquals("Publisher not found", ex.getMessage());
    }

    @Test
    public void deletePublisher_ShouldDeletePublisher() {
        // Given
        when(publisherRepository.existsByName(publisher.getName())).thenReturn(true);

        // When
        publisherService.deletePublisher(publisherRequestDto);

        // Then
        verify(publisherRepository).existsByName(publisher.getName());
        verify(publisherRepository).deleteByName(publisher.getName());
    }

    @Test
    public void deletePublisher_ShouldThrowPublisherNotFoundException() {
        // Given
        when(publisherRepository.existsByName(publisher.getName())).thenReturn(false);

        // When
        PublisherNotFoundException ex = assertThrows(PublisherNotFoundException.class, () -> publisherService.deletePublisher(publisherRequestDto));

        // Then
        assertEquals("Publisher not found", ex.getMessage());
    }
}
