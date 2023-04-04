package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.service.ItemRequestValidationService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ItemRequestValidationServiceTest {

    private ItemRequestValidationService itemRequestValidationService;

    @BeforeEach
    void setUp() {
        itemRequestValidationService = new ItemRequestValidationService();
    }

    @Test
    void validateItemRequestCreateOk() {
        assertDoesNotThrow(
                () -> itemRequestValidationService.validateItemRequestCreate(
                        ItemRequestDto.builder().description("some descr").build()
                )
        );
    }

    @Test
    void validateItemRequestCreateError() {
        assertThrows(ValidationException.class,
                () -> itemRequestValidationService.validateItemRequestCreate(
                        ItemRequestDto.builder().description("").build()
                )
        );

        assertThrows(ValidationException.class,
                () -> itemRequestValidationService.validateItemRequestCreate(
                        ItemRequestDto.builder().description("  ").build()
                )
        );

        assertThrows(ValidationException.class,
                () -> itemRequestValidationService.validateItemRequestCreate(
                        ItemRequestDto.builder().build()
                )
        );
    }
}