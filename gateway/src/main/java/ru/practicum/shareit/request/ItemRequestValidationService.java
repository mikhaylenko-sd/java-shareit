package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;

@Component
public class ItemRequestValidationService {
    public void validateItemRequestCreate(ItemRequestDto itemRequestDto) {
        validateDescription(itemRequestDto);
    }

    private void validateDescription(ItemRequestDto itemRequestDto) {
        String description = itemRequestDto.getDescription();
        if (description == null || description.isBlank()) {
            throw new ValidationException("Ошибка валидации. Описание запроса вещи не может быть пустым.");
        }
    }
}

