package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.service.BookingValidationService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BookingValidationServiceTest {
    private BookingValidationService bookingValidationService;
    private Item item = Item
            .builder()
            .id(1L)
            .name("item name")
            .description("item description")
            .ownerId(1L)
            .available(true)
            .build();
    private BookingInputDto bookingDto = BookingInputDto.builder()
            .itemId(item.getId())
            .build();

    @BeforeEach
    void setUp() {
        bookingValidationService = new BookingValidationService();
    }

    @Test
    void testValidateBookingTime() {
        bookingDto.setStart(LocalDateTime.of(2023, 11, 11, 11, 11));
        bookingDto.setEnd(LocalDateTime.of(2023, 12, 12, 12, 12));
        assertDoesNotThrow(() -> bookingValidationService.validateTime(bookingDto));
    }

    @Test
    void testValidateTimeWhenStartOrEndNull() {
        bookingDto.setStart(null);
        bookingDto.setEnd(null);
        assertThrows(ValidationException.class, () -> bookingValidationService.validateTime(bookingDto));
    }

    @Test
    void testValidateTimeWhenStartOrEndBeforeNow() {
        bookingDto.setStart(LocalDateTime.now().minusDays(2));
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        assertThrows(ValidationException.class, () -> bookingValidationService.validateTime(bookingDto));
    }

    @Test
    void testValidateWhenEndIsBeforeStart() {
        bookingDto.setStart(LocalDateTime.of(2024, 11, 11, 11, 11));
        bookingDto.setEnd(LocalDateTime.of(2023, 12, 12, 12, 12));
        assertThrows(ValidationException.class, () -> bookingValidationService.validateTime(bookingDto));
    }

    @Test
    void testValidateWhenEndEqualsStart() {
        bookingDto.setStart(LocalDateTime.of(2023, 11, 11, 11, 11));
        bookingDto.setEnd(LocalDateTime.of(2023, 11, 11, 11, 11));
        assertThrows(ValidationException.class, () -> bookingValidationService.validateTime(bookingDto));
    }
}