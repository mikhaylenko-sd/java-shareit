package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

@Component
public class BookingValidationService {

    public void validateTime(BookingInputDto bookingDto) {
        LocalDateTime now = LocalDateTime.now().minusMinutes(1);
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start.isBefore(now) || end.isBefore(now)) {
            throw new ValidationException("Дата и время не могут быть в прошлом.");
        }
        if (end.isBefore(start)) {
            throw new ValidationException("Дата и время оканчания бронирования не могут быть раньше начала.");
        }
    }
}
