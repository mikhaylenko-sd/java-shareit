package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.InvalidFieldException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.util.UtilGenerators.generateBookingInputDto;
import static ru.practicum.shareit.util.UtilGenerators.generateItemDto;
import static ru.practicum.shareit.util.UtilGenerators.generateUserDto;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    void testCreateBooking() {
        UserDto ownerUser = userService.create(generateUserDto());
        ItemDto itemDto = itemService.create(ownerUser.getId(), generateItemDto(ownerUser.getId()));

        UserDto bookerUser = userService.create(generateUserDto());
        BookingOutputDto booking = bookingService.createBooking(bookerUser.getId(), generateBookingInputDto(itemDto.getId()));

        assertEquals(bookerUser.getId(), booking.getBookerId());
        assertEquals(Status.WAITING, booking.getStatus());
        assertEquals(itemDto.getId(), booking.getItem().getId());
    }


    @Test
    void testCreateBookingWithErrorParams() {
        assertThrows(UserNotFoundException.class, () -> bookingService.createBooking(-1, generateBookingInputDto(-1)));

        UserDto bookerUser = userService.create(generateUserDto());
        assertThrows(ItemNotFoundException.class, () -> bookingService.createBooking(bookerUser.getId(), generateBookingInputDto(-1)));


        UserDto ownerUser = userService.create(generateUserDto());
        ItemDto itemDto = itemService.create(ownerUser.getId(), generateItemDto(ownerUser.getId()));
        assertThrows(BookingNotFoundException.class, () -> bookingService.createBooking(ownerUser.getId(), generateBookingInputDto(itemDto.getId())));

        itemDto.setAvailable(false);
        itemService.update(ownerUser.getId(), itemDto);
        assertThrows(InvalidFieldException.class, () -> bookingService.createBooking(bookerUser.getId(), generateBookingInputDto(itemDto.getId())));
    }

    @Test
    void testApproveAndRejectBooking() {
        UserDto ownerUser = userService.create(generateUserDto());
        ItemDto itemDto1 = itemService.create(ownerUser.getId(), generateItemDto(ownerUser.getId()));


        UserDto bookerUser = userService.create(generateUserDto());
        BookingOutputDto booking1 = bookingService.createBooking(bookerUser.getId(), generateBookingInputDto(itemDto1.getId()));

        booking1 = bookingService.approveOrRejectBooking(booking1.getId(), ownerUser.getId(), true);
        assertEquals(Status.APPROVED, booking1.getStatus());

        ItemDto itemDto2 = itemService.create(ownerUser.getId(), generateItemDto(ownerUser.getId()));
        BookingOutputDto booking2 = bookingService.createBooking(bookerUser.getId(), generateBookingInputDto(itemDto2.getId()));
        booking2 = bookingService.approveOrRejectBooking(booking2.getId(), ownerUser.getId(), false);
        assertEquals(Status.REJECTED, booking2.getStatus());
    }

    @Test
    void testFailedApproveAndRejectBooking() {
        UserDto ownerUser = userService.create(generateUserDto());
        ItemDto itemDto = itemService.create(ownerUser.getId(), generateItemDto(ownerUser.getId()));
        UserDto bookerUser = userService.create(generateUserDto());

        BookingOutputDto booking = bookingService.createBooking(bookerUser.getId(), generateBookingInputDto(itemDto.getId()));

        assertThrows(BookingNotFoundException.class, () -> bookingService.approveOrRejectBooking(booking.getId(), bookerUser.getId(), true));
        bookingService.approveOrRejectBooking(booking.getId(), ownerUser.getId(), true);
        assertThrows(InvalidFieldException.class, () -> bookingService.approveOrRejectBooking(booking.getId(), ownerUser.getId(), true));
    }

    @Test
    void testGetBookingByBookingId() {
        UserDto ownerUser = userService.create(generateUserDto());
        ItemDto itemDto = itemService.create(ownerUser.getId(), generateItemDto(ownerUser.getId()));
        UserDto bookerUser = userService.create(generateUserDto());

        BookingOutputDto booking = bookingService.createBooking(bookerUser.getId(), generateBookingInputDto(itemDto.getId()));

        BookingOutputDto bookingResult = bookingService.getBookingByBookingId(booking.getId(), ownerUser.getId());
        assertEquals(booking, bookingResult);

        bookingResult = bookingService.getBookingByBookingId(booking.getId(), bookerUser.getId());
        assertEquals(booking, bookingResult);
    }

    @Test
    void testGetBookingByBookingIdRequestFromNotOwnerAndNotBookerUser() {
        UserDto ownerUser = userService.create(generateUserDto());
        ItemDto itemDto = itemService.create(ownerUser.getId(), generateItemDto(ownerUser.getId()));
        UserDto bookerUser = userService.create(generateUserDto());

        BookingOutputDto booking = bookingService.createBooking(bookerUser.getId(), generateBookingInputDto(itemDto.getId()));

        UserDto notOwnerAndNotBookerUser = userService.create(generateUserDto());
        assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingByBookingId(booking.getId(), notOwnerAndNotBookerUser.getId()));
    }

    @Test
    void testDifferentStatesGetAllBookingsByUserId() {
        UserDto ownerUser = userService.create(generateUserDto());
        ItemDto itemDto = itemService.create(ownerUser.getId(), generateItemDto(ownerUser.getId()));

        UserDto bookerUser = userService.create(generateUserDto());

        LocalDateTime now = LocalDateTime.now();

        BookingOutputDto booking1 = bookingService.createBooking(bookerUser.getId(),
                generateBookingInputDto(itemDto.getId(), now.minusYears(1), now));
        BookingOutputDto booking2 = bookingService.createBooking(bookerUser.getId(),
                generateBookingInputDto(itemDto.getId(), now, now.plusYears(1)));

        BookingOutputDto booking3 = createAndApproveBooking(itemDto.getId(), now.minusYears(5), now.minusYears(4),
                bookerUser.getId(), ownerUser.getId(), false);
        BookingOutputDto booking4 = createAndApproveBooking(itemDto.getId(), now.plusYears(4), now.plusYears(5),
                bookerUser.getId(), ownerUser.getId(), false);


        BookingOutputDto booking5 = createAndApproveBooking(itemDto.getId(), now.minusYears(10), now.minusYears(9),
                bookerUser.getId(), ownerUser.getId(), true);
        BookingOutputDto booking6 = createAndApproveBooking(itemDto.getId(), now.minusYears(8), now.minusYears(7),
                bookerUser.getId(), ownerUser.getId(), true);


        BookingOutputDto booking7 = createAndApproveBooking(itemDto.getId(), now.plusYears(9), now.plusYears(10),
                bookerUser.getId(), ownerUser.getId(), true);
        BookingOutputDto booking8 = createAndApproveBooking(itemDto.getId(), now.plusYears(7), now.plusYears(8),
                bookerUser.getId(), ownerUser.getId(), true);


        BookingOutputDto booking9 = createAndApproveBooking(itemDto.getId(), now.minusYears(2), now.plusYears(2),
                bookerUser.getId(), ownerUser.getId(), true);
        BookingOutputDto booking10 = createAndApproveBooking(itemDto.getId(), now.minusYears(3), now.plusYears(3),
                bookerUser.getId(), ownerUser.getId(), true);


        List<BookingOutputDto> bookingOutputDtos = bookingService.getAllBookingsByUserId(bookerUser.getId(), State.ALL.name(), 0, 10);
        assertEquals(10, bookingOutputDtos.size());
        assertEquals(List.of(booking7.getId(), booking8.getId(), booking4.getId(), booking2.getId(), booking1.getId(), booking9.getId(),
                        booking10.getId(), booking3.getId(), booking6.getId(), booking5.getId()),
                bookingOutputDtos.stream().map(BookingOutputDto::getId).collect(Collectors.toList()));

        bookingOutputDtos = bookingService.getAllBookingsByUserId(bookerUser.getId(), State.ALL.name(), 0, 7);
        assertEquals(7, bookingOutputDtos.size());
        assertEquals(List.of(booking7.getId(), booking8.getId(), booking4.getId(), booking2.getId(), booking1.getId(), booking9.getId(), booking10.getId()),
                bookingOutputDtos.stream().map(BookingOutputDto::getId).collect(Collectors.toList()));

        bookingOutputDtos = bookingService.getAllBookingsByUserId(bookerUser.getId(), State.ALL.name(), 7, 7);
        assertEquals(3, bookingOutputDtos.size());
        assertEquals(List.of(booking3.getId(), booking6.getId(), booking5.getId()),
                bookingOutputDtos.stream().map(BookingOutputDto::getId).collect(Collectors.toList()));

        bookingOutputDtos = bookingService.getAllBookingsByUserId(bookerUser.getId(), State.WAITING.name(), 0, 10);
        assertEquals(2, bookingOutputDtos.size());
        assertEquals(List.of(booking2.getId(), booking1.getId()),
                bookingOutputDtos.stream().map(BookingOutputDto::getId).collect(Collectors.toList()));

        bookingOutputDtos = bookingService.getAllBookingsByUserId(bookerUser.getId(), State.REJECTED.name(), 0, 10);
        assertEquals(2, bookingOutputDtos.size());
        assertEquals(List.of(booking4.getId(), booking3.getId()),
                bookingOutputDtos.stream().map(BookingOutputDto::getId).collect(Collectors.toList()));

        bookingOutputDtos = bookingService.getAllBookingsByUserId(bookerUser.getId(), State.PAST.name(), 0, 10);
        assertEquals(4, bookingOutputDtos.size());
        assertEquals(List.of(booking1.getId(), booking3.getId(), booking6.getId(), booking5.getId()),
                bookingOutputDtos.stream().map(BookingOutputDto::getId).collect(Collectors.toList()));

        bookingOutputDtos = bookingService.getAllBookingsByUserId(bookerUser.getId(), State.CURRENT.name(), 0, 10);
        assertEquals(3, bookingOutputDtos.size());
        assertEquals(List.of(booking2.getId(), booking9.getId(), booking10.getId()),
                bookingOutputDtos.stream().map(BookingOutputDto::getId).collect(Collectors.toList()));


        bookingOutputDtos = bookingService.getAllBookingsByUserId(bookerUser.getId(), State.FUTURE.name(), 0, 10);
        assertEquals(3, bookingOutputDtos.size());
        assertEquals(List.of(booking7.getId(), booking8.getId(), booking4.getId()),
                bookingOutputDtos.stream().map(BookingOutputDto::getId).collect(Collectors.toList())

        );
    }


    @Test
    void testDifferentStatesGetAllBookingsByOwnerId() {
        UserDto ownerUser = userService.create(generateUserDto());
        ItemDto itemDto = itemService.create(ownerUser.getId(), generateItemDto(ownerUser.getId()));

        UserDto bookerUser = userService.create(generateUserDto());

        LocalDateTime now = LocalDateTime.now();

        BookingOutputDto booking1 = bookingService.createBooking(bookerUser.getId(),
                generateBookingInputDto(itemDto.getId(), now.minusYears(1), now));
        BookingOutputDto booking2 = bookingService.createBooking(bookerUser.getId(),
                generateBookingInputDto(itemDto.getId(), now, now.plusYears(1)));

        BookingOutputDto booking3 = createAndApproveBooking(itemDto.getId(), now.minusYears(5), now.minusYears(4),
                bookerUser.getId(), ownerUser.getId(), false);
        BookingOutputDto booking4 = createAndApproveBooking(itemDto.getId(), now.plusYears(4), now.plusYears(5),
                bookerUser.getId(), ownerUser.getId(), false);


        BookingOutputDto booking5 = createAndApproveBooking(itemDto.getId(), now.minusYears(10), now.minusYears(9),
                bookerUser.getId(), ownerUser.getId(), true);
        BookingOutputDto booking6 = createAndApproveBooking(itemDto.getId(), now.minusYears(8), now.minusYears(7),
                bookerUser.getId(), ownerUser.getId(), true);


        BookingOutputDto booking7 = createAndApproveBooking(itemDto.getId(), now.plusYears(9), now.plusYears(10),
                bookerUser.getId(), ownerUser.getId(), true);
        BookingOutputDto booking8 = createAndApproveBooking(itemDto.getId(), now.plusYears(7), now.plusYears(8),
                bookerUser.getId(), ownerUser.getId(), true);


        BookingOutputDto booking9 = createAndApproveBooking(itemDto.getId(), now.minusYears(2), now.plusYears(2),
                bookerUser.getId(), ownerUser.getId(), true);
        BookingOutputDto booking10 = createAndApproveBooking(itemDto.getId(), now.minusYears(3), now.plusYears(3),
                bookerUser.getId(), ownerUser.getId(), true);


        List<BookingOutputDto> bookingOutputDtos = bookingService.getAllBookingsByOwnerId(ownerUser.getId(), State.ALL.name(), 0, 10);
        assertEquals(10, bookingOutputDtos.size());
        assertEquals(List.of(booking7.getId(), booking8.getId(), booking4.getId(), booking2.getId(), booking1.getId(), booking9.getId(),
                        booking10.getId(), booking3.getId(), booking6.getId(), booking5.getId()),
                bookingOutputDtos.stream().map(BookingOutputDto::getId).collect(Collectors.toList()));

        bookingOutputDtos = bookingService.getAllBookingsByOwnerId(ownerUser.getId(), State.ALL.name(), 0, 7);
        assertEquals(7, bookingOutputDtos.size());
        assertEquals(List.of(booking7.getId(), booking8.getId(), booking4.getId(), booking2.getId(), booking1.getId(), booking9.getId(), booking10.getId()),
                bookingOutputDtos.stream().map(BookingOutputDto::getId).collect(Collectors.toList()));

        bookingOutputDtos = bookingService.getAllBookingsByOwnerId(ownerUser.getId(), State.ALL.name(), 7, 7);
        assertEquals(3, bookingOutputDtos.size());
        assertEquals(List.of(booking3.getId(), booking6.getId(), booking5.getId()),
                bookingOutputDtos.stream().map(BookingOutputDto::getId).collect(Collectors.toList()));

        bookingOutputDtos = bookingService.getAllBookingsByOwnerId(ownerUser.getId(), State.WAITING.name(), 0, 10);
        assertEquals(2, bookingOutputDtos.size());
        assertEquals(List.of(booking2.getId(), booking1.getId()),
                bookingOutputDtos.stream().map(BookingOutputDto::getId).collect(Collectors.toList()));

        bookingOutputDtos = bookingService.getAllBookingsByOwnerId(ownerUser.getId(), State.REJECTED.name(), 0, 10);
        assertEquals(2, bookingOutputDtos.size());
        assertEquals(List.of(booking4.getId(), booking3.getId()),
                bookingOutputDtos.stream().map(BookingOutputDto::getId).collect(Collectors.toList()));

        bookingOutputDtos = bookingService.getAllBookingsByOwnerId(ownerUser.getId(), State.PAST.name(), 0, 10);
        assertEquals(4, bookingOutputDtos.size());
        assertEquals(List.of(booking1.getId(), booking3.getId(), booking6.getId(), booking5.getId()),
                bookingOutputDtos.stream().map(BookingOutputDto::getId).collect(Collectors.toList()));

        bookingOutputDtos = bookingService.getAllBookingsByOwnerId(ownerUser.getId(), State.CURRENT.name(), 0, 10);
        assertEquals(3, bookingOutputDtos.size());
        assertEquals(List.of(booking2.getId(), booking9.getId(), booking10.getId()),
                bookingOutputDtos.stream().map(BookingOutputDto::getId).collect(Collectors.toList()));


        bookingOutputDtos = bookingService.getAllBookingsByOwnerId(ownerUser.getId(), State.FUTURE.name(), 0, 10);
        assertEquals(3, bookingOutputDtos.size());
        assertEquals(List.of(booking7.getId(), booking8.getId(), booking4.getId()),
                bookingOutputDtos.stream().map(BookingOutputDto::getId).collect(Collectors.toList())

        );
    }

    private BookingOutputDto createAndApproveBooking(long itemId, LocalDateTime start, LocalDateTime end, long bookerId, long ownerId, boolean approved) {
        BookingOutputDto booking3 = bookingService.createBooking(bookerId, new BookingInputDto(itemId, start, end));
        return bookingService.approveOrRejectBooking(booking3.getId(), ownerId, approved);
    }
}
