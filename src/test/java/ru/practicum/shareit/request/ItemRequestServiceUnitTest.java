package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceUnitTest {
    private ItemRequestService itemRequestService;
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;

    private final ItemRequestDto itemRequestDto = ItemRequestDto
            .builder()
            .id(1L)
            .description("Item request DTO description")
            .build();

    private final ItemRequest itemRequest1 = ItemRequest
            .builder()
            .id(1L)
            .created(LocalDateTime.now())
            .description("Item request description 1 from user 1")
            .requestorId(1L)
            .build();

    private final ItemRequest itemRequest2 = ItemRequest
            .builder()
            .id(2L)
            .created(LocalDateTime.now())
            .description("Item request description 2 from user 1")
            .requestorId(1L)
            .build();

    private final ItemRequest itemRequest3 = ItemRequest
            .builder()
            .id(3L)
            .created(LocalDateTime.now())
            .description("Item request description 3 from user 2")
            .requestorId(2L)
            .build();

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService, itemRepository);
    }

    @Test
    void shouldCreateItemRequest() {
        when(itemRequestRepository.save(any()))
                .thenReturn(ItemRequestMapper.toItemRequest(itemRequestDto));

        ItemRequestDto itemRequestDtoToSend = mock(ItemRequestDto.class);
        ItemRequestDto itemRequestDtoResult = itemRequestService.create(1L, itemRequestDtoToSend);

        verify(itemRequestDtoToSend).setRequestorId(anyLong());
        verify(itemRequestDtoToSend).setCreated(any());
        verify(itemRequestDtoToSend, never()).setItems(any());
        verify(itemRequestDtoToSend, never()).setDescription(anyString());
        verify(itemRequestDtoToSend, never()).setId(anyLong());

        assertEquals(itemRequestDto.getDescription(), itemRequestDtoResult.getDescription());
        assertEquals(1L, itemRequestDtoResult.getId());
    }

    @Test
    void shouldReturnEmptyItemRequestsList() {
        when(itemRequestRepository.findAllByRequestorId(1L))
                .thenReturn(Collections.emptyList());

        assertEquals(Collections.emptyList(), itemRequestService.getAllRequests(1L));
    }

    @Test
    void shouldReturnAllItemRequests() {
        when(itemRequestRepository.findAllByRequestorId(1L))
                .thenReturn(List.of(itemRequest1, itemRequest2));
        when(itemRepository.findAllByRequestId(2L))
                .thenReturn(List.of(
                        Item.builder().id(1L).requestId(itemRequest2.getId()).build(),
                        Item.builder().id(2L).requestId(itemRequest2.getId()).build()
                ));
        when(itemRepository.findAllByRequestId(1L))
                .thenReturn(Collections.emptyList());


        List<ItemRequestDto> itemRequests = itemRequestService.getAllRequests(1L);
        assertEquals(2, itemRequests.size());
        assertEquals(itemRequest1.getId(), itemRequests.get(0).getId());
        assertEquals(itemRequest1.getDescription(), itemRequests.get(0).getDescription());
        assertEquals(itemRequest1.getRequestorId(), itemRequests.get(0).getRequestorId());
        assertNotNull(itemRequests.get(0).getCreated());


        assertEquals(itemRequest2.getId(), itemRequests.get(1).getId());
        assertEquals(itemRequest2.getDescription(), itemRequests.get(1).getDescription());
        assertEquals(itemRequest2.getRequestorId(), itemRequests.get(1).getRequestorId());
        assertNotNull(itemRequests.get(1).getCreated());

        assertEquals(2, itemRequests.get(1).getItems().size());
        assertEquals(2, itemRequests.get(1).getItems().get(0).getRequestId());
        assertEquals(2, itemRequests.get(1).getItems().get(1).getRequestId());
    }

    @Test
    void shouldReturnItemRequestById() {
        when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(itemRequest1));
        when(itemRepository.findAllByRequestId(1L))
                .thenReturn(List.of(
                        Item.builder().id(1L).requestId(itemRequest1.getId()).build(),
                        Item.builder().id(2L).requestId(itemRequest1.getId()).build()
                ));

        ItemRequestDto itemRequestDtoResult = itemRequestService.getItemRequestById(1L, itemRequest1.getId());

        assertEquals(itemRequest1.getId(), itemRequestDtoResult.getId());
        assertEquals(itemRequest1.getDescription(), itemRequestDtoResult.getDescription());
        assertEquals(itemRequest1.getRequestorId(), itemRequestDtoResult.getRequestorId());
        assertNotNull(itemRequestDtoResult.getCreated());

        assertEquals(2, itemRequestDtoResult.getItems().size());
        assertEquals(1, itemRequestDtoResult.getItems().get(0).getRequestId());
        assertEquals(1, itemRequestDtoResult.getItems().get(1).getRequestId());


        when(itemRequestRepository.findById(2L))
                .thenReturn(Optional.empty());
        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getItemRequestById(1L, 2L));
    }

    @Test
    void shouldReturnAllItemRequestsByOtherRequestors() {
        when(itemRequestRepository.findAllByIdNotOrderByCreatedDesc(1L, PageRequest.of(0, 10)))
                .thenReturn(List.of(itemRequest3));
        when(itemRequestRepository.findAllByIdNotOrderByCreatedDesc(2L, PageRequest.of(0, 10)))
                .thenReturn(List.of(itemRequest1, itemRequest2));


        List<ItemRequestDto> itemRequestsForNotFirstUser = itemRequestService.getAllItemRequestsByOtherRequestors(1L, 0, 10);
        List<ItemRequestDto> itemRequestsForNotSecondUser = itemRequestService.getAllItemRequestsByOtherRequestors(2L, 0, 10);

        assertEquals(1, itemRequestsForNotFirstUser.size());
        assertEquals(itemRequest3.getId(), itemRequestsForNotFirstUser.get(0).getId());
        assertEquals(2, itemRequestsForNotSecondUser.size());

        assertEquals(itemRequest1.getId(), itemRequestsForNotSecondUser.get(0).getId());
        assertEquals(itemRequest2.getId(), itemRequestsForNotSecondUser.get(1).getId());
    }
}
