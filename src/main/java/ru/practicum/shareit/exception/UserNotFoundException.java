package ru.practicum.shareit.exception;

public class UserNotFoundException extends InvalidIdException {
    public UserNotFoundException(Integer id) {
        super(String.format("User с id = %d не найден", id));
    }

    public UserNotFoundException(final String message) {
        super(message);
    }
}
