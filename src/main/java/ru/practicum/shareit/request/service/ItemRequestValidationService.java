package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.ItemRequestDto;

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

