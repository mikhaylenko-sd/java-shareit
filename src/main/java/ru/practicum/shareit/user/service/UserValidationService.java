package ru.practicum.shareit.user.service;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserDto;

@Component
public class UserValidationService {
    public void validateUserCreate(UserDto userDto) {
        if (userDto.getName() == null) {
            throw new ValidationException("Ошибка валидации. Имя пользователя не может быть пустым.");
        }
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
        boolean isNameBlank = userDto.getName().isBlank();
        if (isNameBlank) {
            throw new ValidationException("Ошибка валидации. Имя пользователя не может быть пустым.");
        }
    }
}
