package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findBookingById(long bookingId);

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status);

    List<Booking> findAllByBookerIdAndStartGreaterThanAndEndGreaterThanOrderByStartDesc(long bookerId, LocalDateTime localDateTime1, LocalDateTime localDateTime2);

    List<Booking> findAllByBookerIdAndStartLessThanAndEndLessThanOrderByStartDesc(long bookerId, LocalDateTime localDateTime1, LocalDateTime localDateTime2);

    List<Booking> findAllByBookerIdAndStartLessThanAndEndGreaterThanOrderByStartDesc(long bookerId, LocalDateTime localDateTime1, LocalDateTime localDateTime2);

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findAllByItem_OwnerIdAndStatusOrderByStartDesc(long ownerId, Status status);

    List<Booking> findAllByItem_OwnerIdAndStartGreaterThanAndEndGreaterThanOrderByStartDesc(long ownerId, LocalDateTime localDateTime1, LocalDateTime localDateTime2);

    List<Booking> findAllByItem_OwnerIdAndStartLessThanAndEndLessThanOrderByStartDesc(long ownerId, LocalDateTime localDateTime1, LocalDateTime localDateTime2);

    List<Booking> findAllByItem_OwnerIdAndStartLessThanAndEndGreaterThanOrderByStartDesc(long ownerId, LocalDateTime localDateTime1, LocalDateTime localDateTime2);

    Booking findFirstBookingByItem_IdAndStatusAndEndBeforeOrderByEndDesc(long itemId, Status status, LocalDateTime localDateTime);

    Booking findFirstBookingByItem_IdAndStatusAndStartAfterOrderByStartAsc(long itemId, Status status, LocalDateTime localDateTime);

    Booking findFirstByItem_idAndBooker_IdAndEndBefore(long itemId, long bookerId, LocalDateTime localDateTime);
}
