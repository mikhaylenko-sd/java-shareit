package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.CommentCreationException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserService userService, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public List<ItemDto> getAllByOwnerId(long ownerId, int from, int size) {
        return itemRepository.findAllByOwnerId(ownerId, PageRequest.of(from, size)).stream()
                .map(ItemMapper::toItemDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .peek(itemDto -> setLastAndNextBookings(itemDto.getId(), itemDto))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(long userId, long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item.orElseThrow(() -> new ItemNotFoundException(itemId)));
        if (userId == itemDto.getOwnerId()) {
            setLastAndNextBookings(itemId, itemDto);
        } else {
            itemDto.setLastBooking(null);
            itemDto.setNextBooking(null);
        }

        List<CommentDto> commentDtos = commentRepository.findAllByItemIdOrderByCreatedDesc(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(commentDtos);
        return itemDto;
    }

    @Override
    public ItemDto create(long ownerId, ItemDto itemDto) {
        userService.getById(ownerId);
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(long ownerId, ItemDto itemDto) {
        userService.getById(ownerId);
        Item newItem = ItemMapper.toItem(itemDto);
        Long itemId = newItem.getId();
        List<Item> itemsByOwnerId = itemRepository.findAllByOwnerId(ownerId);
        if (itemsByOwnerId == null) {
            throw new ItemNotFoundException("У пользователя с id = " + ownerId + " пока нет вещей.");
        }
        Item oldItem = itemsByOwnerId.stream()
                .filter(item -> Objects.equals(item.getId(), itemId))
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("У пользователя с id = " + ownerId + " нет данной вещи."));
        merge(oldItem, newItem);
        return ItemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Override
    public void deleteItem(long ownerId, ItemDto itemDto) {
        userService.getById(ownerId);
        itemRepository.delete(ItemMapper.toItem(itemDto));
    }

    @Override
    public void deleteById(long ownerId, long itemId) {
        userService.getById(ownerId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> searchItemsByText(String text, int from, int size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItemsByText(text.toLowerCase(), PageRequest.of(from, size)).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        LocalDateTime now = LocalDateTime.now();
        UserDto userDto = userService.getById(userId);
        commentDto.setCreated(now);
        commentDto.setAuthorName(userDto.getName());

        if (bookingRepository.findFirstByItem_idAndBooker_IdAndEndBefore(itemId, userId, now) == null) {
            throw new CommentCreationException("Пользователь не брал в аренду данную вещь.");
        }

        if (commentDto.getText().isBlank()) {
            throw new CommentCreationException("Комментарий не может быть пустым.");
        }

        Comment comment = commentRepository.save(
                CommentMapper.toComment(commentDto, userDto, getById(userId, itemId))
        );

        return CommentMapper.toCommentDto(comment);
    }

    private void merge(Item oldItem, Item newItem) {
        String name = newItem.getName();
        String description = newItem.getDescription();
        Boolean available = newItem.getAvailable();

        if (name != null) {
            oldItem.setName(name);
        }
        if (description != null) {
            oldItem.setDescription(description);
        }
        if (available != null) {
            oldItem.setAvailable(available);
        }
    }

    private void setLastAndNextBookings(long itemId, ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = bookingRepository.findFirstBookingByItem_IdAndStatusAndStartBeforeOrderByEndDesc(itemId, Status.APPROVED, now);
        Booking nextBooking = bookingRepository.findFirstBookingByItem_IdAndStatusAndStartAfterOrderByStartAsc(itemId, Status.APPROVED, now);

        if (lastBooking == null) {
            itemDto.setLastBooking(null);
        } else {
            itemDto.setLastBooking(BookingMapper.toBookingDto(lastBooking));
        }

        if (nextBooking == null) {
            itemDto.setNextBooking(null);
        } else {
            itemDto.setNextBooking(BookingMapper.toBookingDto(nextBooking));
        }
    }
}
