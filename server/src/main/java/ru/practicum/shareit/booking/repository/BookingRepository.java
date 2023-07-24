package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findBookingsByBooker_Id(Long bookerId, Pageable pageable);

    Page<Booking> findBookingsByBooker_IdAndStartAfter(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findBookingsByBooker_IdAndEndBefore(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findBookingsByBooker_IdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime nowStart,
                                                                   LocalDateTime nowEnd, Pageable pageable);

    Page<Booking> findBookingsByBooker_IdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findBookingsByItem_IdIn(Collection<Long> items, Pageable pageable);

    Page<Booking> findBookingsByItem_IdInAndStartAfter(Collection<Long> items, LocalDateTime now, Pageable pageable);

    Page<Booking> findBookingsByItem_IdInAndEndBefore(Collection<Long> items, LocalDateTime now, Pageable pageable);

    Page<Booking> findBookingsByItem_IdInAndStartBeforeAndEndAfter(Collection<Long> items, LocalDateTime nowStart,
                                                                   LocalDateTime nowEnd, Pageable pageable);

    Page<Booking> findBookingsByItem_IdInAndStatus(Collection<Long> items, BookingStatus status, Pageable pageable);

    Booking getDistinctTopByItem_IdAndStartBeforeAndStatusNotOrderByEndDesc(Long itemId, LocalDateTime now,
                                                                            BookingStatus status);

    Booking getDistinctTopByItem_IdAndStartAfterAndStatusNotOrderByStartAsc(Long itemId, LocalDateTime now,
                                                                            BookingStatus status);

    boolean existsBookingByBooker_IdAndItem_IdAndStatusAndStartBefore(Long bookerId, Long itemId, BookingStatus status,
                                                                      LocalDateTime now);
}
