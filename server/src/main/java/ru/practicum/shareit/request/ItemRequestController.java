package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";
    private ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(REQUEST_HEADER) long userId, @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос к эндпоинту: {} {}", "POST", "/requests");
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(REQUEST_HEADER) long userId) {
        log.info("Получен запрос к эндпоинту: {} {}", "GET", "/requests");
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping(value = "/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader(REQUEST_HEADER) long userId, @PathVariable(value = "requestId") long requestId) {
        log.info("Получен запрос к эндпоинту: {} /requests/{}", "GET", requestId);
        return itemRequestService.getItemRequestById(userId, requestId);
    }

    @GetMapping(value = "/all")
    public List<ItemRequestDto> getAllItemRequestsByOtherRequestors(@RequestHeader(REQUEST_HEADER) long userId, @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                                                    @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("Получен запрос к эндпоинту: {} /requests/all/?from={}&size={}", "GET", from, size);
        return itemRequestService.getAllItemRequestsByOtherRequestors(userId, from, size);
    }
}
