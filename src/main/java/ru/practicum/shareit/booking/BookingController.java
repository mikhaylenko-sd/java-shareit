package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingValidationService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";
    private static final String BOOKING_ID_PATH_VARIABLE = "bookingId";
    private final BookingService bookingService;
    private final BookingValidationService bookingValidationService;

    @PostMapping
    public BookingOutputDto create(@RequestHeader(REQUEST_HEADER) long userId, @RequestBody BookingInputDto bookingDto) {
        log.info("Получен запрос к эндпоинту: {} {}", "POST", "/bookings");
        bookingValidationService.validateTime(bookingDto);
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping(value = "/{" + BOOKING_ID_PATH_VARIABLE + "}")
    public BookingOutputDto approveOrRejectBooking(@PathVariable(value = BOOKING_ID_PATH_VARIABLE) long bookingId, @RequestHeader(REQUEST_HEADER) long ownerId, @RequestParam(value = "approved") boolean approved) {
        log.info("Получен запрос к эндпоинту: {} /bookings/{}", "PATCH", bookingId);
        return bookingService.approveOrRejectBooking(bookingId, ownerId, approved);
    }

    @GetMapping(value = "/{" + BOOKING_ID_PATH_VARIABLE + "}")
    public BookingOutputDto getBookingByBookingId(@PathVariable(value = BOOKING_ID_PATH_VARIABLE) long bookingId, @RequestHeader(REQUEST_HEADER) long userId) {
        log.info("Получен запрос к эндпоинту: {} /bookings/{}", "GET", bookingId);
        return bookingService.getBookingByBookingId(bookingId, userId);
    }

    @GetMapping
    public List<BookingOutputDto> getAllBookingsByUserId(@RequestHeader(REQUEST_HEADER) long userId, @RequestParam(value = "state", defaultValue = "ALL") String state) {
        log.info("Получен запрос к эндпоинту: {} {}", "GET", "/bookings");
        return bookingService.getAllBookingsByUserId(userId, state);
    }

    @GetMapping(value = "/owner")
    public List<BookingOutputDto> getAllBookingsByOwner(@RequestHeader(REQUEST_HEADER) long ownerId, @RequestParam(value = "state", defaultValue = "ALL") String state) {
        log.info("Получен запрос к эндпоинту: {} {}", "GET", "/bookings/owner");
        return bookingService.getAllBookingsByOwnerId(ownerId, state);
    }
}
