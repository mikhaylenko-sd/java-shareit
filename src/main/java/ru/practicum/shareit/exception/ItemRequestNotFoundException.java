package ru.practicum.shareit.exception;

public class ItemRequestNotFoundException extends InvalidIdException {
    public ItemRequestNotFoundException(long id) {
        super(String.format("Запрос вещи с id = %d не найден", id));
    }

    public ItemRequestNotFoundException(final String message) {
        super(message);
    }
}
