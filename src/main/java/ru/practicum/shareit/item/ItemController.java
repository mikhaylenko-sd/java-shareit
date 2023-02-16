package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemValidationService;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemValidationService itemValidationService;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";
    private static final String ITEM_ID_PATH_VARIABLE = "itemId";

    @GetMapping
    public List<ItemDto> findAllItems(@RequestHeader(REQUEST_HEADER) int ownerId) {
        return itemService.getAllById(ownerId);
    }

    @GetMapping(value = "/{" + ITEM_ID_PATH_VARIABLE + "}")
    public ItemDto findItemById(@PathVariable(ITEM_ID_PATH_VARIABLE) int itemId) {
        return itemService.getById(itemId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(REQUEST_HEADER) int ownerId, @RequestBody ItemDto itemDto) {
        itemDto.setOwnerId(ownerId);
        itemValidationService.validateItemCreate(itemDto);
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping(value = "/{" + ITEM_ID_PATH_VARIABLE + "}")
    public ItemDto patchItem(@RequestHeader(REQUEST_HEADER) int ownerId, @PathVariable(ITEM_ID_PATH_VARIABLE) int itemId, @RequestBody ItemDto itemDto) {
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
    public void removeItemById(@RequestHeader(REQUEST_HEADER) int ownerId, @PathVariable(ITEM_ID_PATH_VARIABLE) int itemId) {
        itemService.deleteById(ownerId, itemId);
    }

    @DeleteMapping
    public void removeItem(@RequestHeader(REQUEST_HEADER) int ownerId, @RequestBody ItemDto itemDto) {
        itemService.deleteItem(ownerId, itemDto);
    }
}
