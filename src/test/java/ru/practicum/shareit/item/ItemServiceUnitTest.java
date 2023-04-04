package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.CommentCreationException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
public class ItemServiceUnitTest {
    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private CommentRepository commentRepository;

    private User user = User
            .builder()
            .id(1L)
            .name("name")
            .email("email@example.com")
            .build();
    private UserDto userDto = UserDto.builder()
            .id(1L)
            .name("name")
            .email("user1@ya.ru")
            .build();
    private Item item = Item
            .builder()
            .id(1L)
            .name("item name")
            .description("item description")
            .ownerId(user.getId())
            .available(true)
            .build();

    private ItemDto itemDto = ItemDto
            .builder()
            .id(1L)
            .name("item name")
            .description("item description")
            .ownerId(user.getId())
            .available(true)
            .nextBooking(null)
            .lastBooking(null)
            .comments(List.of())
            .build();

    private Booking booking = Booking.builder()
            .id(1L)
            .booker(user)
            .item(item)
            .build();

    private CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("comment")
            .build();

    private Comment comment = Comment.builder()
            .id(1L)
            .author(user)
            .text("comment")
            .build();

    public static final int FROM = 0;
    public static final int SIZE = 10;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, commentRepository);
    }

    @Test
    void shouldReturnEmptyItemsList() {
        when(itemRepository.findAllByOwnerId(user.getId(), PageRequest.of(FROM, SIZE)))
                .thenReturn(List.of());
        assertEquals(itemService.getAllByOwnerId(user.getId(), FROM, SIZE).size(), 0);
    }

    @Test
    void shouldReturnAllItems() {
        when(itemRepository.findAllByOwnerId(user.getId(), PageRequest.of(FROM, SIZE)))
                .thenReturn(List.of(item));
        assertEquals(1, itemService.getAllByOwnerId(user.getId(), FROM, SIZE).size());

        when(bookingRepository.findFirstBookingByItem_IdAndStatusAndStartBeforeOrderByEndDesc(anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(booking);
        when(bookingRepository.findFirstBookingByItem_IdAndStatusAndStartAfterOrderByStartAsc(anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(booking);
        assertEquals(1, itemService.getAllByOwnerId(user.getId(), FROM, SIZE).size());
    }

    @Test
    void shouldExceptionWhenGetItemById() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> itemService.getById(user.getId(), item.getId()));
    }

    @Test
    void shouldGetItemById() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        assertEquals(itemDto, itemService.getById(user.getId(), item.getId()));

        item.setOwnerId(2L);
        itemDto.setOwnerId(2L);
        assertEquals(itemDto, itemService.getById(user.getId(), item.getId()));
    }

    @Test
    void shouldCreateItem() {
        itemDto.setComments(null);
        when(itemRepository.save(any()))
                .thenReturn(item);
        assertEquals(itemDto, itemService.create(user.getId(), itemDto));
    }

    @Test
    void shouldExceptionWhenUpdateNotExistItemOrUser() {
        itemDto.setId(null);
        assertThrows(ItemNotFoundException.class, () -> itemService.update(user.getId(), itemDto));
        itemDto.setId(3L);
        assertThrows(ItemNotFoundException.class, () -> itemService.update(user.getId(), itemDto));

        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(null);
        assertThrows(ItemNotFoundException.class, () -> itemService.update(10L, itemDto));

        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(List.of(item));
        when(itemRepository.save(item))
                .thenReturn(item);
        assertEquals(ItemMapper.toItemDto(item), itemService.update(1L, ItemMapper.toItemDto(item)));
    }

    @Test
    void shouldDeleteItem() {
        assertDoesNotThrow(() -> itemService.deleteItem(user.getId(), itemDto));
    }

    @Test
    void shouldDeleteItemById() {
        assertDoesNotThrow(() -> itemService.deleteById(user.getId(), itemDto.getId()));
    }

    @Test
    void shouldReturnEmptyListOfItemsBySearchingBlankText() {
        assertEquals(List.of(), itemService.searchItemsByText("   ", FROM, SIZE));
    }

    @Test
    void shouldReturnItemsBySearching() {
        itemDto.setComments(null);
        when(itemRepository.searchItemsByText("text", PageRequest.of(FROM, SIZE)))
                .thenReturn(List.of(item));
        assertEquals(List.of(itemDto), itemService.searchItemsByText("text", FROM, SIZE));
    }

    @Test
    void shouldExceptionWhenAddCommentWhenNullBooking() {
        when(bookingRepository.findFirstByItem_idAndBooker_IdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(null);
        when(userService.getById(anyLong()))
                .thenReturn(userDto);
        assertThrows(CommentCreationException.class, () -> itemService.addComment(user.getId(), item.getId(), commentDto));
    }

    @Test
    void shouldExceptionWhenAddCommentWhenBlankText() {
        commentDto.setText("    ");
        when(userService.getById(anyLong()))
                .thenReturn(userDto);
        when(bookingRepository.findFirstByItem_idAndBooker_IdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(booking);
        assertThrows(CommentCreationException.class, () -> itemService.addComment(userDto.getId(), itemDto.getId(), commentDto));
    }

    @Test
    void shouldAddComment() {
        when(userService.getById(anyLong()))
                .thenReturn(userDto);
        when(bookingRepository.findFirstByItem_idAndBooker_IdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(booking);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(comment));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);
        CommentDto commentDtoActual = itemService.addComment(userDto.getId(), itemDto.getId(), commentDto);
        assertEquals(commentDto.getId(), commentDtoActual.getId());
        assertEquals(commentDto.getText(), commentDtoActual.getText());
        assertEquals(commentDto.getAuthorName(), commentDtoActual.getAuthorName());
    }
}