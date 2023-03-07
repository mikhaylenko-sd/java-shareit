package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
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
import ru.practicum.shareit.item.service.ItemValidationService;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";
    private static final String ITEM_ID_PATH_VARIABLE = "itemId";
    private final ItemService itemService;
    private final ItemValidationService itemValidationService;

    @GetMapping
    public List<ItemDto> findAllItems(@RequestHeader(REQUEST_HEADER) long ownerId) {
        return itemService.getAllByOwnerId(ownerId);
    }

    @GetMapping(value = "/{" + ITEM_ID_PATH_VARIABLE + "}")
    public ItemDto findItemByItemId(@RequestHeader(REQUEST_HEADER) long userId, @PathVariable(ITEM_ID_PATH_VARIABLE) long itemId) {
        return itemService.getById(userId, itemId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(REQUEST_HEADER) long ownerId, @RequestBody ItemDto itemDto) {
        itemDto.setOwnerId(ownerId);
        itemValidationService.validateItemCreate(itemDto);
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping(value = "/{" + ITEM_ID_PATH_VARIABLE + "}")
    public ItemDto patchItem(@RequestHeader(REQUEST_HEADER) long ownerId, @PathVariable(ITEM_ID_PATH_VARIABLE) long itemId, @RequestBody ItemDto itemDto) {
        itemDto.setOwnerId(ownerId);
        itemDto.setId(itemId);
        itemValidationService.validateItemUpdate(itemDto);
        return itemService.update(ownerId, itemDto);
    }

    @GetMapping(value = "/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItemsByText(text);
    }

    @DeleteMapping(value = "/{" + ITEM_ID_PATH_VARIABLE + "}")
    public void removeItemById(@RequestHeader(REQUEST_HEADER) long ownerId, @PathVariable(ITEM_ID_PATH_VARIABLE) long itemId) {
        itemService.deleteById(ownerId, itemId);
    }

    @DeleteMapping
    public void removeItem(@RequestHeader(REQUEST_HEADER) long ownerId, @RequestBody ItemDto itemDto) {
        itemService.deleteItem(ownerId, itemDto);
    }

    @PostMapping(value = "/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(REQUEST_HEADER) long userId, @PathVariable(ITEM_ID_PATH_VARIABLE) long itemId, @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }

}
