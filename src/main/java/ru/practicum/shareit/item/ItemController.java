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

    @GetMapping()
    public List<ItemDto> findAllItems(@RequestHeader(REQUEST_HEADER) int ownerId) {
        return itemService.getAllById(ownerId);
    }

    @GetMapping(value = "/{itemId}")
    public ItemDto findItemById(@PathVariable("itemId") int itemId) {
        return itemService.getById(itemId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(REQUEST_HEADER) int ownerId, @RequestBody ItemDto itemDto) {
        itemDto.setOwnerId(ownerId);
        itemValidationService.validateItemCreate(itemDto);
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto patchItem(@RequestHeader(REQUEST_HEADER) int ownerId, @PathVariable("itemId") int itemId, @RequestBody ItemDto itemDto) {
        itemDto.setOwnerId(ownerId);
        itemDto.setId(itemId);
        itemValidationService.validateItemUpdate(itemDto);
        return itemService.update(ownerId, itemDto);
    }

    @GetMapping(value = "/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItemsByText(text);
    }

    @DeleteMapping(value = "/{itemId}")
    public void removeItemById(@RequestHeader(REQUEST_HEADER) int ownerId, @PathVariable("itemId") int itemId) {
        itemService.deleteById(ownerId, itemId);
    }

    @DeleteMapping
    public void removeItem(@RequestHeader(REQUEST_HEADER) int ownerId, @RequestBody ItemDto itemDto) {
        itemService.deleteItem(ownerId, itemDto);
    }
}
