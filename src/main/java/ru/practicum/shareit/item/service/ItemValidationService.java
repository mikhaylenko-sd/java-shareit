package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemDto;

@Component
public class ItemValidationService {
    public boolean validateItemCreate(ItemDto itemDto) {
        if (!areItemFieldsNull(itemDto) && !areItemFieldsBlank(itemDto)) {
            return true;
        } else {
            throw new ValidationException("Validation exception");
        }
    }

    public boolean validateItemUpdate(ItemDto itemDto) {
        if (validateNameWhenUpdate(itemDto) || validateDescriptionWhenUpdate(itemDto) || validateAvailableWhenUpdate(itemDto)) {
            return true;
        } else {
            throw new ValidationException("Validation exception");
        }
    }

    private boolean areItemFieldsBlank(ItemDto itemDto) {
        return itemDto.getName().isBlank() || itemDto.getDescription().isBlank();
    }

    private boolean areItemFieldsNull(ItemDto itemDto) {
        return itemDto.getName() == null || itemDto.getDescription() == null || itemDto.getAvailable() == null;
    }

    private boolean validateNameWhenUpdate(ItemDto itemDto) {
        String name = itemDto.getName();
        if (name == null) {
            return true;
        }
        return !name.isBlank();
    }

    private boolean validateDescriptionWhenUpdate(ItemDto itemDto) {
        String description = itemDto.getDescription();
        if (description == null) {
            return true;
        }
        return !description.isBlank();
    }

    private boolean validateAvailableWhenUpdate(ItemDto itemDto) {
        return itemDto.getAvailable() == null;
    }
}
