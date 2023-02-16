package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.GeneratorId;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, List<Item>> items = new HashMap<>();
    private final GeneratorId generatorId = new GeneratorId();

    @Override
    public List<Item> getAllByOwnerId(int ownerId) {
        return items.get(ownerId);
    }

    @Override
    public List<Item> searchItemsByText(String text) {
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
                .get();
    }

    @Override
    public Item create(Item item) {
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
        if (!items.containsKey(ownerId)) {
            throw new UserNotFoundException(ownerId);
        }
        int itemId = item.getId();
        List<Item> itemsByOwnerId = items.get(ownerId);
        if (itemsByOwnerId == null) {
            throw new IllegalArgumentException("Вы пытаетесь обновить несуществующую вещь.");
        }
        Item oldItem = itemsByOwnerId.stream()
                .filter(item1 -> item1.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        itemsByOwnerId.remove(oldItem);
        merge(oldItem, item);
        itemsByOwnerId.add(oldItem);
        return oldItem;
    }

    private void merge(Item oldItem, Item newItem) {
        String name = newItem.getName();
        String description = newItem.getDescription();
        Boolean available = newItem.getAvailable();

        if (name != null) {
            oldItem.setName(name);
        }
        if (description != null) {
            oldItem.setDescription(description);
        }
        if (available != null) {
            oldItem.setAvailable(available);
        }
    }

    @Override
    public void deleteItem(int ownerId, Item item) {
        getAllByOwnerId(ownerId).remove(item);
    }

    @Override
    public void deleteById(int ownerId, int itemId) {
        getAllByOwnerId(ownerId).removeIf(item -> item.getId() == itemId);
    }

}
