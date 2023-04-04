package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.service.ItemValidationService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ItemValidationServiceTest {

    private ItemValidationService itemValidationService;
    private ItemDto itemDtoTest = ItemDto
            .builder()
            .id(1L)
            .name("ItemName")
            .description("description")
            .ownerId(1L)
            .available(true)
            .build();

    @BeforeEach
    void setUp() {
        itemValidationService = new ItemValidationService();
    }

    @Test
    void shouldValidateItemCreateWhenValidParameters() {
        assertDoesNotThrow(() -> itemValidationService.validateItemCreate(itemDtoTest));
    }

    @Test
    void shouldValidateItemCreateWhenNameIsNull() {
        itemDtoTest.setName(null);
        assertThrows(ValidationException.class, () -> itemValidationService.validateItemCreate(itemDtoTest));
    }

    @Test
    void shouldValidateItemCreateWhenDescriptionIsNull() {
        itemDtoTest.setDescription(null);
        assertThrows(ValidationException.class, () -> itemValidationService.validateItemCreate(itemDtoTest));
    }

    @Test
    void shouldValidateItemCreateWhenAvailableIsNull() {
        itemDtoTest.setAvailable(null);
        assertThrows(ValidationException.class, () -> itemValidationService.validateItemCreate(itemDtoTest));
    }

    @Test
    void shouldValidateItemCreateWhenEmptyName() {
        itemDtoTest.setName("      ");
        assertThrows(ValidationException.class, () -> itemValidationService.validateItemCreate(itemDtoTest));
    }

    @Test
    void shouldValidateItemCreateWhenEmptyDescription() {
        itemDtoTest.setDescription("      ");
        assertThrows(ValidationException.class, () -> itemValidationService.validateItemCreate(itemDtoTest));
    }

    @Test
    void shouldValidateItemUpdateWhenValidParameters() {
        assertDoesNotThrow(() -> itemValidationService.validateItemUpdate(itemDtoTest));
    }

    @Test
    void shouldValidateItemUpdateWhenNameIsNull() {
        itemDtoTest.setName(null);
        assertDoesNotThrow(() -> itemValidationService.validateItemUpdate(itemDtoTest));
    }

    @Test
    void shouldValidateItemUpdateWhenDescriptionIsNull() {
        itemDtoTest.setDescription(null);
        assertDoesNotThrow(() -> itemValidationService.validateItemUpdate(itemDtoTest));
    }

    @Test
    void shouldValidateItemUpdateWhenAvailableIsNull() {
        itemDtoTest.setAvailable(null);
        assertDoesNotThrow(() -> itemValidationService.validateItemUpdate(itemDtoTest));
    }

    @Test
    void shouldValidateItemUpdateWhenEmptyName() {
        itemDtoTest.setName("      ");
        assertThrows(ValidationException.class, () -> itemValidationService.validateItemUpdate(itemDtoTest));
    }

    @Test
    void shouldValidateItemUpdateWhenEmptyDescription() {
        itemDtoTest.setDescription("      ");
        assertThrows(ValidationException.class, () -> itemValidationService.validateItemUpdate(itemDtoTest));
    }
}