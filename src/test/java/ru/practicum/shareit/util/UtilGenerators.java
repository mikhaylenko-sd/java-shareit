package ru.practicum.shareit.util;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

public class UtilGenerators {
    private static long counter;

    public static UserDto generateUserDto() {
        return UserDto
                .builder()
                .name("name " + (++counter))
                .email("user" + counter + "@ya.ru")
                .build();
    }

    public static ItemDto generateItemDto(long ownerId) {
        return ItemDto
                .builder()
                .name("item " + (++counter))
                .description("description " + counter)
                .ownerId(ownerId)
                .available(true)
                .build();
    }

    public static BookingInputDto generateBookingInputDto(long itemId) {
        return BookingInputDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .build();
    }

    public static BookingInputDto generateBookingInputDto(long itemId, LocalDateTime start, LocalDateTime end) {
        return BookingInputDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();
    }
}
