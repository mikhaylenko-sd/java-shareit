package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.CommentCreationException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.item.ItemServiceUnitTest.FROM;
import static ru.practicum.shareit.item.ItemServiceUnitTest.SIZE;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private static int counter = 0;

    private UserDto newUserDto;

    private static UserDto generateUser() {
        return UserDto
                .builder()
                .name("name " + (++counter))
                .email("user" + counter + "@ya.ru")
                .build();
    }

    private static ItemDto generateItem(long ownerId) {
        return ItemDto
                .builder()
                .name("item " + (++counter))
                .description("description " + counter)
                .ownerId(ownerId)
                .available(true)
                .build();
    }

    private static CommentDto generateComment() {
        return CommentDto.builder()
                .text("nice Item")
                .build();
    }

    @BeforeEach
    void createDto() {
        newUserDto = userService.create(generateUser());
    }

    @Test
    void testGetAllItemsByOwnerId() {
        assertEquals(0, itemService.getAllByOwnerId(newUserDto.getId(), FROM, SIZE).size());

        ItemDto newItemDto1 = itemService.create(newUserDto.getId(), generateItem(newUserDto.getId()));
        ItemDto newItemDto2 = itemService.create(newUserDto.getId(), generateItem(newUserDto.getId()));

        List<ItemDto> allByOwnerId = itemService.getAllByOwnerId(newUserDto.getId(), FROM, SIZE);
        assertEquals(2, allByOwnerId.size());
        assertEquals(newItemDto1, allByOwnerId.get(0));
        assertEquals(newItemDto2, allByOwnerId.get(1));
    }

    @Test
    void testGetAllEmptyListByOwnerId() {
        assertEquals(0, itemService.getAllByOwnerId(newUserDto.getId(), FROM, SIZE).size());
    }

    @Test
    void testGetItemById() {
        ItemDto itemDto = generateItem(newUserDto.getId());

        ItemDto newItemDto = itemService.create(newUserDto.getId(), itemDto);
        ItemDto returnItemDto = itemService.getById(newUserDto.getId(), newItemDto.getId());

        assertThat(returnItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(returnItemDto.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void testCreateItem() {
        ItemDto itemDto = itemService.create(newUserDto.getId(), generateItem(newUserDto.getId()));
        ItemDto returnItemDto = itemService.getById(newUserDto.getId(), itemDto.getId());

        assertEquals(itemDto.getId(), returnItemDto.getId());
        assertEquals(itemDto.getName(), returnItemDto.getName());
        assertEquals(itemDto.getDescription(), returnItemDto.getDescription());
    }

    @Test
    void testExceptionCreateItemByNotExistUser() {
        ItemDto itemDto = generateItem(newUserDto.getId());
        assertThrows(UserNotFoundException.class, () -> itemService.create(111L, itemDto));
    }

    @Test
    void testUpdateItem() {
        ItemDto itemDtoCreate = itemService.create(newUserDto.getId(), generateItem(newUserDto.getId()));
        ItemDto returnItemDtoBefore = itemService.getById(newUserDto.getId(), itemDtoCreate.getId());

        itemDtoCreate.setName("new name");
        ItemDto itemDtoUpdate = itemService.update(itemDtoCreate.getOwnerId(), itemDtoCreate);
        ItemDto returnItemDtoAfter = itemService.getById(newUserDto.getId(), itemDtoUpdate.getId());

        assertNotEquals(returnItemDtoBefore, returnItemDtoAfter);
        assertEquals(returnItemDtoBefore.getId(), returnItemDtoAfter.getId());
        assertNotEquals(returnItemDtoBefore.getName(), returnItemDtoAfter.getName());
    }

    @Test
    void testExceptionUpdateItemByNotExistItem() {
        assertThrows(ItemNotFoundException.class, () -> itemService.update(newUserDto.getId(), generateItem(newUserDto.getId())));
    }

    @Test
    void testDeleteItem() {
        ItemDto itemDto = itemService.create(newUserDto.getId(), generateItem(newUserDto.getId()));
        assertEquals(1, itemService.getAllByOwnerId(newUserDto.getId(), FROM, SIZE).size());

        itemService.deleteItem(newUserDto.getId(), itemDto);
        assertEquals(0, itemService.getAllByOwnerId(newUserDto.getId(), FROM, SIZE).size());
    }

    @Test
    void testDeleteItemById() {
        ItemDto itemDto = itemService.create(newUserDto.getId(), generateItem(newUserDto.getId()));
        assertEquals(1, itemService.getAllByOwnerId(newUserDto.getId(), FROM, SIZE).size());

        itemService.deleteById(newUserDto.getId(), itemDto.getId());
        assertEquals(0, itemService.getAllByOwnerId(newUserDto.getId(), FROM, SIZE).size());
    }

    @Test
    void testReturnItemsBySearching() {
        itemService.create(newUserDto.getId(), generateItem(newUserDto.getId()));
        itemService.create(newUserDto.getId(), generateItem(newUserDto.getId()));
        assertEquals(2, itemService.searchItemsByText("item", FROM, SIZE).size());
    }

    @Test
    void testExceptionAddCommentWhenUserIsNotBooker() {
        ItemDto newItemDto = itemService.create(newUserDto.getId(), generateItem(newUserDto.getId()));
        assertThrows(CommentCreationException.class, () -> itemService.addComment(newUserDto.getId(), newItemDto.getId(), generateComment()));
    }
}
