package ru.practicum.shareit.exception;

public class InvalidIdException extends RuntimeException {
    public InvalidIdException(String m) {
        super(m);
    }
}
