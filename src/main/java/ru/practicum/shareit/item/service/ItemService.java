package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllById(int ownerId);

    ItemDto getById(int itemId);

    ItemDto create(int ownerId, ItemDto itemDto);

    ItemDto update(Integer ownerId, ItemDto itemDto);

    void deleteItem(int ownerId, ItemDto itemDto);

    void deleteById(int ownerId, int itemId);

    List<ItemDto> searchItemsByText(String text);
}
