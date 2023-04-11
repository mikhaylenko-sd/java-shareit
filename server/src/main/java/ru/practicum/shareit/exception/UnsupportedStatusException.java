package ru.practicum.shareit.exception;

public class UnsupportedStatusException extends IllegalArgumentException {
    public UnsupportedStatusException() {
        super("Проверьте значение параметра state.");
    }
}
