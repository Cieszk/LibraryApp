package pl.cieszk.libraryapp.features.reservations.application.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import pl.cieszk.libraryapp.features.auth.application.mapper.UserMapper;
import pl.cieszk.libraryapp.features.books.application.mapper.BookInstanceMapper;
import pl.cieszk.libraryapp.features.reservations.application.dto.ReservationRequestDto;
import pl.cieszk.libraryapp.features.reservations.application.dto.ReservationResponseDto;
import pl.cieszk.libraryapp.features.reservations.domain.Reservation;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ReservationMapper {

    Reservation toEntity(ReservationRequestDto reservationRequestDto);

    Reservation toEntity(ReservationResponseDto reservationResponseDto);
    ReservationResponseDto toResponseDto(Reservation reservation);
}
