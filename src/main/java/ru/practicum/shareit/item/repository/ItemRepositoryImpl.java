package ru.practicum.shareit.item.repository;

import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRepositoryImpl implements ItemRepositoryCustom {
    private final ItemRepository itemRepository;

    public ItemRepositoryImpl(@Lazy ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Item> searchItemsByText(String text) {
        return itemRepository.findAll().stream()
                .filter(item -> item.getAvailable() && (item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text)))
                .collect(Collectors.toList());
    }
}
