package ru.practicum.shareit.item.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.GeneratorId;
import ru.practicum.shareit.item.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, List<Item>> items;
    private final GeneratorId generatorId;

    @Override
    public List<Item> getAll(int ownerId) {
        return items.get(ownerId);
    }

    @Override
    public List<Item> findItemsBySearch(String text) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getAvailable() && (item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text)))
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(int itemId) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    @Override
    public Item create(Item item) {
        isValid(item);

        item.setId(generatorId.generate());
        int ownerId = item.getOwnerId();
        if (!items.containsKey(ownerId)) {
            List<Item> itemsByUser = new ArrayList<>();
            itemsByUser.add(item);
            items.put(ownerId, itemsByUser);
        } else {
            items.get(ownerId).add(item);
        }
        return item;
    }

    @Override
    public Item update(Item item) {
        int ownerId = item.getOwnerId();
        if (items.get(ownerId) != null) {
            Item oldItem = items.get(ownerId).stream()
                    .filter(item1 -> item1.getId() == item.getId())
                    .findFirst()
                    .orElseThrow(() -> new UserNotFoundException(ownerId));
            if (item.getName() != null && item.getDescription() != null && item.getAvailable() != null) {
                items.get(ownerId).remove(oldItem);
                oldItem.setName(item.getName());
                oldItem.setDescription(item.getDescription());
                oldItem.setAvailable(item.getAvailable());
                items.get(ownerId).add(oldItem);
            } else if (item.getName() == null && item.getDescription() == null) {
                items.get(ownerId).remove(oldItem);
                oldItem.setAvailable(item.getAvailable());
                items.get(ownerId).add(oldItem);
            } else if (item.getName() == null && item.getAvailable() == null) {
                items.get(ownerId).remove(oldItem);
                oldItem.setDescription(item.getDescription());
                items.get(ownerId).add(oldItem);
            } else if (item.getDescription() == null && item.getAvailable() == null) {
                items.get(ownerId).remove(oldItem);
                oldItem.setName(item.getName());
                items.get(ownerId).add(oldItem);
            } else {
                throw new IllegalArgumentException("Вы пытаетесь обновить несуществующего Item'а.");
            }
            return oldItem;
        } else {
            throw new UserNotFoundException(ownerId);
        }
    }

    @Override
    public void delete(Item item) {

    }

    @Override
    public void deleteById(int itemId) {

    }

    public void isValid(Item item) {
        if (item.getName().isBlank() || item.getDescription().isBlank() || item.getAvailable() == null) {
            throw new IllegalArgumentException();
        }
    }
}
