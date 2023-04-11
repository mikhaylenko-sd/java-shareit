package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.ParameterPaginationService;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;
    private final ItemRequestValidationService itemRequestValidationService;
    private final ParameterPaginationService parameterPaginationService;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(REQUEST_HEADER) long userId, @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос к эндпоинту: {} {}", "POST", "/requests");
        itemRequestValidationService.validateItemRequestCreate(itemRequestDto);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(REQUEST_HEADER) long userId) {
        log.info("Получен запрос к эндпоинту: {} {}", "GET", "/requests");
        return itemRequestClient.getAllRequests(userId);
    }

    @GetMapping(value = "/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(REQUEST_HEADER) long userId, @PathVariable(value = "requestId") long requestId) {
        log.info("Получен запрос к эндпоинту: {} /requests/{}", "GET", requestId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Object> getAllItemRequestsByOtherRequestors(@RequestHeader(REQUEST_HEADER) long userId, @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                                                      @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("Получен запрос к эндпоинту: {} /requests/all/?from={}&size={}", "GET", from, size);
        parameterPaginationService.validateRequestParameters(from, size);
        return itemRequestClient.getAllItemRequestsByOtherRequestors(userId, from, size);
    }
}
