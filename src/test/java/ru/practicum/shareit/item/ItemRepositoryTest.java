package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.item.ItemServiceUnitTest.FROM;
import static ru.practicum.shareit.item.ItemServiceUnitTest.SIZE;

@DataJpaTest
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"/schema.sql"})
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User user = User.builder()
            .name("name")
            .email("user1@ya.ru")
            .build();
    private Item item = Item.builder()
            .name("item name")
            .description("item description")
            .ownerId(user.getId())
            .available(true)
            .requestId(null)
            .build();
    private ItemRequest itemRequest = ItemRequest.builder()
            .description("i want item")
            .requestorId(1L)
            .created(LocalDateTime.now())
            .build();

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();

        user.setId(null);
        user = userRepository.save(user);
        itemRequest.setId(null);
        itemRequest = itemRequestRepository.save(itemRequest);

        item.setId(null);
        item.setOwnerId(user.getId());
        item.setRequestId(itemRequest.getId());
    }

    @Test
    public void testSaveItem() {
        Item savedItem = itemRepository.save(item);

        assertEquals(1, savedItem.getId());
        assertEquals(item.getOwnerId(), savedItem.getOwnerId());
    }

    @Test
    void shouldReturnItemsByOwnerIdWithAndWithoutPagination() {
        Item item2 = Item.builder()
                .name("item name2")
                .description("item description2")
                .ownerId(user.getId())
                .available(true)
                .requestId(null)
                .build();
        Item savedItem = itemRepository.save(item);
        Item savedItem2 = itemRepository.save(item2);

        List<Item> itemsByOwnerId = itemRepository.findAllByOwnerId(user.getId(), PageRequest.of(FROM, SIZE));
        assertEquals(2, itemsByOwnerId.size());
        assertEquals(2, itemRepository.findAllByOwnerId(user.getId()).size());
        assertEquals(savedItem.getId(), itemsByOwnerId.get(0).getId());
        assertEquals(savedItem2.getId(), itemsByOwnerId.get(1).getId());
    }

    @Test
    void shouldReturnAllItemsByRequestId() {
        Item savedItem = itemRepository.save(item);
        assertEquals(1, itemRepository.findAllByRequestId(itemRequest.getId()).size());
    }

    @Test
    void shouldReturnItemsBySearchingByText() {
        itemRepository.save(item);
        itemRepository.save(Item.builder()
                .name("item name2")
                .description("item description2")
                .ownerId(user.getId())
                .available(true)
                .requestId(null)
                .build());
        assertEquals(2, itemRepository.searchItemsByText("item", PageRequest.of(FROM, SIZE)).size());
        assertEquals(0, itemRepository.searchItemsByText("      ", PageRequest.of(FROM, SIZE)).size());
        assertEquals(1, itemRepository.searchItemsByText("item name2", PageRequest.of(FROM, SIZE)).size());
    }
}