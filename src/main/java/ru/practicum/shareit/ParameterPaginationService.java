package ru.practicum.shareit;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;

@Component
public class ParameterPaginationService {
    public void validateRequestParameters(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Значения параметра запроса from не могут быть отрицательными, а size - неположительными.");
        }
    }
}
