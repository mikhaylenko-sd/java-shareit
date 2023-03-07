package ru.practicum.shareit.exception;

public class BookingNotFoundException extends InvalidIdException {
    public BookingNotFoundException(long id) {
        super(String.format("Бронирование с id = %d не найдено", id));
    }

    public BookingNotFoundException(final String message) {
        super(message);
    }
}
