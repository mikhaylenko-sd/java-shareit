package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.InvalidFieldException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.item.ItemServiceUnitTest.FROM;
import static ru.practicum.shareit.item.ItemServiceUnitTest.SIZE;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {
    private BookingService bookingService;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Mock
    private BookingRepository bookingRepository;

    private static long counter = 0;

    private static UserDto generateUser() {
        return UserDto.builder()
                .id(++counter)
                .name("name " + (counter))
                .email("user" + counter + "@ya.ru")
                .build();
    }

    private static ItemDto generateItem(long ownerId) {
        return ItemDto.builder()
                .id(++counter)
                .name("item " + (counter))
                .description("description " + counter)
                .ownerId(ownerId)
                .available(true)
                .build();
    }

    private static BookingInputDto generateBookingInput(long itemId) {
        return BookingInputDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.of(2023, 11, 11, 11, 11))
                .end(LocalDateTime.of(2024, 11, 11, 11, 11))
                .build();
    }

    private static BookingOutputDto generateBookingOutput(UserDto booker, ItemDto item) {
        return BookingOutputDto.builder()
                .id(++counter)
                .start(LocalDateTime.of(2023, 11, 11, 11, 11))
                .end(LocalDateTime.of(2024, 11, 11, 11, 11))
                .booker(booker)
                .bookerId(booker.getId())
                .item(item)
                .build();
    }

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(userService, itemService, bookingRepository);
    }

    @Test
    void testCreateBooking() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        ItemDto item = generateItem(owner.getId());
        BookingInputDto bookingInput = generateBookingInput(item.getId());

        when(userService.getById(anyLong()))
                .thenReturn(booker);
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(item);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(BookingMapper.toBooking(bookingInput, UserMapper.toUser(booker), ItemMapper.toItem(item)));

        BookingOutputDto booking = bookingService.createBooking(booker.getId(), bookingInput);
        assertEquals(booker, booking.getBooker());
        assertEquals(item, booking.getItem());
        assertEquals(bookingInput.getStart(), booking.getStart());
        assertEquals(bookingInput.getEnd(), booking.getEnd());
    }

    @Test
    void testExceptionCreateBookingByOwner() {
        UserDto owner = generateUser();
        ItemDto item = generateItem(owner.getId());
        BookingInputDto bookingInput = generateBookingInput(item.getId());

        when(userService.getById(anyLong()))
                .thenReturn(owner);
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(item);

        assertThrows(BookingNotFoundException.class, () -> bookingService.createBooking(owner.getId(), bookingInput));
    }

    @Test
    void testExceptionCreateBookingByFalseAvailable() {
        UserDto owner = generateUser();
        ItemDto item = generateItem(owner.getId());
        item.setAvailable(false);
        BookingInputDto bookingInput = generateBookingInput(item.getId());

        when(userService.getById(anyLong()))
                .thenReturn(owner);
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(item);

        assertThrows(InvalidFieldException.class, () -> bookingService.createBooking(owner.getId(), bookingInput));
    }

    @Test
    void testApproveOrRejectBooking() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        ItemDto item = generateItem(owner.getId());
        BookingInputDto bookingInput = generateBookingInput(item.getId());
        Booking booking = new Booking(1L, bookingInput.getStart(), bookingInput.getEnd(),
                ItemMapper.toItem(item), UserMapper.toUser(booker), Status.WAITING);

        when(userService.getById(anyLong()))
                .thenReturn(owner);
        when(bookingRepository.findBookingById(anyLong()))
                .thenReturn(booking);
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(item);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        bookingService.createBooking(booker.getId(), bookingInput);

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        assertEquals(Status.APPROVED, bookingService.approveOrRejectBooking(booking.getId(), owner.getId(), true).getStatus());
        booking.setStatus(Status.WAITING);
        assertEquals(Status.REJECTED, bookingService.approveOrRejectBooking(booking.getId(), owner.getId(), false).getStatus());
    }

    @Test
    void testExceptionApproveOrRejectBookingWithRejectedStatus() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        ItemDto item = generateItem(owner.getId());
        BookingInputDto bookingInput = generateBookingInput(item.getId());
        Booking booking = new Booking(1L, bookingInput.getStart(), bookingInput.getEnd(),
                ItemMapper.toItem(item), UserMapper.toUser(booker), Status.REJECTED);

        when(userService.getById(anyLong()))
                .thenReturn(owner);
        when(bookingRepository.findBookingById(anyLong()))
                .thenReturn(booking);
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(item);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        bookingService.createBooking(booker.getId(), bookingInput);

        assertThrows(InvalidFieldException.class, () -> bookingService.approveOrRejectBooking(booking.getId(), owner.getId(), true));
    }

    @Test
    void testExceptionApproveOrRejectBookingWithOtherOwner() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        UserDto otherUser = generateUser();
        ItemDto item = generateItem(owner.getId());
        BookingInputDto bookingInput = generateBookingInput(item.getId());
        Booking booking = new Booking(1L, bookingInput.getStart(), bookingInput.getEnd(),
                ItemMapper.toItem(item), UserMapper.toUser(booker), Status.REJECTED);

        when(userService.getById(anyLong()))
                .thenReturn(owner);
        when(bookingRepository.findBookingById(anyLong()))
                .thenReturn(booking);
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(item);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        bookingService.createBooking(booker.getId(), bookingInput);

        when(userService.getById(anyLong()))
                .thenReturn(otherUser);
        assertThrows(BookingNotFoundException.class, () -> bookingService.approveOrRejectBooking(booking.getId(), otherUser.getId(), true));
    }

    @Test
    void testGetBookingByBookingId() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        ItemDto item = generateItem(owner.getId());
        BookingInputDto bookingInput = generateBookingInput(item.getId());
        Booking booking = new Booking(1L, bookingInput.getStart(), bookingInput.getEnd(),
                ItemMapper.toItem(item), UserMapper.toUser(booker), Status.WAITING);

        when(userService.getById(anyLong()))
                .thenReturn(booker);
        when(bookingRepository.findBookingById(anyLong()))
                .thenReturn(booking);
        assertEquals(BookingMapper.toBookingDto(booking), bookingService.getBookingByBookingId(booking.getId(), owner.getId()));
    }

    @Test
    void testExceptionGetBookingByBookingIdByOtherUser() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        UserDto otherUser = generateUser();
        ItemDto item = generateItem(owner.getId());
        BookingInputDto bookingInput = generateBookingInput(item.getId());
        Booking booking = new Booking(1L, bookingInput.getStart(), bookingInput.getEnd(),
                ItemMapper.toItem(item), UserMapper.toUser(booker), Status.WAITING);

        when(userService.getById(anyLong()))
                .thenReturn(otherUser);
        when(bookingRepository.findBookingById(anyLong()))
                .thenReturn(booking);
        assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingByBookingId(booking.getId(), otherUser.getId()));
    }

    @Test
    void testExceptionGetBookingByBookingIdWhenNotFound() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        ItemDto item = generateItem(owner.getId());
        BookingInputDto bookingInput = generateBookingInput(item.getId());
        Booking booking = new Booking(1L, bookingInput.getStart(), bookingInput.getEnd(),
                ItemMapper.toItem(item), UserMapper.toUser(booker), Status.WAITING);

        when(userService.getById(anyLong()))
                .thenReturn(booker);
        when(bookingRepository.findBookingById(anyLong()))
                .thenReturn(null);
        assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingByBookingId(booking.getId(), owner.getId()));
    }

    @Test
    void getAllBookingsByUserIdWhenCurrentState() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        ItemDto item1 = generateItem(owner.getId());
        ItemDto item2 = generateItem(owner.getId());
        BookingInputDto bookingInput1 = generateBookingInput(item1.getId());
        BookingInputDto bookingInput2 = generateBookingInput(item2.getId());
        Booking booking1 = new Booking(1L, bookingInput1.getStart(), bookingInput1.getEnd(),
                ItemMapper.toItem(item1), UserMapper.toUser(booker), Status.APPROVED);
        Booking booking2 = new Booking(2L, bookingInput2.getStart(), bookingInput2.getEnd(),
                ItemMapper.toItem(item2), UserMapper.toUser(booker), Status.WAITING);

        when(bookingRepository.findAllByBookerIdAndStartLessThanAndEndGreaterThan(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));
        List<BookingOutputDto> allBookings = bookingService.getAllBookingsByUserId(booker.getId(), "CURRENT", FROM, SIZE);

        assertEquals(2, allBookings.size());
        assertEquals(BookingMapper.toBookingDto(booking1), allBookings.get(0));
        assertEquals(BookingMapper.toBookingDto(booking2), allBookings.get(1));
    }

    @Test
    void getAllBookingsByUserIdWhenPastState() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        ItemDto item1 = generateItem(owner.getId());
        ItemDto item2 = generateItem(owner.getId());
        BookingInputDto bookingInput1 = generateBookingInput(item1.getId());
        BookingInputDto bookingInput2 = generateBookingInput(item2.getId());
        Booking booking1 = new Booking(1L, bookingInput1.getStart(), bookingInput1.getEnd(),
                ItemMapper.toItem(item1), UserMapper.toUser(booker), Status.APPROVED);
        Booking booking2 = new Booking(2L, bookingInput2.getStart(), bookingInput2.getEnd(),
                ItemMapper.toItem(item2), UserMapper.toUser(booker), Status.APPROVED);

        when(bookingRepository.findAllByBookerIdAndStartLessThanAndEndLessThan(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));
        List<BookingOutputDto> allBookings = bookingService.getAllBookingsByUserId(booker.getId(), "PAST", FROM, SIZE);

        assertEquals(2, allBookings.size());
        assertEquals(BookingMapper.toBookingDto(booking1), allBookings.get(0));
        assertEquals(BookingMapper.toBookingDto(booking2), allBookings.get(1));
    }

    @Test
    void testGetAllBookingsByUserIdWhenFutureState() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        ItemDto item1 = generateItem(owner.getId());
        ItemDto item2 = generateItem(owner.getId());
        BookingInputDto bookingInput1 = generateBookingInput(item1.getId());
        BookingInputDto bookingInput2 = generateBookingInput(item2.getId());
        Booking booking1 = new Booking(1L, bookingInput1.getStart(), bookingInput1.getEnd(),
                ItemMapper.toItem(item1), UserMapper.toUser(booker), Status.APPROVED);
        Booking booking2 = new Booking(2L, bookingInput2.getStart(), bookingInput2.getEnd(),
                ItemMapper.toItem(item2), UserMapper.toUser(booker), Status.APPROVED);

        when(bookingRepository.findAllByBookerIdAndStartGreaterThanAndEndGreaterThan(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));
        List<BookingOutputDto> allBookings = bookingService.getAllBookingsByUserId(booker.getId(), "FUTURE", FROM, SIZE);

        assertEquals(2, allBookings.size());
        assertEquals(BookingMapper.toBookingDto(booking1), allBookings.get(0));
        assertEquals(BookingMapper.toBookingDto(booking2), allBookings.get(1));
    }

    @Test
    void testGetAllBookingsByUserIdWhenWaitingState() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        ItemDto item1 = generateItem(owner.getId());
        ItemDto item2 = generateItem(owner.getId());
        BookingInputDto bookingInput1 = generateBookingInput(item1.getId());
        BookingInputDto bookingInput2 = generateBookingInput(item2.getId());
        Booking booking1 = new Booking(1L, bookingInput1.getStart(), bookingInput1.getEnd(),
                ItemMapper.toItem(item1), UserMapper.toUser(booker), Status.WAITING);
        Booking booking2 = new Booking(2L, bookingInput2.getStart(), bookingInput2.getEnd(),
                ItemMapper.toItem(item2), UserMapper.toUser(booker), Status.WAITING);

        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(Status.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));
        List<BookingOutputDto> allBookings = bookingService.getAllBookingsByUserId(booker.getId(), "WAITING", FROM, SIZE);

        assertEquals(2, allBookings.size());
        assertEquals(BookingMapper.toBookingDto(booking1), allBookings.get(0));
        assertEquals(BookingMapper.toBookingDto(booking2), allBookings.get(1));
    }

    @Test
    void testGetAllBookingsByUserIdWhenRejectedState() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        ItemDto item1 = generateItem(owner.getId());
        ItemDto item2 = generateItem(owner.getId());
        BookingInputDto bookingInput1 = generateBookingInput(item1.getId());
        BookingInputDto bookingInput2 = generateBookingInput(item2.getId());
        Booking booking1 = new Booking(1L, bookingInput1.getStart(), bookingInput1.getEnd(),
                ItemMapper.toItem(item1), UserMapper.toUser(booker), Status.REJECTED);
        Booking booking2 = new Booking(2L, bookingInput2.getStart(), bookingInput2.getEnd(),
                ItemMapper.toItem(item2), UserMapper.toUser(booker), Status.REJECTED);

        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(Status.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));
        List<BookingOutputDto> allBookings = bookingService.getAllBookingsByUserId(booker.getId(), "REJECTED", FROM, SIZE);

        assertEquals(2, allBookings.size());
        assertEquals(BookingMapper.toBookingDto(booking1), allBookings.get(0));
        assertEquals(BookingMapper.toBookingDto(booking2), allBookings.get(1));
    }

    @Test
    void testGetAllBookingsByUserIdWhenAllState() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        ItemDto item1 = generateItem(owner.getId());
        ItemDto item2 = generateItem(owner.getId());
        BookingInputDto bookingInput1 = generateBookingInput(item1.getId());
        BookingInputDto bookingInput2 = generateBookingInput(item2.getId());
        Booking booking1 = new Booking(1L, bookingInput1.getStart(), bookingInput1.getEnd(),
                ItemMapper.toItem(item1), UserMapper.toUser(booker), Status.REJECTED);
        Booking booking2 = new Booking(2L, bookingInput2.getStart(), bookingInput2.getEnd(),
                ItemMapper.toItem(item2), UserMapper.toUser(booker), Status.APPROVED);

        when(bookingRepository.findAllByBookerId(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));
        List<BookingOutputDto> allBookings = bookingService.getAllBookingsByUserId(booker.getId(), "ALL", FROM, SIZE);

        assertEquals(2, allBookings.size());
        assertEquals(BookingMapper.toBookingDto(booking1), allBookings.get(0));
        assertEquals(BookingMapper.toBookingDto(booking2), allBookings.get(1));
    }

    @Test
    void testGetAllBookingsByUserIdWhenWrongState() {
        UserDto booker = generateUser();
        when(userService.getById(anyLong()))
                .thenReturn(booker);

        assertThrows(UnsupportedStatusException.class, () -> bookingService.getAllBookingsByUserId(booker.getId(), "vsbvbndf", FROM, SIZE));
    }

    @Test
    void testGetAllBookingsByOwnerIdWhenCurrentState() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        ItemDto item1 = generateItem(owner.getId());
        ItemDto item2 = generateItem(owner.getId());
        BookingInputDto bookingInput1 = generateBookingInput(item1.getId());
        BookingInputDto bookingInput2 = generateBookingInput(item2.getId());
        Booking booking1 = new Booking(1L, bookingInput1.getStart(), bookingInput1.getEnd(),
                ItemMapper.toItem(item1), UserMapper.toUser(booker), Status.APPROVED);
        Booking booking2 = new Booking(2L, bookingInput2.getStart(), bookingInput2.getEnd(),
                ItemMapper.toItem(item2), UserMapper.toUser(booker), Status.WAITING);

        when(bookingRepository.findAllByItem_OwnerIdAndStartLessThanAndEndGreaterThan(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));
        List<BookingOutputDto> allBookings = bookingService.getAllBookingsByOwnerId(owner.getId(), "CURRENT", FROM, SIZE);

        assertEquals(2, allBookings.size());
        assertEquals(BookingMapper.toBookingDto(booking1), allBookings.get(0));
        assertEquals(BookingMapper.toBookingDto(booking2), allBookings.get(1));
    }

    @Test
    void testGetAllBookingsByOwnerIdWhenPastState() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        ItemDto item1 = generateItem(owner.getId());
        ItemDto item2 = generateItem(owner.getId());
        BookingInputDto bookingInput1 = generateBookingInput(item1.getId());
        BookingInputDto bookingInput2 = generateBookingInput(item2.getId());
        Booking booking1 = new Booking(1L, bookingInput1.getStart(), bookingInput1.getEnd(),
                ItemMapper.toItem(item1), UserMapper.toUser(booker), Status.APPROVED);
        Booking booking2 = new Booking(2L, bookingInput2.getStart(), bookingInput2.getEnd(),
                ItemMapper.toItem(item2), UserMapper.toUser(booker), Status.APPROVED);

        when(bookingRepository.findAllByItem_OwnerIdAndStartLessThanAndEndLessThan(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));
        List<BookingOutputDto> allBookings = bookingService.getAllBookingsByOwnerId(booker.getId(), "PAST", FROM, SIZE);

        assertEquals(2, allBookings.size());
        assertEquals(BookingMapper.toBookingDto(booking1), allBookings.get(0));
        assertEquals(BookingMapper.toBookingDto(booking2), allBookings.get(1));
    }

    @Test
    void testGetAllBookingsByOwnerIdWhenFutureState() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        ItemDto item1 = generateItem(owner.getId());
        ItemDto item2 = generateItem(owner.getId());
        BookingInputDto bookingInput1 = generateBookingInput(item1.getId());
        BookingInputDto bookingInput2 = generateBookingInput(item2.getId());
        Booking booking1 = new Booking(1L, bookingInput1.getStart(), bookingInput1.getEnd(),
                ItemMapper.toItem(item1), UserMapper.toUser(booker), Status.APPROVED);
        Booking booking2 = new Booking(2L, bookingInput2.getStart(), bookingInput2.getEnd(),
                ItemMapper.toItem(item2), UserMapper.toUser(booker), Status.APPROVED);

        when(bookingRepository.findAllByItem_OwnerIdAndStartGreaterThanAndEndGreaterThan(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));
        List<BookingOutputDto> allBookings = bookingService.getAllBookingsByOwnerId(booker.getId(), "FUTURE", FROM, SIZE);

        assertEquals(2, allBookings.size());
        assertEquals(BookingMapper.toBookingDto(booking1), allBookings.get(0));
        assertEquals(BookingMapper.toBookingDto(booking2), allBookings.get(1));
    }

    @Test
    void testGetAllBookingsByOwnerIdWhenWaitingState() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        ItemDto item1 = generateItem(owner.getId());
        ItemDto item2 = generateItem(owner.getId());
        BookingInputDto bookingInput1 = generateBookingInput(item1.getId());
        BookingInputDto bookingInput2 = generateBookingInput(item2.getId());
        Booking booking1 = new Booking(1L, bookingInput1.getStart(), bookingInput1.getEnd(),
                ItemMapper.toItem(item1), UserMapper.toUser(booker), Status.WAITING);
        Booking booking2 = new Booking(2L, bookingInput2.getStart(), bookingInput2.getEnd(),
                ItemMapper.toItem(item2), UserMapper.toUser(booker), Status.WAITING);

        when(bookingRepository.findAllByItem_OwnerIdAndStatus(anyLong(), any(Status.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));
        List<BookingOutputDto> allBookings = bookingService.getAllBookingsByOwnerId(booker.getId(), "WAITING", FROM, SIZE);

        assertEquals(2, allBookings.size());
        assertEquals(BookingMapper.toBookingDto(booking1), allBookings.get(0));
        assertEquals(BookingMapper.toBookingDto(booking2), allBookings.get(1));
    }

    @Test
    void testGetAllBookingsByOwnerIdWhenRejectedState() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        ItemDto item1 = generateItem(owner.getId());
        ItemDto item2 = generateItem(owner.getId());
        BookingInputDto bookingInput1 = generateBookingInput(item1.getId());
        BookingInputDto bookingInput2 = generateBookingInput(item2.getId());
        Booking booking1 = new Booking(1L, bookingInput1.getStart(), bookingInput1.getEnd(),
                ItemMapper.toItem(item1), UserMapper.toUser(booker), Status.REJECTED);
        Booking booking2 = new Booking(2L, bookingInput2.getStart(), bookingInput2.getEnd(),
                ItemMapper.toItem(item2), UserMapper.toUser(booker), Status.REJECTED);

        when(bookingRepository.findAllByItem_OwnerIdAndStatus(anyLong(), any(Status.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));
        List<BookingOutputDto> allBookings = bookingService.getAllBookingsByOwnerId(booker.getId(), "REJECTED", FROM, SIZE);

        assertEquals(2, allBookings.size());
        assertEquals(BookingMapper.toBookingDto(booking1), allBookings.get(0));
        assertEquals(BookingMapper.toBookingDto(booking2), allBookings.get(1));
    }

    @Test
    void testGetAllBookingsByOwnerIdWhenAllState() {
        UserDto owner = generateUser();
        UserDto booker = generateUser();
        ItemDto item1 = generateItem(owner.getId());
        ItemDto item2 = generateItem(owner.getId());
        BookingInputDto bookingInput1 = generateBookingInput(item1.getId());
        BookingInputDto bookingInput2 = generateBookingInput(item2.getId());
        Booking booking1 = new Booking(1L, bookingInput1.getStart(), bookingInput1.getEnd(),
                ItemMapper.toItem(item1), UserMapper.toUser(booker), Status.REJECTED);
        Booking booking2 = new Booking(2L, bookingInput2.getStart(), bookingInput2.getEnd(),
                ItemMapper.toItem(item2), UserMapper.toUser(booker), Status.APPROVED);

        when(bookingRepository.findAllByItem_OwnerId(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking1, booking2));
        List<BookingOutputDto> allBookings = bookingService.getAllBookingsByOwnerId(booker.getId(), "ALL", FROM, SIZE);

        assertEquals(2, allBookings.size());
        assertEquals(BookingMapper.toBookingDto(booking1), allBookings.get(0));
        assertEquals(BookingMapper.toBookingDto(booking2), allBookings.get(1));
    }

    @Test
    void testGetAllBookingsByOwnerIdWhenWrongState() {
        UserDto owner = generateUser();
        when(userService.getById(anyLong()))
                .thenReturn(owner);

        assertThrows(UnsupportedStatusException.class, () -> bookingService.getAllBookingsByOwnerId(owner.getId(), "vsbvbndf", FROM, SIZE));
    }

    @Test
    void testGetEmptyListOfBookingsByOwnerId() {
        UserDto owner = generateUser();
        when(userService.getById(anyLong()))
                .thenReturn(owner);

        assertThrows(BookingNotFoundException.class, () -> bookingService.getAllBookingsByOwnerId(owner.getId(), "ALL", FROM, SIZE));
    }
}