package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserService userService,
                                  ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userService = userService;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {
        userService.getById(userId);

        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setRequestorId(userId);

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId) {
        userService.getById(userId);
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findAllByRequestorId(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());
        itemRequestDtos.forEach(this::setRequestItems);

        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getItemRequestById(long userId, long requestId) {
        userService.getById(userId);

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequestRepository.findById(requestId).orElseThrow(() -> new ItemRequestNotFoundException(requestId)));
        setRequestItems(itemRequestDto);

        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequestsByOtherRequestors(long userId, int from, int size) {
        userService.getById(userId);
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findAllByIdNotOrderByCreatedDesc(userId, PageRequest.of(from, size)).stream()
                .map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        itemRequestDtos.forEach(this::setRequestItems);

        return itemRequestDtos;
    }

    private void setRequestItems(ItemRequestDto itemRequestDto) {
        List<ItemDto> itemDtos = itemRepository.findAllByRequestId(itemRequestDto.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        itemRequestDto.setItems(itemDtos);
    }

    private void containsItemRequest(long requestId) {
        if (itemRequestRepository.countItemRequestById(requestId) == 0) {
            throw new ItemRequestNotFoundException(requestId);
        }
    }
}
