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
    private final ItemMapper itemMapper;
    private final UserService userService;

    @Override
    public List<ItemDto> getAll(int ownerId) {
        return itemRepository.getAll(ownerId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(int itemId) {
        return itemMapper.toItemDto(itemRepository.getById(itemId));
    }

    @Override
    public ItemDto create(int ownerId, ItemDto itemDto) {
        userService.getById(ownerId);

        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDto(itemRepository.create(item));
    }

    @Override
    public ItemDto update(Integer ownerId, ItemDto itemDto) {
        userService.getById(ownerId);
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDto(itemRepository.update(item));
    }

    @Override
    public List<ItemDto> findItemsBySearch(String text) {
        if (!text.isBlank()) {
            return itemRepository.findItemsBySearch(text.toLowerCase()).stream()
                    .map(itemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void delete(ItemDto itemDto) {

    }

    @Override
    public void deleteById(int itemId) {

    }


}
