package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.ItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemRequestDto {
    Long id;
    String description;
    Long requestorId;
    LocalDateTime created;
    List<ItemDto> items;
}
