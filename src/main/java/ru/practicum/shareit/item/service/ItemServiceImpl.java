package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public List<ItemDto> getAllById(int ownerId) {
        return itemRepository.getAllByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(int itemId) {
        Item item = itemRepository.getById(itemId);
        if (item == null) {
            throw new ItemNotFoundException(itemId);
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto create(int ownerId, ItemDto itemDto) {
        userService.getById(ownerId);

        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemRepository.create(item));
    }

    @Override
    public ItemDto update(Integer ownerId, ItemDto itemDto) {
        userService.getById(ownerId);
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemRepository.update(item));
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItemsByText(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(int ownerId, ItemDto itemDto) {
        itemRepository.deleteItem(ownerId, ItemMapper.toItem(itemDto));
    }

    @Override
    public void deleteById(int ownerId, int itemId) {
        itemRepository.deleteById(ownerId, itemId);
    }

}
