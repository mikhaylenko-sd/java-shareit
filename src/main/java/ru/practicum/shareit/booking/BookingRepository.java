package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findBookingById(long bookingId);

    List<Booking> findAllByBookerId(long bookerId, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatus(long bookerId, Status status, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartGreaterThanAndEndGreaterThan(long bookerId, LocalDateTime localDateTime1, LocalDateTime localDateTime2, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartLessThanAndEndLessThan(long bookerId, LocalDateTime localDateTime1, LocalDateTime localDateTime2, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartLessThanAndEndGreaterThan(long bookerId, LocalDateTime localDateTime1, LocalDateTime localDateTime2, PageRequest pageRequest);

    List<Booking> findAllByItem_OwnerId(long ownerId, PageRequest pageRequest);

    List<Booking> findAllByItem_OwnerIdAndStatus(long ownerId, Status status, PageRequest pageRequest);

    List<Booking> findAllByItem_OwnerIdAndStartGreaterThanAndEndGreaterThan(long ownerId, LocalDateTime localDateTime1, LocalDateTime localDateTime2, PageRequest pageRequest);

    List<Booking> findAllByItem_OwnerIdAndStartLessThanAndEndLessThan(long ownerId, LocalDateTime localDateTime1, LocalDateTime localDateTime2, PageRequest pageRequest);

    List<Booking> findAllByItem_OwnerIdAndStartLessThanAndEndGreaterThan(long ownerId, LocalDateTime localDateTime1, LocalDateTime localDateTime2, PageRequest pageRequest);

    Booking findFirstBookingByItem_IdAndStatusAndEndBeforeOrderByEndDesc(long itemId, Status status, LocalDateTime localDateTime);

    Booking findFirstBookingByItem_IdAndStatusAndStartAfterOrderByStartAsc(long itemId, Status status, LocalDateTime localDateTime);

    Booking findFirstByItem_idAndBooker_IdAndEndBefore(long itemId, long bookerId, LocalDateTime localDateTime);

    Booking findFirstBookingByItem_IdAndStatusAndStartBeforeOrderByEndDesc(long itemId, Status status, LocalDateTime localDateTime);
}
