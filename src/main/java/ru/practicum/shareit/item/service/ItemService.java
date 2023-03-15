package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllByOwnerId(long ownerId);

    ItemDto getById(long userId, long itemId);

    ItemDto create(long ownerId, ItemDto itemDto);

    ItemDto update(long ownerId, ItemDto itemDto);

    void deleteItem(long ownerId, ItemDto itemDto);

    void deleteById(long ownerId, long itemId);

    List<ItemDto> searchItemsByText(String text);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
