package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getAll(int ownerId);
    List<Item> findItemsBySearch(String text);

    Item getById(int itemId);

    Item create(Item item);

    Item update(Item item);

    void delete(Item item);

    void deleteById(int itemId);
}
