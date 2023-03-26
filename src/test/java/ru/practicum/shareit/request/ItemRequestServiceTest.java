package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.item.ItemServiceUnitTest.FROM;
import static ru.practicum.shareit.item.ItemServiceUnitTest.SIZE;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private static int counter = 0;

    private static UserDto generateUser() {
        return UserDto
                .builder()
                .name("name " + (++counter))
                .email("user" + counter + "@ya.ru")
                .build();
    }

    private static ItemRequestDto generateItemRequest() {
        return ItemRequestDto.builder()
                .description("looking for item 4")
                .build();
    }

    @Test
    void testCreateItemRequest() {
        UserDto newUserDto = userService.create(generateUser());
        ItemRequestDto newItemRequestDto = generateItemRequest();
        ItemRequestDto itemRequestDto = itemRequestService.create(newUserDto.getId(), newItemRequestDto);

        assertEquals(newUserDto.getId(), itemRequestDto.getRequestorId());
        assertEquals(newItemRequestDto.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    void testGetAllRequests() {
        UserDto newUserDto = userService.create(generateUser());
        assertEquals(0, itemRequestService.getAllRequests(newUserDto.getId()).size());

        ItemRequestDto newItemRequestDto1 = generateItemRequest();
        ItemRequestDto newItemRequestDto2 = generateItemRequest();
        ItemRequestDto newItemRequestDto3 = generateItemRequest();
        ItemRequestDto itemRequestDto1 = itemRequestService.create(newUserDto.getId(), newItemRequestDto1);
        ItemRequestDto itemRequestDto2 = itemRequestService.create(newUserDto.getId(), newItemRequestDto2);
        ItemRequestDto itemRequestDto3 = itemRequestService.create(newUserDto.getId(), newItemRequestDto3);
        List<ItemRequestDto> requestDtoAfter = itemRequestService.getAllRequests(newUserDto.getId());

        assertEquals(3, requestDtoAfter.size());
        assertEquals(itemRequestDto1.getId(), requestDtoAfter.get(0).getId());
        assertEquals(itemRequestDto2.getId(), requestDtoAfter.get(1).getId());
        assertEquals(itemRequestDto3.getId(), requestDtoAfter.get(2).getId());
    }

    @Test
    void testGetItemRequestByRequestId() {
        UserDto newUserDto = userService.create(generateUser());
        ItemRequestDto itemRequestDto1 = itemRequestService.create(newUserDto.getId(), generateItemRequest());
        ItemRequestDto itemRequestDto2 = itemRequestService.create(newUserDto.getId(), generateItemRequest());
        ItemRequestDto returnedItemRequestDto1 = itemRequestService.getItemRequestById(newUserDto.getId(), itemRequestDto1.getId());
        ItemRequestDto returnedItemRequestDto2 = itemRequestService.getItemRequestById(newUserDto.getId(), itemRequestDto2.getId());

        assertEquals(itemRequestDto1.getId(), returnedItemRequestDto1.getId());
        assertEquals(itemRequestDto1.getDescription(), returnedItemRequestDto1.getDescription());
        assertEquals(itemRequestDto1.getCreated(), returnedItemRequestDto1.getCreated());
        assertEquals(itemRequestDto2.getId(), returnedItemRequestDto2.getId());
        assertEquals(itemRequestDto2.getDescription(), returnedItemRequestDto2.getDescription());
        assertEquals(itemRequestDto2.getCreated(), returnedItemRequestDto2.getCreated());
    }

    @Test
    void testGetAllItemRequestsByOtherRequestors() {
        UserDto newUserDto1 = userService.create(generateUser());
        UserDto newUserDto2 = userService.create(generateUser());
        ItemRequestDto itemRequestDto1 = itemRequestService.create(newUserDto2.getId(), generateItemRequest());
        ItemRequestDto itemRequestDto2 = itemRequestService.create(newUserDto2.getId(), generateItemRequest());
        ItemRequestDto returnedItemRequestDto1 = itemRequestService.getItemRequestById(newUserDto2.getId(), itemRequestDto1.getId());
        ItemRequestDto returnedItemRequestDto2 = itemRequestService.getItemRequestById(newUserDto2.getId(), itemRequestDto2.getId());

        List<ItemRequestDto> allItemRequestsByOtherRequestors = itemRequestService.getAllItemRequestsByOtherRequestors(newUserDto1.getId(), FROM, SIZE);
        assertEquals(2, allItemRequestsByOtherRequestors.size());
        assertEquals(returnedItemRequestDto2.getId(), allItemRequestsByOtherRequestors.get(0).getId());
        assertEquals(returnedItemRequestDto1.getId(), allItemRequestsByOtherRequestors.get(1).getId());
        assertTrue(allItemRequestsByOtherRequestors.get(0).getCreated().isAfter(allItemRequestsByOtherRequestors.get(1).getCreated()));
    }

}
