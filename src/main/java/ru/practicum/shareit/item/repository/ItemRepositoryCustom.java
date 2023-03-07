package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemRepositoryCustom {
    List<Item> searchItemsByText(String text);
}
