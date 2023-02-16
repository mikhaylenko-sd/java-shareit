package ru.practicum.shareit.user.service;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserDto;

@Component
public class UserValidationService {
    public boolean validateUserCreate(UserDto userDto) {
        if (validateEmailWhenCreate(userDto) && validateNameWhenCreate(userDto)) {
            return true;
        } else {
            throw new ValidationException("Validation exception");
        }
    }

    public boolean validateUserUpdate(UserDto userDto) {
        if (validateEmailWhenUpdate(userDto) && validateNameWhenUpdate(userDto)) {
            return true;
        } else {
            throw new ValidationException("Validation exception");
        }
    }

    private boolean validateEmailWhenCreate(UserDto userDto) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(userDto.getEmail());
    }

    private boolean validateEmailWhenUpdate(UserDto userDto) {
        if (userDto.getEmail() == null) {
            return true;
        }
        return validateEmailWhenCreate(userDto);
    }

    private boolean validateNameWhenCreate(UserDto userDto) {
        String name = userDto.getName();
        return name != null && !name.isBlank();
    }

    private boolean validateNameWhenUpdate(UserDto userDto) {
        String name = userDto.getName();
        if (name == null) {
            return true;
        }
        return !name.isBlank();
    }
}
