package ru.practicum.shareit.booking;

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
public class BookingController {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;
    private final BookingValidationService bookingValidationService;

    public BookingController(BookingService bookingService, BookingValidationService bookingValidationService) {
        this.bookingService = bookingService;
        this.bookingValidationService = bookingValidationService;
    }

    @PostMapping
    public BookingOutputDto create(@RequestHeader(REQUEST_HEADER) long userId, @RequestBody BookingInputDto bookingDto) {
        bookingValidationService.validateTime(bookingDto);
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingOutputDto approveOrRejectBooking(@PathVariable(value = "bookingId") long bookingId, @RequestHeader(REQUEST_HEADER) long ownerId, @RequestParam(value = "approved") boolean approved) {
        return bookingService.approveOrRejectBooking(bookingId, ownerId, approved);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingOutputDto getBookingByBookingId(@PathVariable(value = "bookingId") long bookingId, @RequestHeader(REQUEST_HEADER) long userId) {
        return bookingService.getBookingByBookingId(bookingId, userId);
    }

    @GetMapping
    public List<BookingOutputDto> getAllBookingsByUserId(@RequestHeader(REQUEST_HEADER) long userId, @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsByUserId(userId, state);
    }

    @GetMapping(value = "/owner")
    public List<BookingOutputDto> getAllBookingsByOwner(@RequestHeader(REQUEST_HEADER) long ownerId, @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsByOwnerId(ownerId, state);
    }
}
