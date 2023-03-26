package ru.practicum.shareit.item.comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentDto {
    Long id;
    String text;
    String authorName;
    LocalDateTime created;
    Long itemId;
}
