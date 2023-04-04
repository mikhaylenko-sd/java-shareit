package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ParameterPaginationServiceTest {
    private ParameterPaginationService parameterPaginationService;

    @BeforeEach
    void setUp() {
        parameterPaginationService = new ParameterPaginationService();
    }

    @Test
    void validateWhenValidRequestParameters() {
        assertDoesNotThrow(() -> parameterPaginationService.validateRequestParameters(1, 2));
        assertDoesNotThrow(() -> parameterPaginationService.validateRequestParameters(15, 55));
    }

    @Test
    void validateWhenOneOfRequestParametersIsValid() {
        assertThrows(ValidationException.class, () -> parameterPaginationService.validateRequestParameters(-1, 1));
        assertThrows(ValidationException.class, () -> parameterPaginationService.validateRequestParameters(0, 0));
    }

    @Test
    void validateWhenInValidRequestParameters() {
        assertThrows(ValidationException.class, () -> parameterPaginationService.validateRequestParameters(-200, -200));
        assertThrows(ValidationException.class, () -> parameterPaginationService.validateRequestParameters(-1, 0));
    }
}