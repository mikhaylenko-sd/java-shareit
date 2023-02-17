package ru.practicum.shareit.user.service;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserDto;

@Component
public class UserValidationService {
    public void validateUserCreate(UserDto userDto) {
        validateName(userDto);
        validateEmail(userDto);
    }

    public void validateUserUpdate(UserDto userDto) {
        if (userDto.getName() != null) {
            validateName(userDto);
        }
        if (userDto.getEmail() != null) {
            validateEmail(userDto);
        }
    }

    private void validateEmail(UserDto userDto) {
        EmailValidator validator = EmailValidator.getInstance();
        if (!validator.isValid(userDto.getEmail())) {
            throw new ValidationException("Ошибка валидации. Проверьте корректность адреса электронной почты.");
        }
    }

    private void validateName(UserDto userDto) {
        String name = userDto.getName();
        if (name == null || name.isBlank()) {
            throw new ValidationException("Ошибка валидации. Имя пользователя не может быть пустым.");
        }
    }
}
