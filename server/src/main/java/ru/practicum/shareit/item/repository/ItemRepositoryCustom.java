package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemRepositoryCustom {
    List<Item> searchItemsByText(String text, PageRequest pageRequest);
}
