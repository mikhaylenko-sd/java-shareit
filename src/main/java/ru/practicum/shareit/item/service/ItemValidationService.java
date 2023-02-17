package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemDto;

@Component
public class ItemValidationService {
    public void validateItemCreate(ItemDto itemDto) {
        if (itemDto.getName() == null) {
            throw new ValidationException("Ошибка валидации. Имя вещи не может быть пустым.");
        }
        if (itemDto.getDescription() == null) {
            throw new ValidationException("Ошибка валидации. Описание вещи не может быть пустым.");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Ошибка валидации. Необходимо указать доступность вещи.");
        }

        validateName(itemDto);
        validateDescription(itemDto);
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
        boolean isNameBlank = itemDto.getName().isBlank();
        if (isNameBlank) {
            throw new ValidationException("Ошибка валидации. Имя вещи не может быть пустым.");
        }
    }

    private void validateDescription(ItemDto itemDto) {
        boolean isDescriptionBlank = itemDto.getDescription().isBlank();
        if (isDescriptionBlank) {
            throw new ValidationException("Ошибка валидации. Описание вещи не может быть пустым.");
        }
    }
}

