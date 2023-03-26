package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.service.UserValidationService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserValidationServiceTest {

    private UserValidationService userValidationService;
    private final UserDto userDtoTest = UserDto
            .builder()
            .id(1L)
            .name("name1")
            .email("email1@ya.ru")
            .build();

    @BeforeEach
    void setUp() {
        userValidationService = new UserValidationService();
    }

    @Test
    void shouldValidateUserCreateWhenValidName() {
        assertDoesNotThrow(() -> userValidationService.validateUserCreate(userDtoTest));
    }

    @Test
    void shouldValidateUserCreateWhenValidEmail() {
        assertDoesNotThrow(() -> userValidationService.validateUserCreate(userDtoTest));
    }

    @Test
    void shouldValidateUserCreateWhenNameIsNull() {
        userDtoTest.setName(null);
        assertThrows(ValidationException.class, () -> userValidationService.validateUserCreate(userDtoTest));
    }

    @Test
    void shouldValidateUserCreateWhenEmailIsNull() {
        userDtoTest.setEmail(null);
        assertThrows(ValidationException.class, () -> userValidationService.validateUserCreate(userDtoTest));
    }

    @Test
    void shouldValidateUserCreateWhenEmptyName() {
        userDtoTest.setName("      ");
        assertThrows(ValidationException.class, () -> userValidationService.validateUserCreate(userDtoTest));
    }

    @Test
    void shouldValidateUserCreateWhenEmptyEmail() {
        userDtoTest.setEmail("      ");
        assertThrows(ValidationException.class, () -> userValidationService.validateUserCreate(userDtoTest));
    }

    @Test
    void shouldValidateUserCreateWhenInvalidEmail() {
        userDtoTest.setEmail("user1.ru");
        assertThrows(ValidationException.class, () -> userValidationService.validateUserCreate(userDtoTest));
    }

    @Test
    void shouldValidateUserUpdateWhenValidName() {
        assertDoesNotThrow(() -> userValidationService.validateUserUpdate(userDtoTest));
    }

    @Test
    void shouldValidateUserUpdateWhenValidEmail() {
        assertDoesNotThrow(() -> userValidationService.validateUserUpdate(userDtoTest));
    }

    @Test
    void shouldValidateUserUpdateWhenNameIsNull() {
        userDtoTest.setName(null);
        assertDoesNotThrow(() -> userValidationService.validateUserUpdate(userDtoTest));
    }

    @Test
    void shouldValidateUserUpdateWhenEmailIsNull() {
        userDtoTest.setEmail(null);
        assertDoesNotThrow(() -> userValidationService.validateUserUpdate(userDtoTest));
    }

    @Test
    void shouldValidateUserUpdateWhenEmptyName() {
        userDtoTest.setName("      ");
        assertThrows(ValidationException.class, () -> userValidationService.validateUserUpdate(userDtoTest));
    }

    @Test
    void shouldValidateUserUpdateWhenEmptyEmail() {
        userDtoTest.setEmail("      ");
        assertThrows(ValidationException.class, () -> userValidationService.validateUserUpdate(userDtoTest));
    }

    @Test
    void shouldValidateUserUpdateWhenInvalidEmail() {
        userDtoTest.setEmail("user1.ru");
        assertThrows(ValidationException.class, () -> userValidationService.validateUserUpdate(userDtoTest));
    }
}