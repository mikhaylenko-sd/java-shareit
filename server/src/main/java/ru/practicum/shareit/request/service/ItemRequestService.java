package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllRequests(long userId);

    ItemRequestDto getItemRequestById(long userId, long requestId);

    List<ItemRequestDto> getAllItemRequestsByOtherRequestors(long userId, int from, int size);
}
