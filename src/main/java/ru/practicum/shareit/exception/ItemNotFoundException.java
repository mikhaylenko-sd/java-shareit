package ru.practicum.shareit.exception;

public class ItemNotFoundException extends InvalidIdException {
    public ItemNotFoundException(long id) {
        super(String.format("Item с id = %d не найден", id));
    }

    public ItemNotFoundException(final String message) {
        super(message);
    }
}
