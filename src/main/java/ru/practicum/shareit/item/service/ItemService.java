package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAll(int ownerId);

    ItemDto getById(int itemId);

    ItemDto create(int ownerId, ItemDto itemDto);

    ItemDto update(Integer ownerId, ItemDto itemDto);

    void delete(ItemDto itemDto);

    void deleteById(int itemId);

    List<ItemDto> findItemsBySearch(String text);
}
