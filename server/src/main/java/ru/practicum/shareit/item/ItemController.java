package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";
    private static final String ITEM_ID_PATH_VARIABLE = "itemId";
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findAllItems(@RequestHeader(REQUEST_HEADER) long ownerId, @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                      @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("Получен запрос к эндпоинту: {} /items?from={}&size={}", "GET", from, size);
        return itemService.getAllByOwnerId(ownerId, from, size);
    }

    @GetMapping(value = "/{" + ITEM_ID_PATH_VARIABLE + "}")
    public ItemDto findItemByItemId(@RequestHeader(REQUEST_HEADER) long userId, @PathVariable(ITEM_ID_PATH_VARIABLE) long itemId) {
        log.info("Получен запрос к эндпоинту: {} /items/{}", "GET", itemId);
        return itemService.getById(userId, itemId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(REQUEST_HEADER) long ownerId, @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту: {} {}", "POST", "/items");
        itemDto.setOwnerId(ownerId);
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping(value = "/{" + ITEM_ID_PATH_VARIABLE + "}")
    public ItemDto patchItem(@RequestHeader(REQUEST_HEADER) long ownerId, @PathVariable(ITEM_ID_PATH_VARIABLE) long itemId, @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту: {} /items/{}", "PATCH", itemId);
        itemDto.setOwnerId(ownerId);
        itemDto.setId(itemId);
        return itemService.update(ownerId, itemDto);
    }

    @GetMapping(value = "/search")
    public List<ItemDto> searchItems(@RequestParam String text, @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                     @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("Получен запрос к эндпоинту: {} /items/search?from={}&size={}", "GET", from, size);
        return itemService.searchItemsByText(text, from, size);
    }

    @DeleteMapping(value = "/{" + ITEM_ID_PATH_VARIABLE + "}")
    public void removeItemById(@RequestHeader(REQUEST_HEADER) long ownerId, @PathVariable(ITEM_ID_PATH_VARIABLE) long itemId) {
        log.info("Получен запрос к эндпоинту: {} /items/{}", "DELETE", itemId);
        itemService.deleteById(ownerId, itemId);
    }

    @DeleteMapping
    public void removeItem(@RequestHeader(REQUEST_HEADER) long ownerId, @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту: {} {}", "DELETE", "/items");
        itemService.deleteItem(ownerId, itemDto);
    }

    @PostMapping(value = "/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(REQUEST_HEADER) long userId, @PathVariable(ITEM_ID_PATH_VARIABLE) long itemId, @RequestBody CommentDto commentDto) {
        log.info("Получен запрос к эндпоинту: {} /items/{}/comment", "POST", itemId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}
