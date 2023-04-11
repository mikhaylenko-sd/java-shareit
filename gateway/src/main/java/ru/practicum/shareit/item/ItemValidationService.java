package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

@Component
public class ItemValidationService {
    public void validateItemCreate(ItemDto itemDto) {
        validateName(itemDto);
        validateDescription(itemDto);
        validateAvailable(itemDto);
    }

    public void validateItemUpdate(ItemDto itemDto) {
        if (itemDto.getName() != null) {
            validateName(itemDto);
        }
        if (itemDto.getDescription() != null) {
            validateDescription(itemDto);
        }
    }

    private void validateName(ItemDto itemDto) {
        String name = itemDto.getName();
        if (name == null || name.isBlank()) {
            throw new ValidationException("Ошибка валидации. Имя вещи не может быть пустым.");
        }
    }

    private void validateDescription(ItemDto itemDto) {
        String description = itemDto.getDescription();
        if (description == null || description.isBlank()) {
            throw new ValidationException("Ошибка валидации. Описание вещи не может быть пустым.");
        }
    }

    private void validateAvailable(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Ошибка валидации. Необходимо указать доступность вещи.");
        }
    }
}

