package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.InvalidFieldException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;


    public BookingServiceImpl(UserService userService, ItemService itemService, BookingRepository bookingRepository) {
        this.userService = userService;
        this.itemService = itemService;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingOutputDto createBooking(long userId, BookingInputDto bookingDto) {
        long itemId = bookingDto.getItemId();
        User user = UserMapper.toUser(userService.getById(userId));
        ItemDto itemDto = itemService.getById(userId, itemId);
        Item item = ItemMapper.toItem(itemDto);
        long ownerId = itemDto.getOwnerId();

        if (itemDto.getAvailable().equals(false)) {
            throw new InvalidFieldException("Вещь с id = " + itemId + " забронирована другим пользователем.");
        }
        if (userId == ownerId) {
            throw new BookingNotFoundException("Владелец не может забронировать свою вещь.");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, user, item);
        booking.setStatus(Status.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingOutputDto approveOrRejectBooking(long bookingId, long ownerId, boolean approved) {
        userService.getById(ownerId);
        Booking booking = getById(bookingId);
        Status status = booking.getStatus();
        if (ownerId != booking.getItem().getOwnerId()) {
            throw new BookingNotFoundException("Бронь с id = " + bookingId + " имеет другого владельца вещи.");
        }
        if (status.equals(Status.APPROVED) || status.equals(Status.REJECTED)) {
            throw new InvalidFieldException("Бронирование уже подтверждено/отклонено владельцем.");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingOutputDto getBookingByBookingId(long bookingId, long userId) {
        userService.getById(userId);
        Booking booking = getById(bookingId);
        if (userId == booking.getBooker().getId() || userId == booking.getItem().getOwnerId()) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new BookingNotFoundException("Информация о бронировании может быть получена его автором, либо владельцем");
        }
    }

    @Override
    public List<BookingOutputDto> getAllBookingsByUserId(long userId, String state, int from, int size) {
        userService.getById(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        try {
            switch (State.valueOf(state.toUpperCase())) {
                case CURRENT:
                    bookings = bookingRepository.findAllByBookerIdAndStartLessThanAndEndGreaterThan(userId, now, now, pageRequest);
                    break;
                case PAST:
                    bookings = bookingRepository.findAllByBookerIdAndStartLessThanAndEndLessThan(userId, now, now, pageRequest);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findAllByBookerIdAndStartGreaterThanAndEndGreaterThan(userId, now, now, pageRequest);
                    break;
                case WAITING:
                    bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING, pageRequest);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED, pageRequest);
                    break;
                case ALL:
                    bookings = bookingRepository.findAllByBookerId(userId, pageRequest);
                    break;
                default:
                    throw new UnsupportedStatusException();
            }
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException();
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingOutputDto> getAllBookingsByOwnerId(long ownerId, String state, int from, int size) {
        userService.getById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        try {
            switch (State.valueOf(state.toUpperCase())) {
                case CURRENT:
                    bookings = bookingRepository.findAllByItem_OwnerIdAndStartLessThanAndEndGreaterThan(ownerId, now, now, pageRequest);
                    break;
                case PAST:
                    bookings = bookingRepository.findAllByItem_OwnerIdAndStartLessThanAndEndLessThan(ownerId, now, now, pageRequest);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findAllByItem_OwnerIdAndStartGreaterThanAndEndGreaterThan(ownerId, now, now, pageRequest);
                    break;
                case WAITING:
                    bookings = bookingRepository.findAllByItem_OwnerIdAndStatus(ownerId, Status.WAITING, pageRequest);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findAllByItem_OwnerIdAndStatus(ownerId, Status.REJECTED, pageRequest);
                    break;
                case ALL:
                    bookings = bookingRepository.findAllByItem_OwnerId(ownerId, pageRequest);
                    break;
                default:
                    throw new UnsupportedStatusException();
            }
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException();
        }

        if (bookings.isEmpty()) {
            throw new BookingNotFoundException("У владельца с id = " + ownerId + " пока нет вещей.");
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public BookingOutputDto findLastBookingByItemId(long itemId) {
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = bookingRepository.findFirstBookingByItem_IdAndStatusAndEndBeforeOrderByEndDesc(itemId, Status.APPROVED, now);
        return BookingMapper.toBookingDto(lastBooking);
    }

    public BookingOutputDto findNextBookingByItemId(long itemId) {
        LocalDateTime now = LocalDateTime.now();
        Booking nextBooking = bookingRepository.findFirstBookingByItem_IdAndStatusAndStartAfterOrderByStartAsc(itemId, Status.APPROVED, now);
        return BookingMapper.toBookingDto(nextBooking);
    }

    private Booking getById(long bookingId) {
        Booking booking = bookingRepository.findBookingById(bookingId);
        if (booking == null) {
            throw new BookingNotFoundException(bookingId);
        }
        return booking;
    }
}

