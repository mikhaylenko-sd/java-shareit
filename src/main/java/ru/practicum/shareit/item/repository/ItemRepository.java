package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getAllByOwnerId(int ownerId);

    List<Item> searchItemsByText(String text);

    Item getById(int itemId);

    Item create(Item item);

    Item update(Item item);

    void deleteItem(int ownerId, Item item);

    void deleteById(int ownerId, int itemId);
}
