package pl.cieszk.libraryapp.features.publishers.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.core.exceptions.custom.PublisherNotFoundException;
import pl.cieszk.libraryapp.features.publishers.application.dto.PublisherRequestDto;
import pl.cieszk.libraryapp.features.publishers.application.dto.PublisherResponseDto;
import pl.cieszk.libraryapp.features.publishers.application.mapper.PublisherMapper;
import pl.cieszk.libraryapp.features.publishers.domain.Publisher;
import pl.cieszk.libraryapp.features.publishers.repository.PublisherRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublisherService {
    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;

    public PublisherResponseDto getPublisherById(Long id) {
        Publisher publisher =  publisherRepository.findById(id)
                .orElseThrow(() -> new PublisherNotFoundException("Publisher not found"));
        return publisherMapper.toResponseDto(publisher);
    }

    public List<PublisherResponseDto> getAllPublishers() {
        return publisherRepository.findAll().stream()
                .map(publisherMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public PublisherResponseDto addPublisher(PublisherRequestDto publisher) {
        return publisherMapper.toResponseDto(publisherRepository.save(publisherMapper.toEntity(publisher)));
    }

    public PublisherResponseDto updatePublisher(PublisherRequestDto publisher) {
        if (!publisherRepository.existsById(publisher.getId())) {
            throw new PublisherNotFoundException("Publisher not found");
        }
        return publisherMapper.toResponseDto(publisherRepository.save(publisherMapper.toEntity(publisher)));
    }

    public void deletePublisher(Long id) {
        if (!publisherRepository.existsById(id)) {
            throw new PublisherNotFoundException("Publisher not found");
        }
        publisherRepository.deleteById(id);
    }
}
