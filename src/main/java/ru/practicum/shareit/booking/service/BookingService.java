package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;

import java.util.List;

public interface BookingService {
    BookingOutputDto createBooking(long userId, BookingInputDto bookingDto);

    BookingOutputDto approveOrRejectBooking(long bookingId, long ownerId, boolean approved);

    BookingOutputDto getBookingByBookingId(long bookingId, long userId);

    List<BookingOutputDto> getAllBookingsByOwnerId(long ownerId, String state, int from, int size);

    List<BookingOutputDto> getAllBookingsByUserId(long userId, String state, int from, int size);
}
