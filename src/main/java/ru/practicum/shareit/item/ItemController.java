package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping()
    public List<ItemDto> findAllItems(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        return itemService.getAll(ownerId);
    }

    @GetMapping(value = "/{itemId}")
    public ItemDto findItemById(@PathVariable("itemId") int itemId) {
        return itemService.getById(itemId);
    }
    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") int ownerId, @Valid @RequestBody ItemDto itemDto) {
        itemDto.setOwnerId(ownerId);
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") int ownerId,@PathVariable("itemId") int itemId, @RequestBody ItemDto itemDto) {
        itemDto.setOwnerId(ownerId);
        itemDto.setId(itemId);
        return itemService.update(ownerId, itemDto);
    }

    @GetMapping(value = "/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.findItemsBySearch(text);
    }
}
